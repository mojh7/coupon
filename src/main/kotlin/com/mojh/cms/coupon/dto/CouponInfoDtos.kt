package com.mojh.cms.coupon.dto

import com.mojh.cms.coupon.entity.CouponInfo
import com.mojh.cms.member.entity.Member
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class CreateCouponInfoRequest(
    @field:NotBlank
    @field:Size(max = 32)
    val name: String,

    @field:Size(max = 64)
    val description: String = "",

    @field:Size(min = 0)
    val maxCount: Int,

    @field:NotNull
    val startAt: LocalDateTime,

    @field:NotNull
    val endAt: LocalDateTime
) {
    fun toCouponInfo(admin: Member) = CouponInfo(
        admin = admin,
        name = this.name,
        description = this.description,
        maxCount = this.maxCount,
        startAt = this.startAt,
        endAt = this.endAt
    )
}

data class CouponInfoResponse(
    val name: String,
    val description: String,
    val maxCount: Int,
    val status: CouponInfo.Status,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
) {
    constructor(couponInfo: CouponInfo): this(
        name = couponInfo.name,
        description = couponInfo.description,
        maxCount = couponInfo.maxCount,
        status = couponInfo.status,
        startAt = couponInfo.startAt,
        endAt = couponInfo.endAt
    )
}