package com.mojh.cms.security.member

import com.mojh.cms.member.entity.Member
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class MemberAdapter(val member: Member) :
    User(member.accountId, member.password, listOf(SimpleGrantedAuthority(member.role.toString())))