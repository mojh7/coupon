package com.mojh.cms.member.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.coupon.entity.MemberCoupon
import java.util.*
import javax.persistence.*

@Entity
class Member(
    accountId: String,
    password: String,
    role: Role,
): BaseEntity() {
    @Column(nullable = false, columnDefinition = "char(16)")
    var accountId: String = accountId
        protected set

    @Column(nullable = false, columnDefinition = "char(64)")
    var password: String = password
        protected set

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role = role
        protected set

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "member_id")
    var coupons: MutableSet<MemberCoupon> = TreeSet<MemberCoupon>()
        protected set

    enum class Role {
        ROLE_CUSTOMER, ROLE_SELLER
    }

    fun isCustomer() = role == Role.ROLE_CUSTOMER

    fun isSeller() = role == Role.ROLE_SELLER
}