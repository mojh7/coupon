package com.mojh.cms.coupon.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.member.entity.Member
import org.springframework.security.access.AccessDeniedException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class MemberCoupon protected constructor(
    customer: Member,
    coupon: Coupon,
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
    var status: Status = Status.ISSUED
        protected set

    var usedAt: LocalDateTime? = null
        protected set

    enum class Status {
        ISSUED, USED
    }

    companion object {
        fun of(customer: Member, coupon: Coupon): MemberCoupon {
            if (!customer.isCustomer()) {
                throw AccessDeniedException("Customer 만 쿠폰을 다운 받을 수 있습니다")
            }
            return MemberCoupon(customer, coupon)
        }
    }

    fun isAvailable(now: LocalDateTime): Boolean {
        if (status != Status.ISSUED || !coupon.validPeriod.isValid(now)) {
            return false
        }
        return true
    }
}