package com.mojh.cms.coupon.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode
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

    @Transactional
    fun createCoupon(createCouponRequest: CreateCouponRequest, seller: Member): Long? {
        val coupon = couponRepository.save(createCouponRequest.toCoupon(seller))
        return coupon.id
    }

    fun enable(couponId: Long, seller: Member) {
        val couponInfo = couponRepository.findByIdOrNull(couponId)
            ?: throw CouponApplicationException(ErrorCode.COUPON_DOES_NOT_EXIST)
        couponInfo.enable(seller)
        val couponRedis = CouponRedis.from(couponInfo)

        couponRedisRepository.save(couponRedis)
        couponRepository.save(couponInfo)
    }

    fun getActuallyIssuedCouponCount(couponId: Long) =
        memberCouponRepository.countByCouponId(couponId)

    fun downloadCoupon(couponId: Long, customer: Member): Boolean {
        val script = """
            local coupon_id = KEYS[1]
            local coupon_key = 'coupon:'..coupon_id
            local coupon_downloaders_key = KEYS[2]..':'..coupon_id
            local coupon_issuance_queue_key = KEYS[3]
            local customer_id = ARGV[1]
            local enabled = ARGV[2]
            local now = tonumber(ARGV[3])
            local coupon_issuance_queue_value = coupon_id..':'..customer_id
            local COUPON_NOT_ENABLED = ARGV[4]
            local COUPON_ISSUE_PERIOD_INVALID = ARGV[5]
            local COUPON_EXHAUSTED = ARGV[6]
            local ALREADY_DOWNLOADED_COUPON = ARGV[7]

            -- 쿠폰 발급 가능 여부 확인
            -- 활성화 상태인지 확인
            local status = redis.call('HGET', coupon_key, 'status')
            if status ~= enabled then
              return COUPON_NOT_ENABLED
            end

            -- 발급 가능 시간 비교
            local start_at = tonumber(redis.call('HGET', coupon_key, 'startAt'))
            local end_at = tonumber(redis.call('HGET', coupon_key, 'endAt'))
            if (now < start_at or now > end_at) then
              return COUPON_ISSUE_PERIOD_INVALID
            end

            -- 개수 확인
            local max_count = tonumber(redis.call('HGET', coupon_key, 'maxCount'))
            local curr_count = tonumber(redis.call('SCARD', coupon_downloaders_key))
            if (curr_count >= max_count) then
              return COUPON_EXHAUSTED
            end

            -- 중복 발급 여부 확인
            if (redis.call('SISMEMBER', coupon_downloaders_key, customer_id) == 1) then
              return ALREADY_DOWNLOADED_COUPON
            end

            -- 쿠폰 발급 요청 성공한 유저 목록에 추가
            redis.call('SADD', coupon_downloaders_key, customer_id)
            -- 쿠폰 발급 요청 대기 큐에 쿠폰과 유저 id 정보 추가
            redis.call('ZADD', coupon_issuance_queue_key, now, coupon_issuance_queue_value)

            return 'SUCCESS'
        """.trimIndent()


        val now = Instant.now().toEpochMilli().toString()
        val scriptResult = redisTemplate.execute(
            RedisScript.of(script, String::class.java),
            listOf(couponId.toString(), COUPON_DOWNLOADERS_KEY_PREFIX, COUPON_ISSUANCE_QUEUE_KEY),
            customer.id.toString(), Coupon.Status.ENABLED.toString(), now,
            ErrorCode.COUPON_NOT_ENABLED.name, ErrorCode.COUPON_ISSUE_PERIOD_INVALID.name,
            ErrorCode.COUPON_EXHAUSTED.name, ErrorCode.ALREADY_DOWNLOADED_COUPON.name
        )

        if (scriptResult != "SUCCESS") {
            throw CouponApplicationException(ErrorCode.valueOf(scriptResult))
        }

        LOGGER.info("couponId=$couponId, memberId=$customer.id res=$scriptResult")

        return true;
    }
}