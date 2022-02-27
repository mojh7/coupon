package com.mojh.cms.coupon.repository

import com.mojh.cms.coupon.entity.CouponInfo
import org.springframework.data.jpa.repository.JpaRepository

interface CouponInfoRepository : JpaRepository<CouponInfo, Long> {
}
