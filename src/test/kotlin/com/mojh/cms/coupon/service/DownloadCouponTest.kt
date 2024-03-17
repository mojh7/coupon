/*
package com.mojh.cms.coupon.service

import com.mojh.cms.common.annotation.BaseTest
import com.mojh.cms.coupon.dto.CreateCouponRequest
import com.mojh.cms.coupon.repository.MemberCouponRepository
import com.mojh.cms.member.entity.Member
import com.mojh.cms.member.repository.MemberRepository
import io.kotest.matchers.shouldBe
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.time.Month
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@BaseTest
@SpringBootTest
class DownloadCouponTest(
    private val memberRepository: MemberRepository,
    private val couponService: CouponService,
    private val memberCouponRepository: MemberCouponRepository
) {
    private val THREAD_POOL_SIZE = 17

    companion object {
        private val LOGGER = LogManager.getLogger()

    }

    @Test
    fun `쿠폰 다운로드`() {
        val customer = memberRepository.save(Member("accountId1", "pw", Member.Role.ROLE_CUSTOMER))
        val seller = memberRepository.save(Member("accountId2", "pw", Member.Role.ROLE_SELLER))

        LOGGER.info("${customer.id!!} ${seller.id!!}")

        val expectedMaxCount = 10

        val createCouponRequest = CreateCouponRequest("쿠폰 이름", "쿠폰 설명", maxCount = expectedMaxCount
            , startAt = LocalDateTime.of(2022, Month.MARCH, 3, 14, 0)
            , endAt = LocalDateTime.of(2022, Month.MARCH, 5, 22, 0))

        val couponId = couponService.createCoupon(createCouponRequest, seller)!!
        couponService.enable(couponId, seller)

        LOGGER.debug("쿠폰 등록")

        val executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE)

        for (i in 0 until THREAD_POOL_SIZE) {
            executorService.execute {
                LOGGER.debug(i)
                couponService.downloadCoupon(couponId, customer)
            }
        }


        LOGGER.debug("shutdown")
        executorService.shutdown();
        // 셧다운 요구 후 모든 태스크 실행이 완료되거나 타임아웃이 발생하거나
        // 현재 스레드가 중단될 때까지 차단합니다.
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        val memberCouponList = memberCouponRepository.findAllByCustomerIdAndCouponId(customer.id!!, couponId)

        println(memberCouponList.size)
        memberCouponList.size shouldBe 1
    }

    */
/*@Test
    fun `쿠폰 다운로드 - lock 없는 테스트`() {
        val customer = memberRepository.save(Member("accountId1", "pw", Member.Role.ROLE_CUSTOMER))
        val seller = memberRepository.save(Member("accountId2", "pw", Member.Role.ROLE_SELLER))

        LOGGER.info("${customer.id!!} ${seller.id!!}")

        val expectedMaxCount = 10

        val createCouponRequest = CreateCouponRequest("쿠폰 이름", "쿠폰 설명", maxCount = expectedMaxCount
            , startAt = LocalDateTime.of(2022, Month.MARCH, 3, 14, 0)
            , endAt = LocalDateTime.of(2022, Month.MARCH, 5, 22, 0))

        val couponId = couponService.createCoupon(createCouponRequest, seller)!!
        couponService.enable(couponId, seller)

        LOGGER.debug("쿠폰 등록")

        val executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE)

        for (i in 0 until THREAD_POOL_SIZE) {
            executorService.execute {
                LOGGER.debug(i)
                couponService.downloadCoupon2(couponId, customer)
            }
        }


        LOGGER.debug("shutdown")
        executorService.shutdown();
        // 셧다운 요구 후 모든 태스크 실행이 완료되거나 타임아웃이 발생하거나
        // 현재 스레드가 중단될 때까지 차단합니다.
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        val memberCouponList = memberCouponRepository.findAllByCustomerIdAndCouponId(customer.id!!, couponId)

        println(memberCouponList.size)
        memberCouponList.size shouldBe expectedMaxCount
    }*//*


}
*/
