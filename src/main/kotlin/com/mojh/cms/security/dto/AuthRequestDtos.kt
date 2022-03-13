package com.mojh.cms.member.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank
    @field:Size(min = 5, max = 16)
    val accountId: String,

    @field:NotBlank
    @field:Size(min = 6, max = 20)
    val password: String,
)

data class ReissueTokenRequest(
    @field:NotBlank
    val refreshToken: String
)