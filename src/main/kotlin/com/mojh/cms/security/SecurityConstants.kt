package com.mojh.cms.security

val PERMIT_ALL_GET_URI = arrayOf(
    "/event/**"
)

val PERMIT_ALL_POST_URI = arrayOf(
    "/member/signup",
    "/auth/login",
    "/auth/logout",
    "/auth/reissue"
)

const val AUTH_EXCEPTION_INFO = "AUTH_EXCEPTION_INFO"

const val BEARER_PREFIX = "Bearer "

const val ACCESS_TOKEN_REDIS_KEY_PREFIX = "AT:"

const val REFRESH_TOKEN_REDIS_KEY_PREFIX = "RT:"