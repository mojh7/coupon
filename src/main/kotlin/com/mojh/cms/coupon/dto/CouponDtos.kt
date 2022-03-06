package com.mojh.cms.coupon.dto

import com.mojh.cms.coupon.entity.Coupon
import com.mojh.cms.member.entity.Member
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class CreateCouponRequest(
    @field:NotBlank
    @field:Size(max = 64)
    val title: String,

    @field:Size(max = 64)
    val description: String = "",

    @field:Size(min = 0)
    val maxCount: Int,

    @field:NotNull
    val startAt: LocalDateTime,

    @field:NotNull
    val endAt: LocalDateTime
) {
    fun toEntity(admin: Member) = Coupon(
        seller = admin,
        title = this.title,
        description = this.description,
        maxCount = this.maxCount,
        startAt = this.startAt,
        endAt = this.endAt
    )
}

data class CouponResponse(
    val title: String,
    val description: String,
    val maxCount: Int,
    val status: Coupon.Status,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
) {
    constructor(coupon: Coupon): this(
        title = coupon.title,
        description = coupon.description,
        maxCount = coupon.maxCount,
        status = coupon.status,
        startAt = coupon.startAt,
        endAt = coupon.endAt
    )
}