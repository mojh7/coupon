package com.mojh.cms.security.jwt

import com.mojh.cms.common.exception.CustomException
import com.mojh.cms.common.exception.ErrorCode.EXPIRED_TOKEN
import com.mojh.cms.common.exception.ErrorCode.INVALID_TOKEN
import com.mojh.cms.security.ACCESS_TOKEN_REDIS_KEY_PREFIX
import com.mojh.cms.security.BEARER_PREFIX
import com.mojh.cms.security.REFRESH_TOKEN_REDIS_KEY_PREFIX
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.redisson.api.RSetCache
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtTokenUtils(
    private val redisson: RedissonClient
) {
    @Value("\${jwt.secret-key}")
    private val SECRET_KEY_RAW: String = ""

    @Value("\${jwt.access-token-valid-time}")
    private val ACCESS_TOKEN_VALID_TIME: Long = 0

    @Value("\${jwt.refresh-token-valid-time}")
    private val REFRESH_TOKEN_VALID_TIME: Long = 0

    private val SECRET_KEY: SecretKey by lazy {
        Keys.hmacShaKeyFor(SECRET_KEY_RAW.toByteArray())
    }

    fun createAccessToken(accountId: String): String {
        val now = Date()
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject("access")
            .claim("id", accountId)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + ACCESS_TOKEN_VALID_TIME))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
            .compact()
    }

    fun createRefreshToken(): String {
        val now = Date()
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setIssuedAt(now)
            .setExpiration(Date(now.time + REFRESH_TOKEN_VALID_TIME))
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
                .requireSubject("access")
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(accessToken)
                .body["id"] as String
        } catch (ex: ExpiredJwtException) {
            // 만료된 AccessToken 재발급에 필요한 로직이라 만료 됐어도 parsing
            try {
                ex.claims["id"] as String
            } catch (ex: Exception) {
                // Claims안의 값이 null이거나 이상한 값일 수도 있어서 한 번 더 예외 처리
                throw CustomException(INVALID_TOKEN, ex)
            }
        } catch (ex: Exception) {
            throw CustomException(INVALID_TOKEN, ex)
        }
    }

    /**
     * 남은 만료 시간 milliseconds 단위로 반환
     */
    fun getRemainingExpirationTime(token: String) =
        try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .body.expiration.time - Date().time
        } catch (ex: Exception) {
            when (ex) {
                is ExpiredJwtException -> throw CustomException(EXPIRED_TOKEN, ex)
                else -> throw CustomException(INVALID_TOKEN, ex)
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
                is ExpiredJwtException -> throw CustomException(EXPIRED_TOKEN, ex)
                else -> throw CustomException(INVALID_TOKEN, ex)
            }
        }

    /**
     * 이미 로그아웃 처리되어 차단된 access token인지 확인
     */
    fun isBlockedAccessToken(accessToken: String, accountId: String): Boolean {
        if (getAccessTokenRSetCache(accountId).contains(accessToken)) {
            return true
        }
        return false
    }

    fun extractTokenFrom(header: String?): String? {
        return header?.run {
            if (!startsWith(BEARER_PREFIX)) {
                return null
            }
            substring(BEARER_PREFIX.length)
        }
    }

    fun getAccessTokenRSetCache(accountId: String): RSetCache<String> =
        redisson.getSetCache(ACCESS_TOKEN_REDIS_KEY_PREFIX + accountId)

    fun getRefreshTokenRSetCache(accountId: String): RSetCache<String> =
        redisson.getSetCache(REFRESH_TOKEN_REDIS_KEY_PREFIX + accountId)
}