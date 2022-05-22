package com.mojh.cms.coupon.dto.request

import com.mojh.cms.common.embeddable.Period
import com.mojh.cms.coupon.entity.Coupon
import com.mojh.cms.member.entity.Member
import java.time.LocalDateTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CreateCouponRequest(
    @field:NotBlank
    @field:Size(max = 64)
    val title: String,

    @field:Size(max = 64)
    val description: String = "",

    @field:Min(value = 1)
    val maxCount: Int,

    val startAt: LocalDateTime,

    val endAt: LocalDateTime
) {
    fun toCoupon(seller: Member) = Coupon(
        seller = seller,
        title = this.title,
        description = this.description,
        maxCount = this.maxCount,
        availablePeriod = Period(this.startAt, this.endAt)
    )
}