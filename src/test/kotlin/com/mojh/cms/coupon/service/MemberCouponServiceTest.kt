package com.mojh.cms.coupon.service

import com.mojh.cms.common.UnitTest
import com.mojh.cms.coupon.dto.CreateCouponRequest
import com.mojh.cms.coupon.repository.CouponRepository
import com.mojh.cms.member.entity.Member
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.*
import java.time.LocalDateTime
import java.time.Month

internal class MemberCouponServiceTest : UnitTest() {
    @MockK
    private lateinit var couponRepository: CouponRepository

    @InjectMockKs
    private lateinit var couponService: CouponService

    private lateinit var seller: Member;

    @BeforeEach
    internal fun setUp() {
        seller = Member("accountId", "pw", Member.Role.SELLER)
    }

    @DisplayName("쿠폰 정보 생성")
    @Nested
    inner class Create {
        private lateinit var request: CreateCouponRequest

        @BeforeEach
        internal fun setUp() {
            request = CreateCouponRequest("쿠폰 이름", "쿠폰 설명", maxCount = 100
                , startAt = LocalDateTime.of(2022, Month.MARCH, 3, 14, 0)
                , endAt = LocalDateTime.of(2022, Month.MARCH, 5, 22, 0))
        }

        @Test
        fun `성공`() {
            // given
            val couponInfo = request.toEntity(seller);
            every { couponRepository.save(couponInfo) } returns couponInfo

            // when
            couponService.createCoupon(request, seller)

            // then
            verify (exactly = 1) { couponRepository.save(couponInfo) }
        }
    }
}