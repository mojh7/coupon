package com.mojh.cms.coupon.dto.response

import com.mojh.cms.coupon.entity.Coupon
import java.time.LocalDateTime

data class CouponResponse private constructor(
    val title: String,
    val description: String,
    val maxCount: Int,
    val status: Coupon.Status,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
) {
    companion object {
        fun from(coupon: Coupon) = CouponResponse(
            title = coupon.title,
            description = coupon.description,
            maxCount = coupon.maxCount,
            status = coupon.status,
            startAt = coupon.availablePeriod.startAt,
            endAt = coupon.availablePeriod.endAt
        )
    }
}