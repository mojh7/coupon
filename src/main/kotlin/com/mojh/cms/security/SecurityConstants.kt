package com.mojh.cms.security

val PERMIT_ALL_GET_URI = arrayOf(
    "/event/**"
)

val PERMIT_ALL_POST_URI = arrayOf(
    "/member/signup",
    "/auth/login",
)

const val BEARER_PREFIX = "Bearer "