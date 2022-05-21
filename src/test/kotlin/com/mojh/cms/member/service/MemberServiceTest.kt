package com.mojh.cms.member.service

import com.mojh.cms.common.UnitTest
import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode
import com.mojh.cms.member.dto.request.SignupMemberRequest
import com.mojh.cms.member.repository.MemberRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder

@UnitTest
internal class MemberServiceTest : BehaviorSpec({
    val memberRepository = mockk<MemberRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val memberService = MemberService(memberRepository, passwordEncoder)

    val ENCODED_PW = "\$2a\$10\$1h7PIxB8mCORT69vsg1Lve6YFzl/O4k349WrPhlN76BUB905JVQqC";
    val PASSWORD = "pw12ab31cd23"

    isolationMode = IsolationMode.InstancePerTest

    given("중복되지 않은 계정 아이디와 유효한 비밀번호로") {
        val signupMemberRequest = SignupMemberRequest("account", PASSWORD)
        val customer = signupMemberRequest.toMember(ENCODED_PW)
        every { memberRepository.existsByAccountId(signupMemberRequest.accountId) } returns false
        every { passwordEncoder.encode(signupMemberRequest.password) } returns ENCODED_PW
        every { memberRepository.save(signupMemberRequest.toMember(ENCODED_PW)) } returns customer

        `when`("일반 회원 가입 요청을 시도하면") {
            memberService.signup(signupMemberRequest)
            then("요청이 성공하여 회원 정보를 저장한다") {
                verify(exactly = 1) {
                    memberRepository.existsByAccountId(signupMemberRequest.accountId)
                    passwordEncoder.encode(signupMemberRequest.password)
                    memberRepository.save(customer)
                }
            }
        }
    }

    given("이미 가입된 회원 아이디와 중복된 계정 아이디와 비밀번호로") {
        val signupMemberRequest = SignupMemberRequest("account2", PASSWORD)
        val customer = signupMemberRequest.toMember(ENCODED_PW)
        every { memberRepository.existsByAccountId(signupMemberRequest.accountId) } returns true

        `when`("일반 회원 가입 요청을 시도하면") {
            val exception = shouldThrow<CouponApplicationException> { memberService.signup(signupMemberRequest) }

            then("DUPLICATE_ACCOUNT_ID 예외를 던진다") {
                exception.errorCode shouldBe ErrorCode.DUPLICATE_ACCOUNT_ID
                verify(exactly = 1) {
                    memberRepository.existsByAccountId(signupMemberRequest.accountId)
                }
                verify(exactly = 0) {
                    passwordEncoder.encode(signupMemberRequest.password)
                    memberRepository.save(customer)
                }
            }
        }
    }
})