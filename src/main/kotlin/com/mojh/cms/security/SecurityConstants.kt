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

const val ACCESS_TOKEN_REDIS_KEY_PREFIX = "AT:"

const val REFRESH_TOKEN_REDIS_KEY_PREFIX = "RT:"

const val ACCOUNT_ID = "id"