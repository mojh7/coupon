package com.mojh.cms.member.dto.request

import com.mojh.cms.member.entity.Member
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class SignupMemberRequest(
    @field:NotBlank
    @field:Size(min = 5, max = 16)
    val accountId: String,

    @field:NotBlank
    @field:Size(min = 6, max = 20)
    val password: String
) {
    fun toMember(encodedPassword: String) = Member(
        accountId = this.accountId,
        password = encodedPassword,
        role = Member.Role.ROLE_CUSTOMER
    )
}