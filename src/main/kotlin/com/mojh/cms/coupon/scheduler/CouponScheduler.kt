package com.mojh.cms.coupon.scheduler

import com.mojh.cms.coupon.service.MemberCouponService
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CouponScheduler(
    private val memberCouponService: MemberCouponService
) {
    @Value("\${coupon.issue-batch-size}")
    private val BATCH_SIZE: Int = 0;

    @Scheduled(initialDelay = 3000, fixedRate = 1000)
    fun issueMemberCoupon() {
        memberCouponService.issueMemberCoupon(BATCH_SIZE)
    }
}