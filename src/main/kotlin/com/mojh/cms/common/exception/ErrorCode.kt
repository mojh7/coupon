package com.mojh.cms.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    // common

    // member & auth
    MEMBER_NOT_FOUND(NOT_FOUND, "100", "해당 멤버를 찾을 수 없습니다"),
    DUPLICATE_ACCOUNT_ID(BAD_REQUEST, "101", "중복된 계정 ID 입니다."),

    // coupon
    COUPON_NOT_FOUND(NOT_FOUND, "200", "해당 쿠폰 정보를 찾을 수 없습니다")

    // event
}