package com.mojh.cms.coupon.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode.CUSTOMER_COUPON_DOES_NOT_EXIST
import com.mojh.cms.coupon.dto.response.MemberCouponResponse
import com.mojh.cms.coupon.entity.MemberCoupon
import com.mojh.cms.coupon.repository.MemberCouponRepository
import com.mojh.cms.member.entity.Member
import com.mojh.cms.member.service.MemberService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MemberCouponService(
    private val memberCouponRepository: MemberCouponRepository,
    private val couponService: CouponService,
    private val memberService: MemberService,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val getIssuableCouponScript: RedisScript<List<String>>

) {
    @Value("\${coupon.issue-queue-key}")
    private lateinit var COUPON_ISSUE_QUEUE_KEY: String

    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    fun findAllAvailableCoupons(customer: Member): List<MemberCouponResponse> {
        return memberCouponRepository.findAllByCustomerId(customer.id!!)
            .filter { it.isAvailable() }
            .map { MemberCouponResponse.from(it) }
    }

    @Transactional
    fun useCoupon(memberCouponId: Long, customer: Member) {
        val memberCoupon = memberCouponRepository.findByIdOrNull(memberCouponId)
            ?: throw CouponApplicationException(CUSTOMER_COUPON_DOES_NOT_EXIST)
        memberCoupon.use(customer)
    }

    fun issueMemberCoupon(batchSize: Int) {
        val issuableCoupons: List<String> = redisTemplate.execute(getIssuableCouponScript,
            listOf(COUPON_ISSUE_QUEUE_KEY), batchSize.toString())

        issuableCoupons.map { issueInfo ->
            val (couponId, memberId) = issueInfo.split(" ").map { it.toLong() }
            MemberCoupon(coupon = couponService.getById(couponId), customer = memberService.getById(memberId))
        }.let {
            memberCouponRepository.saveAll(it)
        }
    }
}