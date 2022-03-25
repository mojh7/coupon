package com.mojh.cms.coupon.service

import com.mojh.cms.coupon.dto.response.MemberCouponResponse
import com.mojh.cms.coupon.repository.MemberCouponRepository
import com.mojh.cms.member.entity.Member
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MemberCouponService(
    private val memberCouponRepository: MemberCouponRepository,
) {

    fun findAvailableCouponList(customer: Member): List<MemberCouponResponse> {
        val now: LocalDateTime = LocalDateTime.now()
        return memberCouponRepository.findAllByCustomerId(customer.id!!)
            .filter { it.isAvailable(now) }
            .map { MemberCouponResponse.from(it) }
    }

/*
    TODO : 사용가능한 쿠폰 조회
    TODO : 사용 및 만료된 쿠폰 조회
    TODO : 해당 쿠폰 조회
    TODO : 쿠폰 사용
     */
}