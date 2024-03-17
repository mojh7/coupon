package com.mojh.cms

import com.mojh.cms.common.annotation.IntegrationTest
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

// TODO: testcontainer redis test, 이후에 삭제하기
@IntegrationTest
class RedisTest : AnnotationSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @BeforeEach
    fun setUp() {
        // 각 테스트 메서드 실행 전에 Redis 데이터를 초기화합니다.
        redisTemplate.connectionFactory?.connection?.flushDb()
    }

    @Test
    fun `redis test`() {
        val key = "test-key"
        val value = "test-value"

        redisTemplate.opsForValue().set(key, value)
        val result = redisTemplate.opsForValue().get(key) as String?

        result shouldBe value
    }

    // 각 테스트가 독립적이여야되니 test-key 데이터가 없어야됨
    @Test
    fun `redis test2`() {
        val key = "test-key"
        val value = "test-value"

        val result = redisTemplate.opsForValue().get(key) as String?

        result shouldBe null
    }

}