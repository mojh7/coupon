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
class GlobalRestControllerAdvice {

    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<*>> {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach(Consumer {
                error: ObjectError -> errors[(error as FieldError).field] = error.getDefaultMessage()
        })
        LOGGER.warn(ex)
        return ResponseEntity.status(BAD_REQUEST)
            .body(ApiResponse.failed(BAD_REQUEST, errors))
    }
    
    @ExceptionHandler(HttpMessageConversionException::class)
    fun handleHttpMessageConversionException(ex: HttpMessageConversionException): ResponseEntity<ApiResponse<*>> {
        LOGGER.warn(ex)
        return ResponseEntity.status(BAD_REQUEST)
            .body(ApiResponse.failed(BAD_REQUEST, "bad request body"))
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
        return ApiResponse.failed(FORBIDDEN, "Access is denied")
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiResponse<*>> {
        LOGGER.error("internal error", ex)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(ApiResponse.failed(INTERNAL_SERVER_ERROR, "Internal Server Error"))
    }
}