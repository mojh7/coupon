package com.mojh.cms.common.exception

import com.mojh.cms.common.ApiResponse
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.function.Consumer

@RestControllerAdvice
class GlobalExceptionHandler {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ApiResponse<*> {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach(Consumer {
                error: ObjectError -> errors[(error as FieldError).field] = error.getDefaultMessage()
        })
        LOGGER.warn(ex)
        return ApiResponse.failed(errors)
    }
    
    @ExceptionHandler(HttpMessageConversionException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleHttpMessageConversionException(ex: HttpMessageConversionException): ApiResponse<*> {
        LOGGER.warn(ex)
        return ApiResponse.failed("Bad request body")
    }

    @ExceptionHandler(CouponApplicationException::class)
    fun handleCustomException(ex: CouponApplicationException): ResponseEntity<ApiResponse<*>> {
        LOGGER.warn(ex)
        return ResponseEntity.status(ex.errorCode.status)
            .body(ApiResponse.failed(ex.errorCode))
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(FORBIDDEN)
    fun handleAccessDeniedException(ex: AccessDeniedException): ApiResponse<*> {
        LOGGER.warn(ex)
        return ApiResponse.failed("Access is denied")
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    fun handleException(ex: Exception): ApiResponse<*> {
        LOGGER.error("Internal server error", ex)
        return ApiResponse.failed("Internal server error")
    }
}