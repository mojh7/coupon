package com.mojh.cms.web

import org.springframework.http.HttpStatus

data class ApiResponse<out T> private constructor(
    val success: Boolean,
    val response: T?,
    val error: ErrorResponse<*>?) {

    companion object {
        private val SUCCEED_RESPONSE: ApiResponse<*> = ApiResponse(true, null, null)

        fun succeed(): ApiResponse<*> = SUCCEED_RESPONSE

        fun <T> succeed(data: T?): ApiResponse<T> = ApiResponse(true, data, null)

        fun failed(httpStatus: HttpStatus) =
            ApiResponse(false, null, ErrorResponse(httpStatus.value(), null))

        fun <E> failed(httpStatus: HttpStatus, message: E?) =
            ApiResponse(false, null, ErrorResponse<E>(httpStatus.value(), message))
    }

    class ErrorResponse<out E> (val status: Int, val message: E?)
}