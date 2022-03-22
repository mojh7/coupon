package com.mojh.cms.coupon.service

import com.mojh.cms.common.exception.CustomException
import com.mojh.cms.common.exception.ErrorCode
import com.mojh.cms.coupon.dto.CreateCouponRequest
import com.mojh.cms.coupon.repository.CouponRepository
import com.mojh.cms.member.entity.Member
import org.redisson.api.RedissonClient
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val redisson: RedissonClient
) {

    @Transactional
    fun createCoupon(createCouponRequest: CreateCouponRequest, seller: Member): Long? {
        val coupon = couponRepository.save(createCouponRequest.toEntity(seller))
        return coupon.id
    }

    fun enable(couponId: Long, seller: Member) {
        val coupon = couponRepository.findByIdOrNull(couponId)
            ?: throw CustomException(ErrorCode.COUPON_DOES_NOT_EXIST)

        redisson.getBucket<Int>(MemberCouponService.COUPON_COUNT_KEY_PREFIX + couponId).set(coupon.maxCount)

        coupon.enable(seller)
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