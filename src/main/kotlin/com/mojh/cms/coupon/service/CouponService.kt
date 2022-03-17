package com.mojh.cms.coupon.service

import com.mojh.cms.common.exception.CustomException
import com.mojh.cms.common.exception.ErrorCode.COUPON_NOT_FOUND
import com.mojh.cms.coupon.dto.CreateCouponRequest
import com.mojh.cms.coupon.dto.MemberCouponResponse
import com.mojh.cms.coupon.entity.MemberCoupon
import com.mojh.cms.coupon.repository.CouponRepository
import com.mojh.cms.coupon.repository.MemberCouponRepository
import com.mojh.cms.member.entity.Member
import com.mojh.cms.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val memberCouponRepository: MemberCouponRepository,
) {

    @Transactional
    fun createCoupon(couponRequest: CreateCouponRequest, seller: Member) {
        couponRepository.save(couponRequest.toEntity(seller))
    }

    @Transactional
    fun downloadCoupon(couponInfoId: Long, customer: Member): MemberCouponResponse {
        val couponInfo = couponRepository.findByIdOrNull(couponInfoId)
            ?: throw CustomException(COUPON_NOT_FOUND)

        val coupon = MemberCoupon(customer, couponInfo)
        memberCouponRepository.save(coupon)
        return MemberCouponResponse.from(coupon)
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