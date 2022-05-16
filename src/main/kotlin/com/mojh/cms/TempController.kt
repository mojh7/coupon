package com.mojh.cms

import com.mojh.cms.common.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TempController(
) {

    @GetMapping("/hello-world")
    fun testApi() = ApiResponse.succeed("hello world~")

    @GetMapping("/pause")
    @Throws(InterruptedException::class)
    fun pause(): String? {
        val s = 15
        println("$s 초 sleep 시작")
        for (idx in 1..s) {
            Thread.sleep(1000)
            println("$idx 초")
        }
        println("sleep 끝")
        return "pause~"
    }
}