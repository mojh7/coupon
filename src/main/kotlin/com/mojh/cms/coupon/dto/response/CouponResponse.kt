package com.mojh.cms.coupon.dto.response

import com.mojh.cms.coupon.entity.Coupon
import java.time.LocalDateTime

data class CouponResponse(
    val title: String,
    val description: String,
    val maxCount: Int,
    val status: Coupon.Status,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
)

fun Coupon.toCouponResponse() = CouponResponse(
    title = this.title,
    description = this.description,
    maxCount = this.maxCount,
    status = this.status,
    startAt = this.startAt,
    endAt = this.endAt
)