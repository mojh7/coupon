package com.mojh.cms.common

import com.mojh.cms.common.exception.ErrorCode
import org.springframework.http.HttpStatus

data class ApiResponse<out T> private constructor(
    val success: Boolean,
    val response: T?,
    val error: ErrorResponse<*>?) {

    companion object {
        private val SUCCEED_RESPONSE: ApiResponse<*> = ApiResponse(true, null, null)

        fun succeed(): ApiResponse<*> = SUCCEED_RESPONSE

        fun <T> succeed(data: T?): ApiResponse<T> = ApiResponse(true, data, null)

        fun failed(errorCode: ErrorCode) =
            ApiResponse(false, null, ErrorResponse(errorCode.status.value(), errorCode.code, errorCode.message))

        fun <E> failed(status: HttpStatus, message: E) =
            ApiResponse(false, null, ErrorResponse(status = status.value(), message = message))
    }

    private class ErrorResponse<out E> (
        val status: Int,
        val code: String? = null,
        val message: E?
    )
}