package com.mojh.cms.security.service

import com.mojh.cms.security.*
import com.mojh.cms.security.util.JwtUtils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class JwtService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val accessTokenUtils: JwtUtils,
    private val refreshTokenUtils: JwtUtils
) {
    fun generateAccessToken(accountId: String): String {
        val claims = mapOf(
            ACCOUNT_ID to accountId
        )
        return accessTokenUtils.generateToken(claims)
    }

    fun generateRefreshToken(): String {
        return refreshTokenUtils.generateToken()
    }

    fun parseAccountIdFromAccessToken(accessToken: String): String {
        return accessTokenUtils.parseClaims(accessToken)[ACCOUNT_ID] as String
    }

    /**
     * 남은 만료 시간 milliseconds 단위로 반환
     */
    fun getRemainingExpirationTimeFromAccessToken(accessToken: String): Long =
        accessTokenUtils.getRemainingExpirationTime(accessToken)

    /**
     * 남은 만료 시간 milliseconds 단위로 반환
     */
    fun getRemainingExpirationTimeFromRefreshToken(refreshToken: String): Long =
        refreshTokenUtils.getRemainingExpirationTime(refreshToken)


    fun validateAccessToken(accessToken: String): Boolean = accessTokenUtils.validateToken(accessToken)

    fun validateRefreshToken(refreshToken: String): Boolean = refreshTokenUtils.validateToken(refreshToken)

    fun extractAccessTokenFrom(header: String?): String? {
        return header?.run {
            if (!startsWith(BEARER_PREFIX)) {
                return null
            }
            substring(BEARER_PREFIX.length)
        }
    }

    fun addRefreshToken(accountId: String, refreshToken: String) {
        val key = REFRESH_TOKEN_REDIS_KEY_PREFIX + accountId
        redisTemplate.opsForSet().add(key, refreshToken)
        redisTemplate.expire(key, refreshTokenUtils.getRemainingExpirationTime(refreshToken), TimeUnit.MILLISECONDS)
    }

    fun removeRefreshToken(accountId: String, refreshToken: String) {
        val key = REFRESH_TOKEN_REDIS_KEY_PREFIX + accountId
        redisTemplate.opsForSet().remove(key, refreshToken)
    }

    fun containsRefreshToken(accountId: String, refreshToken: String): Boolean {
        val key = REFRESH_TOKEN_REDIS_KEY_PREFIX + accountId
        val contains = redisTemplate.opsForSet().isMember(key, refreshToken)
        return contains!!
    }
}