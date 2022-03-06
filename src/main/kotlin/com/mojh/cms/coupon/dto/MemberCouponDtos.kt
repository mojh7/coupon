package com.mojh.cms.coupon.dto

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
        fun from(memberCoupon: MemberCoupon) =
            memberCoupon.coupon.let {
                MemberCouponResponse(it.title, it.description, memberCoupon.status, it.startAt, it.endAt)
            }
    }
}