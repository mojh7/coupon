package com.mojh.cms.coupon.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.event.entity.Event
import com.mojh.cms.member.entity.Member
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Coupon(
    seller: Member,
    event: Event? = null,
    title: String,
    description: String = "",
    maxCount: Int,
    status: Status = Status.CREATED,
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
    var status: Status = status
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
}