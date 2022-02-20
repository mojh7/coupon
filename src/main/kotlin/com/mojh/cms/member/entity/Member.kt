package com.mojh.cms.member.entity

import com.mojh.cms.common.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
class Member(
    @Column(length = 16, nullable = false, columnDefinition = "char")
    var accountId: String,

    @Column(length = 64, nullable = false, columnDefinition = "char")
    var password: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: MemberRole
): BaseEntity()