package com.mojh.cms.security.jwt

import com.mojh.cms.common.BaseTest
import com.mojh.cms.common.config.RedissonConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@BaseTest
@SpringBootTest(classes = [JwtTokenUtils::class, RedissonConfig::class])
internal class JwtTokenUtilsTest(private val jwtTokenUtils: JwtTokenUtils) {

    private lateinit var accountId: String;

    @BeforeEach
    internal fun setUp() {
        accountId = "testAccountId"
    }

    @Test
    fun `유효한 access token 생성`() {
        //given

        //when
        val token = jwtTokenUtils.createAccessToken(accountId)

        //then
        assertThat(token).isNotBlank
        jwtTokenUtils.validateToken(token)
    }

    @Test
    fun `유효한 refresh token 생성`() {
        //given

        //when
        val token = jwtTokenUtils.createRefreshToken()

        //then
        assertThat(token).isNotBlank
        jwtTokenUtils.validateToken(token)
    }
}