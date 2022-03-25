package com.mojh.cms.coupon.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.coupon.service.MemberCouponService
import com.mojh.cms.member.entity.Member
import com.mojh.cms.security.LoginMember
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/customer/coupons")
class MemberCouponController(
    val memberCouponService: MemberCouponService
) {
    @GetMapping
    @Secured("ROLE_CUSTOMER")
    fun findAvailableCouponList(@LoginMember customer: Member) =
        ApiResponse.succeed(memberCouponService.findAvailableCouponList(customer))

}