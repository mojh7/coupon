package com.mojh.cms.coupon.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.event.entity.Event
import com.mojh.cms.member.entity.Member
import org.springframework.security.access.AccessDeniedException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Coupon(
    seller: Member,
    event: Event? = null,
    title: String,
    description: String = "",
    maxCount: Int,
    startAt: LocalDateTime,
    endAt: LocalDateTime
) : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var seller: Member = seller
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = true)
    var event: Event? = event
        protected set

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
    var startAt: LocalDateTime = startAt
        protected set

    @Column(updatable = false)
    var endAt: LocalDateTime = endAt
        protected set

    enum class Status {
        CREATED, ENABLED, DISABLED
    }

    fun enable(seller: Member) {
        if (!seller.isSeller()) {
            throw AccessDeniedException("Seller만 쿠폰을 활성화 할 수 있습니다.")
        }

        status = Status.ENABLED
    }
}