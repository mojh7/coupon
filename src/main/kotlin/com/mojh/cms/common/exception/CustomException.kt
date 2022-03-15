package com.mojh.cms.common.exception

open class CustomException : RuntimeException {
    val errorCode: ErrorCode

    constructor(errorCode: ErrorCode) : super(errorCode.message) {
        this.errorCode = errorCode
    }

    constructor(errorCode: ErrorCode, cause: Throwable) : super(errorCode.message, cause) {
        this.errorCode = errorCode
    }
}

