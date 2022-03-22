package com.mojh.cms.coupon.controller

import com.mojh.cms.coupon.service.MemberCouponService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class MemberCouponController(
    val memberCouponService: MemberCouponService
) {

}