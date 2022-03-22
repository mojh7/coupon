package com.mojh.cms.security.dto

data class TokensResponse(
    val accessToken: String,
    val refreshToken: String
)