package com.mojh.cms.security.util


interface JwtUtils {

    fun generateToken(claims: Map<String, Any>): String

    fun generateToken(claims: Map<String, Any>, tokenId: String): String

    fun validateToken(token: String)

    fun <T> parseClaim(token: String, claimName: String): T

    fun parseClaims(token: String): Map<String, Any>

    fun getRemainingExpirationTime(token: String): Long
}