package com.mojh.cms

import com.mojh.cms.common.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class TempController {

    @GetMapping("/s")
    fun succeed(): ApiResponse<*> {
        return ApiResponse.succeed()
    }

    @GetMapping("/s2")
    @ResponseStatus(HttpStatus.CREATED)
    fun succeed2() = ApiResponse.succeed("sss")

    @GetMapping("/f")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun failed(): ApiResponse<*> {
        return ApiResponse.failed(HttpStatus.BAD_REQUEST, "error~")
    }

    @GetMapping("/f2")
    fun failed2(): ApiResponse<*> {
        return ApiResponse.failed(HttpStatus.NOT_FOUND)
    }
}