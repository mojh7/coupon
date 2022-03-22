package com.mojh.cms.security.dto.request

import javax.validation.constraints.NotBlank

data class LogoutRequest(
    @field:NotBlank
    val refreshToken: String
)