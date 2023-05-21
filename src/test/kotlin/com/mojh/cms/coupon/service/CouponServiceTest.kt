/*
package com.mojh.cms.coupon.service

import com.mojh.cms.common.UnitTest
import com.mojh.cms.coupon.dto.request.CreateCouponRequest
import com.mojh.cms.coupon.repository.CouponRepository
import com.mojh.cms.coupon.repository.MemberCouponRepository
import com.mojh.cms.member.entity.Member
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDateTime
import java.time.Month

@UnitTest
internal class CouponServiceTest : BehaviorSpec() {
    companion object {
        private val SELLER = Member("sellerId", "password", Member.Role.ROLE_SELLER)
    }

    init {
        val couponRepository = mockk<CouponRepository>()
        val memberCouponRepository = mockk<MemberCouponRepository>()
        val redisson = mockk<RedissonClient>()
        val transactionManager = mockk<PlatformTransactionManager>()
        val couponService = CouponService(couponRepository, memberCouponRepository, redisson, transactionManager)

        isolationMode = IsolationMode.InstancePerTest

        given("유효한 쿠폰 정보로") {
            val createCouponRequest = CreateCouponRequest(
                "쿠폰 이름",
                "쿠폰 설명",
                maxCount = 100,
                startAt = LocalDateTime.of(2022, Month.MARCH, 3, 14, 0),
                endAt = LocalDateTime.of(2022, Month.MARCH, 5, 22, 0)
            )
            val coupon = createCouponRequest.toCoupon(SELLER);
            every { couponRepository.save(coupon) } returns coupon

            `when`("쿠폰 정보를 생성하면") {
                couponService.createCoupon(createCouponRequest, SELLER)

                then("요청이 성공하여 쿠폰 정보를 저장한다") {
                    verify(exactly = 1) { couponRepository.save(coupon) }
                }
            }
        }
    }
}
*/
