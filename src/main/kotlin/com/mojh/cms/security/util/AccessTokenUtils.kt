package com.mojh.cms.security.util

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class AccessTokenUtils : AbstractJJwtUtils() {
    @Value("\${jwt.access.token-valid-time}")
    override val EXPIRATION: Long = 0

    @Value("\${jwt.access.secret-key}")
    private lateinit var ACCESS_TOKEN_SECRET_KEY_RAW: String

    override val SECRET_KEY: SecretKey by lazy {
        Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET_KEY_RAW.toByteArray())
    }

    override val SIGNATURE_ALGORITHM: SignatureAlgorithm = SignatureAlgorithm.HS512
}