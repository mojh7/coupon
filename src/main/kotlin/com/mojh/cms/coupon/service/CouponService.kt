package com.mojh.cms.coupon.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode
import com.mojh.cms.coupon.dto.request.CreateCouponRequest
import com.mojh.cms.coupon.dto.response.MemberCouponResponse
import com.mojh.cms.coupon.dto.response.toMemberCouponResponse
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
        val coupon = couponRepository.save(createCouponRequest.toEntity(seller))
        return coupon.id
    }

    fun enable(couponId: Long, seller: Member) {
        val coupon = couponRepository.findByIdOrNull(couponId)
            ?: throw CouponApplicationException(ErrorCode.COUPON_DOES_NOT_EXIST)

        redisson.getBucket<Int>(COUPON_COUNT_KEY_PREFIX + couponId).set(coupon.maxCount)

        coupon.enable(seller)
    }


    fun downloadCoupon(couponId: Long, customer: Member): MemberCouponResponse {
        val couponInfo = couponRepository.findByIdOrNull(couponId)
            ?: throw CouponApplicationException(ErrorCode.COUPON_DOES_NOT_EXIST)

        var result: MemberCouponResponse?

        val lock: RLock = redisson.getLock(COUPON_RLOCK_KEY_PREFIX + couponId)

        try {
            if (!lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                LOGGER.info("lock 획득 실패")
                throw CouponApplicationException(ErrorCode.DOWNLOAD_COUPON_TIME_OUT)
            }
            LOGGER.info("lock 획득")

            if (memberCouponRepository.findAllByCustomerIdAndCouponId(customer.id!!, couponId).size >= 1) {
                throw CouponApplicationException(ErrorCode.HAS_ALREADY_DOWNLOADED_COUPON)
            }

            val status = transactionManager.getTransaction(DefaultTransactionDefinition())
            try {
                val couponCountRBucket: RBucket<Int> = redisson.getBucket(COUPON_COUNT_KEY_PREFIX + couponId)
                val couponCount = couponCountRBucket.get()
                if (!couponCountRBucket.isExists || couponCount <= 0) {
                    LOGGER.info("준비된 모든 쿠폰 소진")
                    throw CouponApplicationException(ErrorCode.COUPONS_ARE_EXHAUSTED)
                }

                LOGGER.info("coupon 갯수 : $couponCount")
                couponCountRBucket.set(couponCount - 1)
                val memberCoupon = MemberCoupon.of(customer, couponInfo)
                memberCouponRepository.save(memberCoupon)

                transactionManager.commit(status)
                result = memberCoupon.toMemberCouponResponse()
                LOGGER.info("쿠폰 발급 성공")
            } catch (ex: Exception) {
                transactionManager.rollback(status)
                throw ex
            }
        } catch (ex: InterruptedException) { // thread가 작동 전 interrupted 될 때
            throw CouponApplicationException(ErrorCode.COUPON_DOWNLOAD_FAILED)
        } finally {
            lock.unlock()
            LOGGER.info("lock 반납")
        }

        return result ?: throw CouponApplicationException(ErrorCode.COUPON_DOWNLOAD_FAILED)
    }

    /*
    TODO : 쿠폰 정보 전체 조회
    TODO : 해당 admin이 생성한 쿠폰 정보 전체 조회
    TODO : 해당 쿠폰 정보 조회
     
    TODO : 쿠폰 정보 활성화
    TODO : 쿠폰 정보 비활성화
    TODO : 쿠폰 정보 삭제
     */
}