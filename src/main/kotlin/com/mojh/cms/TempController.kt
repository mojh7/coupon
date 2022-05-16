package com.mojh.cms

import com.mojh.cms.common.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TempController(
) {

    @GetMapping("/hello-world")
    fun testApi() = ApiResponse.succeed("hello world~")
}