package com.mojh.cms.common

import com.mojh.cms.common.exception.ErrorCode

data class ApiResponse<out T> private constructor(
    val success: Boolean,
    val response: T?,
    val error: ErrorResponse<*>?) {

    companion object {
        private val SUCCEED_RESPONSE: ApiResponse<*> = ApiResponse(true, null, null)

        fun succeed(): ApiResponse<*> = SUCCEED_RESPONSE

        fun <T> succeed(data: T?): ApiResponse<T> = ApiResponse(true, data, null)

        fun failed(errorCode: ErrorCode) =
            ApiResponse(false, null, ErrorResponse(errorCode.code, errorCode.message))

        fun <E> failed(message: E) =
            ApiResponse(false, null, ErrorResponse(message = message))
    }

    private class ErrorResponse<out E> (
        val code: String? = null,
        val message: E?
    )
}