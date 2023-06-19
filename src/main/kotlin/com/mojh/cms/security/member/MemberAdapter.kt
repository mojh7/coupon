package com.mojh.cms.security.member

import com.mojh.cms.member.entity.Member
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class MemberAdapter(member: Member) : UserDetails {

    val member: Member = member
    private val role = listOf(SimpleGrantedAuthority(member.role.toString()))

    override fun getAuthorities(): List<SimpleGrantedAuthority> {
        return role
    }

    override fun getPassword(): String = ""

    override fun getUsername(): String = member.accountId

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}