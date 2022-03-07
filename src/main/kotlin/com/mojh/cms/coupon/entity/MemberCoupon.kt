package com.mojh.cms.coupon.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.member.entity.Member
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class MemberCoupon(
    customer: Member,
    coupon: Coupon,
    status: Status = Status.ISSUED,
) : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var customer: Member = customer
        protected set

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    var coupon: Coupon = coupon
        protected set

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status = status
        protected set

    var usedAt: LocalDateTime? = null
        protected set

    enum class Status {
        ISSUED, USED, EXPIRED
    }
}