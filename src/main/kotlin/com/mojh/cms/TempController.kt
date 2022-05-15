package com.mojh.cms

import com.mojh.cms.common.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hello-world")
class TempController(
) {

    @GetMapping
    fun testApi() = ApiResponse.succeed("hello world~")
}