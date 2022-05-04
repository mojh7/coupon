package com.mojh.cms.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Profile(value = ["local", "test"])
@Configuration
class EmbeddedRedisConfig {
    //@Value("\${spring.redis.port}")
    //private val redisPort = 0
    private var redisServer: RedisServer? = null

    @PostConstruct
    fun redisServer() {
        redisServer = RedisServer(6379)
        redisServer?.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer?.stop()
    }
}