package com.mojh.cms.security.jwt

import com.mojh.cms.common.BaseTest
import com.mojh.cms.common.config.RedissonConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.StringUtils

@BaseTest
@SpringBootTest(classes = [JwtTokenUtils::class, RedissonConfig::class])
@ContextConfiguration
internal class JwtTokenUtilsTest(private val jwtTokenUtils: JwtTokenUtils) : FunSpec() {

    companion object {
        private val accountId = "testAccountId";
        private val jwtHeaderBase64 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.";
    }

    init {

        test("access token 생성 성공시 유효한 토큰 획득") {
            val actualAccessToken = jwtTokenUtils.createAccessToken(accountId)

            StringUtils.containsWhitespace(actualAccessToken) shouldBe false
            actualAccessToken shouldStartWith jwtHeaderBase64
        }

        test("refresh token 생성 성공시 유효한 토큰 획득") {
            val actualRefreshToken = jwtTokenUtils.createRefreshToken()

            StringUtils.containsWhitespace(actualRefreshToken) shouldBe false
            actualRefreshToken shouldStartWith jwtHeaderBase64
        }
    }
}