package com.mojh.cms.coupon.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode
import com.mojh.cms.common.exception.ErrorCode.*
import com.mojh.cms.coupon.dto.request.CreateCouponRequest
import com.mojh.cms.coupon.entity.Coupon
import com.mojh.cms.coupon.entity.CouponRedis
import com.mojh.cms.coupon.repository.CouponRedisRepository
import com.mojh.cms.coupon.repository.CouponRepository
import com.mojh.cms.coupon.repository.MemberCouponRepository
import com.mojh.cms.member.entity.Member
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val memberCouponRepository: MemberCouponRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val couponRedisRepository: CouponRedisRepository,
) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    @Value("\${coupon.downloaders-key-prefix}")
    private val COUPON_DOWNLOADERS_KEY_PREFIX: String = ""

    @Value("\${coupon.coupon-issuance-queue-key}")
    private val COUPON_ISSUANCE_QUEUE_KEY: String = ""

    @Value("\${classpath:lua-scripts/download-coupon.lua}")
    private val DOWNLOAD_COUPON_SCRIPT_RESOURCE: String? = null

    private val DOWNLOAD_COUPON_SCRIPT: String by lazy {
        DOWNLOAD_COUPON_SCRIPT_RESOURCE?.takeIf { it.isNotBlank() }
            ?.trimIndent()
            ?: throw CouponApplicationException(COUPON_DOWNLOAD_FAILED)
    }

    @Transactional
    fun createCoupon(createCouponRequest: CreateCouponRequest, seller: Member): Long? {
        val coupon = couponRepository.save(createCouponRequest.toCoupon(seller))
        return coupon.id
    }

    fun enable(couponId: Long, seller: Member) {
        val couponInfo = couponRepository.findByIdOrNull(couponId)
            ?: throw CouponApplicationException(COUPON_DOES_NOT_EXIST)
        couponInfo.enable(seller)
        val couponRedis = CouponRedis.from(couponInfo)

        couponRedisRepository.save(couponRedis)
        couponRepository.save(couponInfo)
    }

    fun getActuallyIssuedCouponCount(couponId: Long) =
        memberCouponRepository.countByCouponId(couponId)

    fun downloadCoupon(couponId: Long, customer: Member): Boolean {
        val now = Instant.now().toEpochMilli().toString()
        val scriptResult = redisTemplate.execute(
            RedisScript.of(DOWNLOAD_COUPON_SCRIPT, String::class.java),
            listOf(couponId.toString(), COUPON_DOWNLOADERS_KEY_PREFIX, COUPON_ISSUANCE_QUEUE_KEY),
            customer.id.toString(), Coupon.Status.ENABLED.toString(), now,
            COUPON_NOT_ENABLED.name, COUPON_ISSUE_PERIOD_INVALID.name,
            COUPON_EXHAUSTED.name, ALREADY_DOWNLOADED_COUPON.name
        )

        if (scriptResult != "SUCCESS") {
            throw CouponApplicationException(ErrorCode.valueOf(scriptResult))
        }

        LOGGER.info("couponId=$couponId, memberId=$customer.id res=$scriptResult")

        return true;
    }
}