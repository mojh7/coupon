package com.mojh.cms.security

val PERMIT_ALL_GET_URI = arrayOf(
    "/event/**"
)

val PERMIT_ALL_POST_URI = arrayOf(
    "/member/signup",
    "/auth/login",
)

const val BEARER_PREFIX = "Bearer "

const val ACCESS_TOKEN_REDIS_KEY_PREFIX = "AT:"

const val REFRESH_TOKEN_REDIS_KEY_PREFIX = "RT:"