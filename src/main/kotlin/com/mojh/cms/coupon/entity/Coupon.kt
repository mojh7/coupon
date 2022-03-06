package com.mojh.cms.coupon.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.event.entity.Event
import com.mojh.cms.member.entity.Member
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Coupon(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val seller: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = true)
    val event: Event? = null,

    @Column(length = 64, nullable = false)
    var title: String,

    @Column(length = 64, nullable = false)
    var description: String = "",

    @Column(nullable = false)
    var maxCount: Int = 0,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status = Status.CREATED,

    @Column(nullable = false)
    var startAt: LocalDateTime,

    @Column(updatable = false)
    var endAt: LocalDateTime
) : BaseEntity() {
    enum class Status {
        CREATED, ENABLED, DISABLED
    }
}