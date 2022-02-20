package com.mojh.cms.coupon.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.member.entity.Member
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Coupon(
    @ManyToOne
    @JoinColumn(name = "member_seq", nullable = false)
    val customer: Member,

    @ManyToOne
    @JoinColumn(name = "coupon_info_seq", nullable = false)
    val couponInfo: CouponInfo,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: CouponStatus = CouponStatus.ISSUED,

    var usedAt: LocalDateTime,
) : BaseEntity()