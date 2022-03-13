package com.mojh.cms.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    // common

    // member & auth
    MEMBER_NOT_FOUND(NOT_FOUND, "100", "해당 멤버를 찾을 수 없습니다"),
    DUPLICATE_ACCOUNT_ID(BAD_REQUEST, "101", "중복된 계정 ID 입니다."),
    WRONG_ACCOUNT_ID(UNAUTHORIZED, "102", "잘못된 계정 ID를 입력했습니다."),
    PASSWORD_NOT_MATCHED(UNAUTHORIZED, "103", "비밀번호가 일치하지 않습니다."),
    
    LOGIN_REQUIRED(UNAUTHORIZED, "104", "로그인이 필요합니다."),
    INVALID_TOKEN(UNAUTHORIZED, "105", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "106", "만료된 토큰입니다."),

    // coupon
    COUPON_NOT_FOUND(NOT_FOUND, "200", "해당 쿠폰 정보를 찾을 수 없습니다")

    // event
}