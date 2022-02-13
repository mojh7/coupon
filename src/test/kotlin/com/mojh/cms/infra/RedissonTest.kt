package com.mojh.cms.infra

import com.mojh.cms.common.BaseTest
import com.mojh.cms.config.RedissonConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.redisson.api.RedissonClient
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.TimeUnit

@SpringBootTest(classes = [RedissonConfig::class])
class RedissonTest(private val redisson: RedissonClient) : BaseTest() {

    @Test
    fun `레디스 데이터 만료 전 조회 성공`() {
        //given
        val key = "redissonTestKey1"
        val value = "value1"

        //when
        val actual = redisson.getBucket<String>(key).run {
            set(value, 750, TimeUnit.MILLISECONDS)
            Thread.sleep(300)
            get()
        }

        //then
        assertThat(actual).isEqualTo(value)
    }

    @Test
    fun `레디스 데이터 만료 후 조회시 데이터 삭제 확인`() {
        //given
        val key = "redissonTestKey2"
        val value = "value2"

        //when
        val actual = redisson.getBucket<String>(key).run {
            set(value, 750, TimeUnit.MILLISECONDS)
            Thread.sleep(1000)
            get()
        }

        //then
        assertThat(actual).isNull()
    }

}