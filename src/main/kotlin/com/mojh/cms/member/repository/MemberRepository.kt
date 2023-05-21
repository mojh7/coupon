package com.mojh.cms.member.repository

import com.mojh.cms.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun existsByAccountId(accountId: String): Boolean
    fun findByAccountId(accountId: String): Member?
}