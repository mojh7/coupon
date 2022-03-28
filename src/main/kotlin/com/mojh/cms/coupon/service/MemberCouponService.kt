package com.mojh.cms.coupon.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode.CUSTOMER_COUPON_DOES_NOT_EXIST
import com.mojh.cms.coupon.dto.response.MemberCouponResponse
import com.mojh.cms.coupon.repository.MemberCouponRepository
import com.mojh.cms.member.entity.Member
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberCouponService(
    private val memberCouponRepository: MemberCouponRepository,
) {

    fun findAvailableCouponList(customer: Member): List<MemberCouponResponse> {
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
}