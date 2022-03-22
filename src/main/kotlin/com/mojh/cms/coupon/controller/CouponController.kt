package com.mojh.cms.coupon.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.coupon.dto.CreateCouponRequest
import com.mojh.cms.coupon.dto.MemberCouponResponse
import com.mojh.cms.coupon.service.CouponService
import com.mojh.cms.member.entity.Member
import com.mojh.cms.security.LoginMember
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/coupons")
class CouponController(
    private val couponService: CouponService
) {

    @PostMapping()
    @Secured("ROLE_SELLER")
    fun createCoupon(@Valid @RequestBody createCouponRequest: CreateCouponRequest,
                     @LoginMember seller: Member): ApiResponse<*> {
        couponService.createCoupon(createCouponRequest, seller)
        return ApiResponse.succeed()
    }

    @PostMapping("/{couponId}/enable")
    @Secured("ROLE_SELLER")
    fun enableCoupon(@PathVariable couponId: Long, @LoginMember seller: Member): ApiResponse<*> {
        couponService.enable(couponId, seller)
        return ApiResponse.succeed()
    }


    @PostMapping("/{couponId}/download")
    @Secured("ROLE_CUSTOMER")
    fun downloadCoupon(@PathVariable couponId: Long, @LoginMember customer: Member): ApiResponse<MemberCouponResponse> {
        return ApiResponse.succeed(couponService.downloadCoupon(couponId, customer))
    }

}