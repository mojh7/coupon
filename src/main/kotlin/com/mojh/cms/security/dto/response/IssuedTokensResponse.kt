package com.mojh.cms.security.dto.response

data class IssuedTokensResponse(
    val accessToken: String,
    val refreshToken: String
)