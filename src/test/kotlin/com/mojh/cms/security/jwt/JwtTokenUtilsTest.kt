package com.mojh.cms.security.jwt

import com.mojh.cms.common.BaseTest
import com.mojh.cms.common.config.RedissonConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [JwtTokenUtils::class, RedissonConfig::class])
internal class JwtTokenUtilsTest(
    private val jwtTokenUtils: JwtTokenUtils
) : BaseTest() {

    @Test
    fun `유효한 access token 생성`() {
        //given
        val accountId = "account123"

        //when
        val token = jwtTokenUtils.createAccessToken(accountId)
        jwtTokenUtils.validateToken(token)

        //then
        assertThat(token).isNotBlank
    }

    @Test
    fun `유효한 refresh token 생성`() {
        //given

        //when
        val token = jwtTokenUtils.createRefreshToken()
        jwtTokenUtils.validateToken(token)

        //then
        assertThat(token).isNotBlank
    }
}