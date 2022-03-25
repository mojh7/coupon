package com.mojh.cms.coupon.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.coupon.service.MemberCouponService
import com.mojh.cms.member.entity.Member
import com.mojh.cms.security.LoginMember
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/customer/coupons")
class MemberCouponController(
    val memberCouponService: MemberCouponService
) {
    @GetMapping
    @Secured("ROLE_CUSTOMER")
    fun findAvailableCouponList(@LoginMember customer: Member) =
        ApiResponse.succeed(memberCouponService.findAvailableCouponList(customer))

    @PostMapping("/{memberCouponId}/use")
    @Secured("ROLE_CUSTOMER")
    fun useCoupon(@PathVariable memberCouponId: Long,
                  @LoginMember customer: Member) =
        ApiResponse.succeed(memberCouponService.useCoupon(memberCouponId, customer))
}