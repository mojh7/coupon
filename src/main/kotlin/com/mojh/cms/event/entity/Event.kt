package com.mojh.cms.event.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.coupon.entity.Coupon
import com.mojh.cms.coupon.entity.MemberCoupon
import com.mojh.cms.member.entity.Member
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class Event(
    seller: Member,
    title: String,
    description: String,
    status: Status = Status.CREATED,
    startAt: LocalDateTime,
    endAt: LocalDateTime
) : BaseEntity() {
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var seller: Member = seller
        protected set

    @Column(length = 64, nullable = false)
    var title: String = title
        protected set

    @Column(length = 255, nullable = false)
    var description: String = description
        protected set

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status = status
        protected set

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    var coupons: MutableSet<Coupon> = TreeSet<Coupon>()
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