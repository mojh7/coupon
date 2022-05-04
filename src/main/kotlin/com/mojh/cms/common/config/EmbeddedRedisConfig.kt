package com.mojh.cms.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Profile(value = ["local", "test"])
@Configuration
class EmbeddedRedisConfig {
    companion object {
        private var redisServer: RedisServer? = null
    }

    @PostConstruct
    fun redisServer() {
        if (redisServer == null) {
            redisServer = RedisServer()
            redisServer!!.start()
        }
    }

    @PreDestroy
    fun stopRedis() {
        redisServer?.stop()
    }
}