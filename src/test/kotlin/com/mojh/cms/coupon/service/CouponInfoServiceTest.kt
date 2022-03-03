package com.mojh.cms.coupon.service

import com.mojh.cms.common.UnitTest
import com.mojh.cms.common.exception.ConflictException
import com.mojh.cms.coupon.dto.CreateCouponInfoRequest
import com.mojh.cms.coupon.entity.CouponInfo
import com.mojh.cms.coupon.repository.CouponInfoRepository
import com.mojh.cms.member.entity.Member
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.*
import java.time.LocalDateTime
import java.time.Month

internal class CouponInfoServiceTest : UnitTest() {
    @MockK
    private lateinit var couponInfoRepository: CouponInfoRepository

    @InjectMockKs
    private lateinit var couponInfoService: CouponInfoService

    private lateinit var admin: Member;

    @BeforeEach
    internal fun setUp() {
        admin = Member("accountId", "pw", Member.Role.ADMIN)
    }

    @DisplayName("쿠폰 정보 생성")
    @Nested
    inner class Create {
        private lateinit var request: CreateCouponInfoRequest

        @BeforeEach
        internal fun setUp() {
            request = CreateCouponInfoRequest("쿠폰 이름", "쿠폰 설명", maxCount = 100
                , startAt = LocalDateTime.of(2022, Month.MARCH, 3, 14, 0)
                , endAt = LocalDateTime.of(2022, Month.MARCH, 5, 22, 0))
        }

        @Test
        fun `성공`() {
            // given
            val couponInfo = request.toEntity(admin);
            every { couponInfoRepository.save(couponInfo) } returns couponInfo

            // when
            couponInfoService.createCouponInfo(request, admin)

            // then
            verify (exactly = 1) { couponInfoRepository.save(couponInfo) }
        }
    }
}