package com.mojh.cms.security.util

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode
import com.mojh.cms.common.exception.ErrorCode.INVALID_TOKEN
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

/**
 * JJWT 의존성 사용해서 만든 JWT
 */
abstract class AbstractJJwtUtils : JwtUtils {

    protected abstract val EXPIRATION: Long

    protected abstract val SECRET_KEY: SecretKey

    override fun generateToken(claims: Map<String, Any>): String {
        val now = Instant.now()
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setClaims(claims)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(EXPIRATION)))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
            .compact()
    }

    override fun generateToken(claims: Map<String, Any>, tokenId: String): String {
        val now = Instant.now()
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setClaims(claims)
            .setId(tokenId)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(EXPIRATION)))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
            .compact()
    }

    override fun validateToken(token: String) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
        } catch (ex: Exception) {
            when (ex) {
                is ExpiredJwtException -> throw CouponApplicationException(ErrorCode.EXPIRED_TOKEN, ex)
                else -> throw CouponApplicationException(INVALID_TOKEN, ex)
            }
        }
    }

    override fun <T> parseClaim(token: String, claimName: String): T = parseClaims(token)[claimName] as T

    /**
     * jwt 사용하는 로직 특정상 invalid toekn이 아니라면 만료됐어도 claim 얻는게 가능하다
     */
    override fun parseClaims(token: String): Map<String, Any> {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (ex: ExpiredJwtException) {
            ex.claims
        } catch (ex: Exception) {
            throw CouponApplicationException(INVALID_TOKEN, ex)
        }
    }

    override fun getRemainingExpirationTime(token: String): Long {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .body.expiration.time - Instant.now().toEpochMilli()
        } catch (ex: Exception) {
            when (ex) {
                is ExpiredJwtException -> throw CouponApplicationException(ErrorCode.EXPIRED_TOKEN, ex)
                else -> throw CouponApplicationException(INVALID_TOKEN, ex)
            }
        }
    }
}
