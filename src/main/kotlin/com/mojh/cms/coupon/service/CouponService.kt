package com.mojh.cms.coupon.service

import com.mojh.cms.common.exception.NotFoundException
import com.mojh.cms.coupon.dto.MemberCouponResponse
import com.mojh.cms.coupon.dto.CreateCouponRequest
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
    private val memberRepository: MemberRepository
) {

    @Transactional
    fun createCoupon(couponRequest: CreateCouponRequest, admin: Member) {
        couponRepository.save(couponRequest.toEntity(admin))
    }

    @Transactional
    fun downloadCoupon(couponInfoId: Long): MemberCouponResponse {
        val couponInfo = couponRepository.findByIdOrNull(couponInfoId)
            ?: throw NotFoundException("해당 쿠폰 정보를 찾을 수 없습니다.")

        // security 추가하면 빠질 코드
        val customer = memberRepository.findByIdOrNull(1L) ?: throw NotFoundException("해당 member를 찾을 수 없습니다.")

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