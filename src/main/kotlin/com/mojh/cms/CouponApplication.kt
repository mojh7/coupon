package com.mojh.cms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class CouponApplication

fun main(args: Array<String>) {
    runApplication<CouponApplication>(*args)
}
