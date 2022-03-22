package com.mojh.cms.coupon.dto.response

import com.mojh.cms.coupon.entity.MemberCoupon
import java.time.LocalDateTime

data class MemberCouponResponse(
    val title: String,
    val description: String,
    val status: MemberCoupon.Status,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
)

fun MemberCoupon.toMemberCouponResponse() = MemberCouponResponse(
    title = this.coupon.title,
    description = this.coupon.description,
    status = this.status,
    startAt = this.coupon.startAt,
    endAt = this.coupon.endAt
)