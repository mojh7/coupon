package com.mojh.cms

import com.mojh.cms.common.annotation.IntegrationTest
import com.mojh.cms.member.dto.request.SignupMemberRequest
import com.mojh.cms.member.entity.Member
import com.mojh.cms.member.repository.MemberRepository
import com.mojh.cms.member.service.MemberService
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

// TODO: testcontainer mysql test, 이후에 삭제하기
@IntegrationTest
class MySQLTest : AnnotationSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    fun `mysql test1 - member1 저장`() {
        val request = SignupMemberRequest("member1", "12345678")
        memberService.signup(request)

        val member1: Member? = memberRepository.findByAccountId(request.accountId)

        member1?.let { it.accountId shouldBe request.accountId }
    }

    // 각 테스트가 독립적이여야되니 member1 없어야됨.
    @Test
    fun `mysql test2 - 존재하지 않은 member1`() {
        val request = SignupMemberRequest("member1", "12345678")
        val member1: Member? = memberRepository.findByAccountId(request.accountId)

        member1 shouldBe null
    }

}