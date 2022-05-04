package com.mojh.cms.common.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@DependsOn(value = ["embeddedRedisConfig"])
@Configuration
class RedissonConfig {
    @Bean
    fun redisson(): RedissonClient? {
        val config: Config = Config.fromYAML(
            RedissonConfig::class.java.classLoader.getResource("redisson-config.yml"))
        return Redisson.create(config)
    }
}