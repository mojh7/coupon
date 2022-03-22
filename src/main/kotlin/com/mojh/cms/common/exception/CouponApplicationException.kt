package com.mojh.cms.common.exception

open class CouponApplicationException : RuntimeException {
    val errorCode: ErrorCode

    constructor(errorCode: ErrorCode) : super(errorCode.message) {
        this.errorCode = errorCode
    }

    constructor(errorCode: ErrorCode, cause: Throwable) : super(cause) {
        this.errorCode = errorCode
    }

    override fun toString(): String {
        val s = javaClass.name
        return s + ": " + errorCode.message + if(cause == null) "" else "\ncause: " + cause.toString()
    }
}

