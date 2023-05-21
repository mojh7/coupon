package com.mojh.cms.coupon.entity

import com.mojh.cms.common.BaseEntity
import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode.COUPON_IS_NOT_AVAILABLE
import com.mojh.cms.member.entity.Member
import org.springframework.security.access.AccessDeniedException
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class MemberCoupon constructor(
    customer: Member,
    coupon: Coupon,
) : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var customer: Member = customer
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    val coupon: Coupon = coupon

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status = Status.ISSUED
        protected set

    @Column
    var usedAt: LocalDateTime? = null
        protected set

    enum class Status {
        ISSUED, USED
    }

    companion object {
        fun of(customer: Member, coupon: Coupon): MemberCoupon {
            if (!customer.isCustomer()) {
                throw AccessDeniedException("회원만 쿠폰을 다운 받을 수 있습니다")
            }
            return MemberCoupon(customer, coupon)
        }
    }

    fun isAvailable(): Boolean {
        if (status != Status.ISSUED || !coupon.isAvailable()) {
            return false
        }
        return true
    }

    fun use(customer: Member) {
        if (isOwner(customer)) {
            throw AccessDeniedException("해당 쿠폰을 소유한 회원이 아닙니다")
        }
        if (!isAvailable()) {
            throw CouponApplicationException(COUPON_IS_NOT_AVAILABLE)
        }

        status = Status.USED
        usedAt = LocalDateTime.now()
    }

    private fun isOwner(customer: Member): Boolean {
        if (!this.customer.equals(customer)) {
            return false
        }
        return true
    }
}