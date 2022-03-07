package com.mojh.cms.coupon.repository

import com.mojh.cms.coupon.entity.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponRepository : JpaRepository<Coupon, Long> {
}
