package com.mojh.cms.coupon.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.coupon.dto.response.MemberCouponResponse
import com.mojh.cms.coupon.service.CouponService
import com.mojh.cms.member.entity.Member
import com.mojh.cms.security.member.LoginMember
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coupons")
class CouponController(
    private val couponService: CouponService
) {
    @PostMapping("/{couponId}/download")
    @Secured("ROLE_CUSTOMER")
    fun downloadCoupon(@PathVariable couponId: Long, @LoginMember customer: Member): ApiResponse<MemberCouponResponse> {
        return ApiResponse.succeed(couponService.downloadCoupon(couponId, customer))
    }
}