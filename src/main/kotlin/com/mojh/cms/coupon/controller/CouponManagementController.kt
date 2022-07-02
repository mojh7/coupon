package com.mojh.cms.coupon.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.coupon.dto.request.CreateCouponRequest
import com.mojh.cms.coupon.service.CouponService
import com.mojh.cms.member.entity.Member
import com.mojh.cms.security.member.LoginMember
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/seller/coupons")
class CouponManagementController(
    private val couponService: CouponService
) {
    @PostMapping
    fun createCoupon(@Valid @RequestBody createCouponRequest: CreateCouponRequest,
                     @LoginMember seller: Member
    ): ApiResponse<*> {
        couponService.createCoupon(createCouponRequest, seller)
        return ApiResponse.succeed()
    }

    @PostMapping("/{couponId}/enable")
    fun enableCoupon(@PathVariable couponId: Long, @LoginMember seller: Member): ApiResponse<*> {
        couponService.enable(couponId, seller)
        return ApiResponse.succeed()
    }
}