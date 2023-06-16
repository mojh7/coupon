package com.mojh.cms.security.util

import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class RefreshTokenUtils : AbstractJJwtUtils() {
    @Value("\${jwt.refresh.expiration}")
    override val EXPIRATION: Long = 0

    @Value("\${jwt.refresh.secret-key}")
    private lateinit var REFRESH_TOKEN_SECRET_KEY_RAW: String

    override val SECRET_KEY: SecretKey by lazy {
        Keys.hmacShaKeyFor(REFRESH_TOKEN_SECRET_KEY_RAW.toByteArray())
    }
}