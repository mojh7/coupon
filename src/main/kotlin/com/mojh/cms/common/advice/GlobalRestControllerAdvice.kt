package com.mojh.cms.common.advice

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.common.exception.CustomException
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.function.Consumer

@RestControllerAdvice
class GlobalRestControllerAdvice {

    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException): ResponseEntity<ApiResponse<*>> {
        val errors: MutableMap<String, String?> = HashMap()
        exception.bindingResult.allErrors.forEach(Consumer {
                error: ObjectError -> errors[(error as FieldError).field] = error.getDefaultMessage()
        })
        LOGGER.warn(exception.toString())
        return ResponseEntity.status(BAD_REQUEST)
            .body(ApiResponse.failed(BAD_REQUEST, errors))
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(exception: CustomException): ResponseEntity<ApiResponse<*>> {
        LOGGER.warn(exception.toString())
        return ResponseEntity.status(exception.errorCode.status)
            .body(ApiResponse.failed(exception.errorCode))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ApiResponse<*>> {
        LOGGER.error(exception.toString())
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(ApiResponse.failed(INTERNAL_SERVER_ERROR, "internal server error"))
    }
}