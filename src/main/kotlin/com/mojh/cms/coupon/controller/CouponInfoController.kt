package com.mojh.cms.coupon.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.coupon.dto.MemberCouponResponse
import com.mojh.cms.coupon.dto.CreateCouponInfoRequest
import com.mojh.cms.coupon.service.CouponInfoService
import com.mojh.cms.member.entity.Member
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class CouponInfoController(
    private val couponInfoService: CouponInfoService
) {

    @PostMapping("/coupon-info")
    fun createCouponInfo(@Valid @RequestBody createCouponInfoRequest: CreateCouponInfoRequest, admin: Member) {
        couponInfoService.createCouponInfo(createCouponInfoRequest, admin)
    }

    @PostMapping("/coupons/{couponInfoId}/download")
//    fun downloadCoupon(@PathVariable couponInfoId: Long, customer: Member): ApiResponse<CouponResponse> {
    fun downloadCoupon(@PathVariable couponInfoId: Long): ApiResponse<MemberCouponResponse> {
        return ApiResponse.succeed(couponInfoService.downloadCoupon(couponInfoId))
    }
}