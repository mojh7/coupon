package com.mojh.cms.coupon.service

import com.mojh.cms.common.exception.CustomException
import com.mojh.cms.common.exception.ErrorCode.*
import com.mojh.cms.coupon.dto.MemberCouponResponse
import com.mojh.cms.coupon.entity.MemberCoupon
import com.mojh.cms.coupon.repository.CouponRepository
import com.mojh.cms.coupon.repository.MemberCouponRepository
import com.mojh.cms.member.entity.Member
import org.apache.logging.log4j.LogManager
import org.redisson.api.RBucket
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.util.concurrent.TimeUnit

@Service
class MemberCouponService(
    private val couponRepository: CouponRepository,
    private val memberCouponRepository: MemberCouponRepository,
    private val redisson: RedissonClient,
    private val transactionManager: PlatformTransactionManager
) {
    companion object {
        private val LOGGER = LogManager.getLogger()
        const val COUPON_RLOCK_KEY_PREFIX = "CRL:"
        const val COUPON_COUNT_KEY_PREFIX = "CC:"
    }

    fun downloadCoupon(couponInfoId: Long, customer: Member): MemberCouponResponse {
        val couponInfo = couponRepository.findByIdOrNull(couponInfoId)
            ?: throw CustomException(COUPON_DOES_NOT_EXIST)

        memberCouponRepository.findMemberIdAndCouponId(customer.id!!, couponInfoId)?.let{
            CustomException(HAS_ALREADY_DOWNLOADED_COUPON)
        }

        var result: MemberCouponResponse? = null

        val lock: RLock = redisson.getLock(COUPON_RLOCK_KEY_PREFIX + couponInfoId)

        try {
            if (!lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                LOGGER.info("lock 획득 실패")
                throw CustomException(DOWNLOAD_COUPON_TIME_OUT)
            }
            LOGGER.info("lock 획득")

            val status = transactionManager.getTransaction(DefaultTransactionDefinition())
            try {
                val couponCountRBucket: RBucket<Int> = redisson.getBucket(COUPON_COUNT_KEY_PREFIX + couponInfoId)
                val couponCount = couponCountRBucket.get()
                if (!couponCountRBucket.isExists || couponCount <= 0) {
                    LOGGER.info("준비된 모든 쿠폰 소진")
                    throw CustomException(COUPONS_ARE_EXHAUSTED)
                }
                couponCountRBucket.set(couponCount - 1)

                val memberCoupon = MemberCoupon.of(customer, couponInfo)
                memberCouponRepository.save(memberCoupon)
                transactionManager.commit(status)
                result = MemberCouponResponse.from(memberCoupon)
                LOGGER.info("쿠폰 발급 성공")
            } catch (ex: Exception) {
                transactionManager.rollback(status)
                throw ex
            }
        } catch (ex: InterruptedException) { // thread가 작동 전 interrupted 될 때
            throw CustomException(COUPON_DOWNLOAD_FAILED)
        } finally {
            lock.unlock()
            LOGGER.info("lock 반납")
        }

        return result ?: throw CustomException(COUPON_DOWNLOAD_FAILED)
    }


/*
    TODO : 사용가능한 쿠폰 조회
    TODO : 사용 및 만료된 쿠폰 조회
    TODO : 해당 쿠폰 조회 
    TODO : 쿠폰 사용
     */
}