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

    var maxCount: Int = maxCount;

    var status: Coupon.Status = status
        protected set

    var startAt: Long = availablePeriod.startAt.toEpochMilli()

    var endAt: Long = availablePeriod.endAt.toEpochMilli()
}