package com.mojh.cms.event.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.coupon.entity.Coupon
import com.mojh.cms.member.entity.Member
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Event(
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    val seller: Member,

    @Column(length = 64, nullable = false)
    var title: String,

    @Column(length = 255, nullable = false)
    var description: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status = Status.CREATED,

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    var coupons: MutableSet<Coupon>,

    @Column(nullable = false)
    var startAt: LocalDateTime,

    @Column(updatable = false)
    var endAt: LocalDateTime
) : BaseEntity() {
    enum class Status {
        CREATED, ENABLED, DISABLED
    }
}