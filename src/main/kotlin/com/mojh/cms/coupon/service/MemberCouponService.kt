package com.mojh.cms.coupon.service

import com.mojh.cms.coupon.repository.CouponRepository
import com.mojh.cms.coupon.repository.MemberCouponRepository
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager

@Service
class MemberCouponService(
    private val couponRepository: CouponRepository,
    private val memberCouponRepository: MemberCouponRepository,
    private val redisson: RedissonClient,
    private val transactionManager: PlatformTransactionManager
) {

/*
    TODO : 사용가능한 쿠폰 조회
    TODO : 사용 및 만료된 쿠폰 조회
    TODO : 해당 쿠폰 조회
    TODO : 쿠폰 사용
     */
}