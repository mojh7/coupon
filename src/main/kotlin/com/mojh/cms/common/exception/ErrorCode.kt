package com.mojh.cms.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class ErrorCode(
    val status: HttpStatus,
    val code: String, // client에서 error 상세 구분용 code
    val message: String
) {
    // common

    // member & auth
    MEMBER_NOT_FOUND(NOT_FOUND, "100", "해당 멤버를 찾을 수 없습니다"),
    DUPLICATE_ACCOUNT_ID(BAD_REQUEST, "101", "중복된 계정 ID 입니다."),
    WRONG_ACCOUNT_ID(UNAUTHORIZED, "102", "잘못된 계정 ID를 입력했습니다."),
    PASSWORD_NOT_MATCHED(UNAUTHORIZED, "103", "비밀번호가 일치하지 않습니다."),

    NEED_TO_LOGIN_AGAIN(UNAUTHORIZED, "104", "다시 로그인해야 합니다."),
    INVALID_TOKEN(UNAUTHORIZED, "105", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "106", "만료된 토큰입니다."),
    ALREADY_LOGGED_OUT_MEMBER(UNAUTHORIZED, "107", "이미 로그아웃 처리된 멤버입니다."),

    // coupon
    COUPON_DOES_NOT_EXIST(NOT_FOUND, "200", "해당 쿠폰 정보가 존재하지 않습니다."),
    COUPON_DOWNLOAD_FAILED(INTERNAL_SERVER_ERROR, "201", "쿠폰 다운로드에 실패했습니다."),
    HAS_ALREADY_DOWNLOADED_COUPON(CONFLICT, "202", "이미 쿠폰을 다운 받았습니다."),
    DOWNLOAD_COUPON_TIME_OUT(REQUEST_TIMEOUT, "203", "쿠폰 다운로드 요청 time out"),
    RUN_OUT_OF_COUPONS(CONFLICT, "204", "준비된 쿠폰이 모두 소진되었습니다."),
    COUPON_IS_NOT_AVAILABLE(BAD_REQUEST, "205", "쿠폰을 사용할 수 없습니다."),
    CUSTOMER_COUPON_DOES_NOT_EXIST(NOT_FOUND, "206", "회원 쿠폰이 존재하지 않습니다."),
    UNABLE_DOWNLOAD_COUPON(CONFLICT, "207", "쿠폰을 다운로드 할 수 없습니다.")

    // event
}