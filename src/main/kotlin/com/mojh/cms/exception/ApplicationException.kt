package com.mojh.cms.exception

open class ApplicationException(override val message: String?) : RuntimeException()

class BadRequestException(message: String?) : ApplicationException(message)

class NotFoundException(message: String?) : ApplicationException(message)

class ConflictException(message: String?) : ApplicationException(message)