package com.mojh.cms.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.scripting.support.ResourceScriptSource
import java.util.*

@Configuration
class RedisConfig {

    @Value("\${redis.host:localhost}")
    private lateinit var host: String

    @Value("\${redis.port:6379}")
    private var port: Int = 0

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<*, *> {
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(redisConnectionFactory())

            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = GenericJackson2JsonRedisSerializer()
        }
    }

    @Bean
    fun downloadCouponScript(): RedisScript<String> {
        val script = DefaultRedisScript<String>()
        script.setScriptSource(ResourceScriptSource(ClassPathResource("lua-scripts/download-coupon.lua")))
        script.resultType = String::class.java
        return script
    }

    @Bean
    fun getIssuableCouponsScript(): RedisScript<List<String>> {
        val script = DefaultRedisScript<List<String>>()
        script.setScriptSource(ResourceScriptSource(ClassPathResource("lua-scripts/get-issuable-coupons.lua")))
        script.resultType = List::class.java as Class<List<String>>
        return script
    }
}