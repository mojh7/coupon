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

    @Column(length = 32, nullable = false)
    var title: String,

    @Column(length = 255, nullable = false)
    var description: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status = Status.CREATED,

    @ManyToMany
    @JoinTable(name = "event_coupon_info",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "coupon_info_id")]
    )
    var coupon: MutableList<Coupon>,

    @Column(nullable = false)
    var startAt: LocalDateTime,

    @Column(updatable = false)
    var endAt: LocalDateTime
) : BaseEntity() {
    enum class Status {
        CREATED, ENABLED, DISABLED
    }
}