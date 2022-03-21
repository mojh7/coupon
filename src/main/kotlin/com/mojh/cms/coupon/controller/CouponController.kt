package com.mojh.cms.coupon.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.coupon.dto.CreateCouponRequest
import com.mojh.cms.coupon.dto.MemberCouponResponse
import com.mojh.cms.coupon.service.CouponService
import com.mojh.cms.member.entity.Member
import com.mojh.cms.security.LoginMember
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class CouponController(
    private val couponService: CouponService
) {

    @PostMapping("/coupons")
    @Secured("ROLE_SELLER")
    fun createCoupon(@Valid @RequestBody createCouponRequest: CreateCouponRequest,
                     @LoginMember seller: Member) {
        couponService.createCoupon(createCouponRequest, seller)
    }
}