package com.mojh.cms.member.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.coupon.entity.MemberCoupon
import javax.persistence.*

@Entity
class Member(
    @Column(length = 16, nullable = false, columnDefinition = "char")
    var accountId: String,

    @Column(length = 64, nullable = false, columnDefinition = "char")
    var password: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "member_id")
    var memberCoupons: MutableSet<MemberCoupon>
): BaseEntity() {
    enum class Role {
        CUSTOMER, ADMIN
    }
}