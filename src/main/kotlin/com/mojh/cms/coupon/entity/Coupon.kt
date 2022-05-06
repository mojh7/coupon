package com.mojh.cms.coupon.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.common.embeddable.Period
import com.mojh.cms.member.entity.Member
import org.springframework.security.access.AccessDeniedException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Coupon(
    seller: Member,
    title: String,
    description: String = "",
    maxCount: Int,
    availablePeriod: Period,
) : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val seller: Member = seller

    @Column(length = 64, nullable = false)
    var title: String = title
        protected set

    @Column(length = 64, nullable = false)
    var description: String = description
        protected set

    @Column(nullable = false)
    var maxCount: Int = maxCount
        protected set

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status = Status.CREATED
        protected set

    @Column(nullable = false)
    var availablePeriod: Period = availablePeriod

    enum class Status {
        CREATED, ENABLED, DISABLED
    }

    fun isAvailable(): Boolean {
        val now = LocalDateTime.now()
        if (status != Status.ENABLED || !availablePeriod.contains(now)) {
            return false
        }
        return true
    }

    fun enable(seller: Member) {
        if (!seller.isSeller()) {
            throw AccessDeniedException("판매자만 쿠폰을 활성화 할 수 있습니다.")
        }
        status = Status.ENABLED
    }
}