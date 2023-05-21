package com.mojh.cms.member.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode.DUPLICATE_ACCOUNT_ID
import com.mojh.cms.common.exception.ErrorCode.MEMBER_NOT_FOUND
import com.mojh.cms.member.dto.request.SignupMemberRequest
import com.mojh.cms.member.entity.Member
import com.mojh.cms.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun signup(signupMemberRequest: SignupMemberRequest) {
        if (memberRepository.existsByAccountId(signupMemberRequest.accountId)) {
            throw CouponApplicationException(DUPLICATE_ACCOUNT_ID)
        }

        passwordEncoder.encode(signupMemberRequest.password).let {
            memberRepository.save(signupMemberRequest.toMember(it))
        }
    }

    fun getById(id: Long) = memberRepository.getById(id)

    fun findById(id: Long) = memberRepository.findByIdOrNull(id)
        ?: throw CouponApplicationException(MEMBER_NOT_FOUND)
}