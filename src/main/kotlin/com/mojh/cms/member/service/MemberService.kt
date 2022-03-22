package com.mojh.cms.member.service

import com.mojh.cms.common.exception.CustomException
import com.mojh.cms.common.exception.ErrorCode.DUPLICATE_ACCOUNT_ID
import com.mojh.cms.member.dto.request.SignupMemberRequest
import com.mojh.cms.member.repository.MemberRepository
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
            throw CustomException(DUPLICATE_ACCOUNT_ID)
        }

        passwordEncoder.encode(signupMemberRequest.password).let {
            memberRepository.save(signupMemberRequest.toMember(it))
        }
    }
}