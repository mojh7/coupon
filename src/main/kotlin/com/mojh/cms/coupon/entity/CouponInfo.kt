package com.mojh.cms.coupon.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.member.entity.Member
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class CouponInfo(
    @ManyToOne(targetEntity = Member::class)
    @JoinColumn(name = "member_seq", nullable = false)
    val admin: Member,

    @Column(length = 32, nullable = false)
    var name: String,

    @Column(length = 64)
    var description: String? = "",

    @Column(nullable = false)
    var maxCount: Int = 0,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: CouponInfoStatus = CouponInfoStatus.CREATED,

    @Column(nullable = false)
    var issuableFromDt: LocalDateTime,

    @Column(updatable = false)
    var issuableToDt: LocalDateTime,

    @Column(nullable = false)
    var validFromDt: LocalDateTime,

    @Column(updatable = false)
    var validToDt: LocalDateTime
) : BaseEntity()