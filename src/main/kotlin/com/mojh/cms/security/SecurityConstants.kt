package com.mojh.cms.security

val AUTH_GET_URL = arrayOf(
    "/customer/**",
)

val AUTH_POST_URL = arrayOf(
    "/seller/**",
    "/coupons/**",
    "/customer/**",
)

const val AUTH_EXCEPTION_INFO = "AUTH_EXCEPTION_INFO"

const val BEARER_PREFIX = "Bearer "

const val ACCOUNT_ID_CLAIM_NAME = "id"

const val REFRESH_TOKEN_CHAIN_CLAIM_NAME = "tokenChain"
