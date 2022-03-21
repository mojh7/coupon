package com.mojh.cms.coupon.repository

import com.mojh.cms.coupon.entity.MemberCoupon
import org.springframework.data.jpa.repository.JpaRepository

interface MemberCouponRepository : JpaRepository<MemberCoupon, Long> {
    fun findMemberIdAndCouponId(memberId: Long, couponId: Long): MemberCoupon?
}