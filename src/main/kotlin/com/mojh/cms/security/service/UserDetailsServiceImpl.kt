package com.mojh.cms.security.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode
import com.mojh.cms.member.repository.MemberRepository
import com.mojh.cms.security.MemberAdapter
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val memberRepository: MemberRepository) : UserDetailsService {
    override fun loadUserByUsername(accountId: String): UserDetails {
        return memberRepository.findByAccountId(accountId)?.let {
            MemberAdapter(it)
        } ?: throw CouponApplicationException(ErrorCode.MEMBER_NOT_FOUND)
    }
}