package com.mojh.cms.coupon.service

import com.mojh.cms.coupon.dto.CreateCouponInfoRequest
import com.mojh.cms.coupon.repository.CouponInfoRepository
import com.mojh.cms.member.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CouponInfoService(
    private val couponInfoRepository: CouponInfoRepository
) {

    @Transactional
    fun createCouponInfo(couponInfoRequest: CreateCouponInfoRequest, admin: Member) {
        couponInfoRepository.save(couponInfoRequest.toEntity(admin))
    }

    /*
    TODO : 쿠폰 정보 전체 조회
    TODO : 해당 admin이 생성한 쿠폰 정보 전체 조회
    TODO : 해당 쿠폰 정보 조회
     
    TODO : 쿠폰 정보 활성화
    TODO : 쿠폰 정보 비활성화
    TODO : 쿠폰 정보 삭제
     */
}