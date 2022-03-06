package com.mojh.cms.coupon.dto

import com.mojh.cms.coupon.entity.Coupon
import java.time.LocalDateTime

data class CouponResponse private constructor(
    val name: String,
    val description: String,
    val status: Coupon.Status,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
) {
    companion object {
        fun from(coupon: Coupon) =
            coupon.couponInfo.let {
                CouponResponse(it.name, it.description, coupon.status, it.startAt, it.endAt)
            }
    }
}