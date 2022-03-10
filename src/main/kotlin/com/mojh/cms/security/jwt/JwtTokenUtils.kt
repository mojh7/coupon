package com.mojh.cms.security.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenUtils {
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

    fun parseId(accessToken: String): String {
        return Jwts.parserBuilder()
            .requireSubject("access")
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(accessToken)
            .body["id"] as String
    }

    fun validateTokenStatus(token: String): TokenStatus {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
            TokenStatus.VALID
        } catch (ex: Exception) {
            when(ex) {
                is ExpiredJwtException -> TokenStatus.EXPIRED
                else -> TokenStatus.INVALID
            }
        }
    }
}