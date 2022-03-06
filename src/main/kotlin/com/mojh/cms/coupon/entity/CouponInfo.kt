package com.mojh.cms.coupon.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.member.entity.Member
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class CouponInfo(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val admin: Member,

    @Column(length = 32, nullable = false)
    var name: String,

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