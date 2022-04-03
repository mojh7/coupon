package com.mojh.cms.coupon.dto.response

import com.mojh.cms.coupon.entity.MemberCoupon
import java.time.LocalDateTime

data class MemberCouponResponse private constructor(
    val title: String,
    val description: String,
    val status: MemberCoupon.Status,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
) {
    companion object {
        fun from(memberCoupon: MemberCoupon) = MemberCouponResponse(
            title = memberCoupon.coupon.title,
            description = memberCoupon.coupon.description,
            status = memberCoupon.status,
            startAt = memberCoupon.coupon.availablePeriod.startAt,
            endAt = memberCoupon.coupon.availablePeriod.endAt
        )
    }
}
