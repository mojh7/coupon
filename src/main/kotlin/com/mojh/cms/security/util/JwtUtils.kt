package com.mojh.cms.security.util


interface JwtUtils {

    fun generateToken(): String

    fun generateToken(claims: Map<String, Any>): String

    fun validateToken(token: String): Boolean

    fun parseClaims(token: String): Map<String, Any>

    fun getRemainingExpirationTime(token: String): Long
}