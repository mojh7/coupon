package com.mojh.cms.coupon.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode
import com.mojh.cms.coupon.dto.request.CreateCouponRequest
import com.mojh.cms.coupon.dto.response.MemberCouponResponse
import com.mojh.cms.coupon.entity.MemberCoupon
import com.mojh.cms.coupon.repository.CouponRepository
import com.mojh.cms.coupon.repository.MemberCouponRepository
import com.mojh.cms.member.entity.Member
import org.apache.logging.log4j.LogManager
import org.redisson.api.RBucket
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.util.concurrent.TimeUnit

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val memberCouponRepository: MemberCouponRepository,
    private val redisson: RedissonClient,
    private val transactionManager: PlatformTransactionManager
) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    @Value("\${coupon.lock-key-prefix}")
    private val COUPON_RLOCK_KEY_PREFIX: String = ""

    @Value("\${coupon.count-key-prefix}")
    private val COUPON_COUNT_KEY_PREFIX: String = ""

    @Transactional
    fun createCoupon(createCouponRequest: CreateCouponRequest, seller: Member): Long? {
        val coupon = couponRepository.save(createCouponRequest.toCoupon(seller))
        return coupon.id
    }

    fun enable(couponId: Long, seller: Member) {
        val couponInfo = couponRepository.findByIdOrNull(couponId)
            ?: throw CouponApplicationException(ErrorCode.COUPON_DOES_NOT_EXIST)

        redisson.getBucket<Int>("${COUPON_COUNT_KEY_PREFIX}${couponId}").set(couponInfo.maxCount)

        couponInfo.enable(seller)
        couponRepository.save(couponInfo)
    }

    fun getActuallyDeployedCouponCount(couponId: Long) =
        memberCouponRepository.countByCouponId(couponId)

    fun getCouponCountInRedis(couponId: Long) =
        redisson.getBucket<Int>("${COUPON_COUNT_KEY_PREFIX}${couponId}").get()

    fun downloadCoupon(couponId: Long, customer: Member): MemberCouponResponse {
        val threadInfo = "[thread-${Thread.currentThread().id}]"

        LOGGER.info("${threadInfo} couponId: ${couponId}, memberId: ${customer.id!!} 쿠폰 발급 시도")
        val couponInfo = couponRepository.findByIdOrNull(couponId)
            ?: throw CouponApplicationException(ErrorCode.COUPON_DOES_NOT_EXIST)

        if (!couponInfo.isAvailable()) {
            throw CouponApplicationException(ErrorCode.UNABLE_DOWNLOAD_COUPON)
        }

        var result: MemberCouponResponse?

        val lock: RLock = redisson.getLock("${COUPON_RLOCK_KEY_PREFIX}${couponId}")

        LOGGER.info("${threadInfo} lock 획득 시도")

        /**
         * tryLock은 lock 획득 여부에 따라 boolean으로 리턴되는데
         * 만약 try 구문 안에 포함시켜 버리면 lock 획득 실패하고 finally에서 lock을 획득했던 thread와
         * 다른 thread에서 unlock 요청이 될 수 있어 에러가 발생
         * 그래서 try 전에 lock 획득 시도하도록 변경
         **/
        if (!lock.tryLock(30, 3, TimeUnit.SECONDS)) {
            LOGGER.info("${threadInfo} lock 획득 실패")
            throw CouponApplicationException(ErrorCode.DOWNLOAD_COUPON_TIME_OUT)
        }

        try {
            LOGGER.info("${threadInfo} lock 획득")

            if (memberCouponRepository.findAllByCustomerIdAndCouponId(customer.id!!, couponId).size >= 1) {
                throw CouponApplicationException(ErrorCode.HAS_ALREADY_DOWNLOADED_COUPON)
            }

            val status = transactionManager.getTransaction(DefaultTransactionDefinition())
            try {
                val couponCountRBucket: RBucket<Int> = redisson.getBucket(COUPON_COUNT_KEY_PREFIX + couponId)
                val couponCount = couponCountRBucket.get()
                if (!couponCountRBucket.isExists || couponCount <= 0) {
                    LOGGER.info("${threadInfo} 준비된 모든 쿠폰 소진")
                    throw CouponApplicationException(ErrorCode.RUN_OUT_OF_COUPONS)
                }

                LOGGER.info("${threadInfo} coupon 잔여 갯수 : $couponCount")
                couponCountRBucket.set(couponCount - 1)
                val memberCoupon = MemberCoupon.of(customer, couponInfo)
                memberCouponRepository.save(memberCoupon)

                /**
                 * 만약 lock을 획득한 후 어떠한 이유로 lease time이 넘은 시간 동안 로직이 지연되고 나서
                 * 완료되어 DB에 commit하려 할 때 이미 lock이 lease time이 넘어가 자동으로 해제된 상황
                 * 그래서 다른 스레드가 접근할 수 있는 상황에서 우연히 연속으로 같은 request가 들어오면
                 * 중복 처리 되어 쿠폰이 제한 갯수보다 많이 발급 될 수 있기에 커밋 직전에 lock에 대해 검사
                 */
                if (!lock.isHeldByCurrentThread) {
                    LOGGER.warn("${threadInfo} lease time이 되기전 로직이 성공하지 못함")
                    throw CouponApplicationException(ErrorCode.COUPON_DOWNLOAD_FAILED)
                }
                transactionManager.commit(status)
                result = MemberCouponResponse.from(memberCoupon)
                LOGGER.info("${threadInfo} couponId: ${couponId}, memberId: ${customer.id!!} 쿠폰 발급 성공")
            } catch (ex: Exception) {
                transactionManager.rollback(status)
                throw ex
            }
        } catch (ex: InterruptedException) {
            LOGGER.warn(ex)
            throw CouponApplicationException(ErrorCode.COUPON_DOWNLOAD_FAILED)
        } finally {
            // lock이 존재하고 해당 thread에 의해 잠긴 경우 unlock
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
                LOGGER.info("${threadInfo} lock 반납 성공")
            }
        }

        return result ?: throw CouponApplicationException(ErrorCode.COUPON_DOWNLOAD_FAILED)
    }
}