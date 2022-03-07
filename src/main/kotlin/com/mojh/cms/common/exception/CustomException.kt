package com.mojh.cms.common.exception

open class CustomException(override val message: String?) : RuntimeException() {
    
}

class BadRequestException(message: String?) : CustomException(message)

class NotFoundException(message: String?) : CustomException(message)

class ConflictException(message: String?) : CustomException(message)