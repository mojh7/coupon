package com.mojh.cms.coupon.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.coupon.service.MemberCouponService
import com.mojh.cms.member.entity.Member
import com.mojh.cms.security.member.LoginMember
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/customer/coupons")
class MemberCouponController(
    val memberCouponService: MemberCouponService
) {
    @GetMapping
    fun findAvailableCouponList(@LoginMember customer: Member) =
        ApiResponse.succeed(memberCouponService.findAllAvailableCoupons(customer))

    @PostMapping("/{memberCouponId}/use")
    fun useCoupon(@PathVariable memberCouponId: Long, @LoginMember customer: Member) =
        ApiResponse.succeed(memberCouponService.useCoupon(memberCouponId, customer))
}