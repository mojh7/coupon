package com.mojh.cms.coupon.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.coupon.dto.MemberCouponResponse
import com.mojh.cms.coupon.service.MemberCouponService
import com.mojh.cms.member.entity.Member
import com.mojh.cms.security.LoginMember
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberCouponController(
    val memberCouponService: MemberCouponService
) {


    @PostMapping("/coupons/{couponInfoId}/download")
    @Secured("ROLE_CUSTOMER")
    fun downloadCoupon(@PathVariable couponInfoId: Long,
                       @LoginMember customer: Member
    ): ApiResponse<MemberCouponResponse> {
        return ApiResponse.succeed(memberCouponService.downloadCoupon(couponInfoId, customer))
    }
}