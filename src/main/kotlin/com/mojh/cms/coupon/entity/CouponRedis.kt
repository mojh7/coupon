package com.mojh.cms.coupon.entity

import com.mojh.cms.common.embeddable.Period
import org.springframework.data.redis.core.RedisHash
import java.time.ZoneOffset
import javax.persistence.*

@RedisHash("coupon")
class CouponRedis(
    id: Long,
    maxCount: Int,
    availablePeriod: Period,
    status: Coupon.Status
) {
    companion object {
        fun from(coupon: Coupon) = CouponRedis(
            id = coupon.id!!,
            maxCount = coupon.maxCount,
            availablePeriod = coupon.availablePeriod,
            status = coupon.status
        )
    }

    @Id
    val id: Long = id

    @Column(nullable = false)
    var maxCount: Int = maxCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Coupon.Status = status
        protected set

    // TODO: LocalDateTime -> Instant로 변경
    @Column
    var startAt: Long = availablePeriod.startAt.toEpochMilli()

    @Column
    var endAt: Long = availablePeriod.endAt.toEpochMilli()
}