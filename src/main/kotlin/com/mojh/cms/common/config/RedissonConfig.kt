package com.mojh.cms.common.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
class RedissonConfig(private val environment: Environment) {
    companion object {
        private var redisServer: RedisServer? = null
    }

    // profile local 혹은 test 일 때 embedded redis 사용
    @PostConstruct
    fun redisServer() {
        for (profileName in environment.activeProfiles) {
            println(profileName)
            if (!(profileName.equals("local") || profileName.equals("test"))) {
                continue;
            }

            println(redisServer)
            if (redisServer == null) {
                redisServer = RedisServer()
                redisServer!!.start()
            }
            println(redisServer)
            break;
        }
    }

    @PreDestroy
    fun stopRedis() {
        redisServer?.stop()
    }

    @Bean
    fun redisson(): RedissonClient? {
        val config: Config = Config.fromYAML(
            RedissonConfig::class.java.classLoader.getResource("redisson-config.yml"))
        return Redisson.create(config)
    }
}