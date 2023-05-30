package com.mojh.cms.security.jwt

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode.EXPIRED_TOKEN
import com.mojh.cms.common.exception.ErrorCode.INVALID_TOKEN
import com.mojh.cms.security.*
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey


@Component
class JwtTokenUtils(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    @Value("\${jwt.access-token-valid-time}")
    private val ACCESS_TOKEN_VALID_TIME: Long = 0

    @Value("\${jwt.refresh-token-valid-time}")
    private val REFRESH_TOKEN_VALID_TIME: Long = 0

    @Value("\${jwt.secret-key}")
    private lateinit var SECRET_KEY_RAW: String
    private val SECRET_KEY: SecretKey by lazy {
        Keys.hmacShaKeyFor(SECRET_KEY_RAW.toByteArray())
    }

    fun createAccessToken(accountId: String): String {
        val now = Instant.now()
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .claim(ACCOUNT_ID, accountId)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(ACCESS_TOKEN_VALID_TIME)))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
            .compact()
    }

    fun createRefreshToken(): String {
        val now = Instant.now()
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(REFRESH_TOKEN_VALID_TIME)))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
            .compact()
    }

    /**
     * access token 내의 account id 반환(만료된 토큰이라도 반환)
     * access token만 가능
     */
    fun parseAccountId(accessToken: String): String {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(accessToken)
                .body[ACCOUNT_ID] as String
        } catch (ex: ExpiredJwtException) {
            if (ex.claims[Claims.SUBJECT] != "ACCESS") {
                throw CouponApplicationException(INVALID_TOKEN)
            }
            // 만료된 AccessToken 재발급에 필요한 로직이라 만료 됐어도 parsing
            ex.claims[ACCOUNT_ID] as String
        } catch (ex: Exception) {
            throw CouponApplicationException(INVALID_TOKEN, ex)
        }
    }

    /**
     * 남은 만료 시간 milliseconds 단위로 반환
     */
    fun getRemainingExpirationTime(token: String): Long =
        try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .body.expiration.time - Instant.now().toEpochMilli()
        } catch (ex: Exception) {
            when (ex) {
                is ExpiredJwtException -> throw CouponApplicationException(EXPIRED_TOKEN, ex)
                else -> throw CouponApplicationException(INVALID_TOKEN, ex)
            }
        }


    fun validateToken(token: String) =
        try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: Exception) {
            when (ex) {
                is ExpiredJwtException -> throw CouponApplicationException(EXPIRED_TOKEN, ex)
                else -> throw CouponApplicationException(INVALID_TOKEN, ex)
            }
        }

    /**
     * 이미 로그아웃 처리되어 차단된 access token인지 확인
     */
    fun isBlockedAccessToken(accountId: String, accessToken: String): Boolean {
        val key = ACCESS_TOKEN_REDIS_KEY_PREFIX + accountId
        val contains = redisTemplate.opsForSet().isMember(key, accessToken)
        return contains!!
    }

    fun extractTokenFrom(header: String?): String? {
        return header?.run {
            if (!startsWith(BEARER_PREFIX)) {
                return null
            }
            substring(BEARER_PREFIX.length)
        }
    }

    fun getAccessTokenSet(accountId: String): MutableSet<out Any>? {
        val valueOperations = redisTemplate.opsForSet()
        return valueOperations.members(ACCESS_TOKEN_REDIS_KEY_PREFIX + accountId)
    }

    fun addAccessTokenBlackList(accountId: String, accessToken: String) {
        val key = ACCESS_TOKEN_REDIS_KEY_PREFIX + accountId
        redisTemplate.opsForSet().add(key, accessToken)
        redisTemplate.expire(key, getRemainingExpirationTime(accessToken), TimeUnit.MILLISECONDS)
    }

    fun addRefreshToken(accountId: String, refreshToken: String) {
        val key = REFRESH_TOKEN_REDIS_KEY_PREFIX + accountId
        redisTemplate.opsForSet().add(key, refreshToken)
        redisTemplate.expire(key, REFRESH_TOKEN_VALID_TIME, TimeUnit.MILLISECONDS)
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