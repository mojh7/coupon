package com.mojh.cms.exception

import com.mojh.cms.web.ApiResponse
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus.*
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.function.Consumer

@RestControllerAdvice
class CustomExceptionHandler {

    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException): ApiResponse<*> {
        val errors: MutableMap<String, String?> = HashMap()
        exception.bindingResult.allErrors.forEach(Consumer {
                error: ObjectError -> errors[(error as FieldError).field] = error.getDefaultMessage()
        })
        LOGGER.warn(exception.toString())
        return ApiResponse.failed(BAD_REQUEST, errors)
    }

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleBadRequestException(exception: BadRequestException): ApiResponse<*> {
        LOGGER.warn(exception.toString())
        return ApiResponse.failed(BAD_REQUEST, exception.message)
    }

    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(CONFLICT)
    fun handleConflictException(exception: ConflictException): ApiResponse<*> {
        LOGGER.warn(exception.toString())
        return ApiResponse.failed(CONFLICT, exception.message)
    }

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(NOT_FOUND)
    fun handleNotFoundException(exception: NotFoundException): ApiResponse<*> {
        LOGGER.warn(exception.toString())
        return ApiResponse.failed(NOT_FOUND, exception.message)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    fun handleException(exception: Exception): ApiResponse<*> {
        LOGGER.error(exception.toString())
        return ApiResponse.failed(INTERNAL_SERVER_ERROR, "internal server error")
    }
}