package com.mojh.cms.security.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode.ALREADY_LOGGED_OUT
import com.mojh.cms.common.exception.ErrorCode.NOT_TOKENS_FROM_SAME_ACCOUNT
import com.mojh.cms.security.ACCOUNT_ID_CLAIM_NAME
import com.mojh.cms.security.BEARER_PREFIX
import com.mojh.cms.security.REFRESH_TOKEN_CHAIN_CLAIM_NAME
import com.mojh.cms.security.entity.RefreshTokenRedis
import com.mojh.cms.security.repository.RefreshTokenRedisRepository
import com.mojh.cms.security.util.JwtUtils
import io.jsonwebtoken.Claims
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val accessTokenUtils: JwtUtils,
    private val refreshTokenUtils: JwtUtils,
    private val refreshTokenRedisRepository: RefreshTokenRedisRepository
) {
    fun generateAccessToken(accountId: String): String {
        val claims = mapOf(
            ACCOUNT_ID_CLAIM_NAME to accountId
        )
        return accessTokenUtils.generateToken(claims)
    }

    fun generateRefreshToken(accountId: String): String {
        return generateRefreshToken(accountId, UUID.randomUUID().toString())
    }

    fun generateRefreshToken(accountId: String, tokenChainId: String): String {
        val claims = mapOf(
            ACCOUNT_ID_CLAIM_NAME to accountId,
            REFRESH_TOKEN_CHAIN_CLAIM_NAME to tokenChainId
        )
        val tokenId = UUID.randomUUID().toString()
        return refreshTokenUtils.generateToken(claims, tokenId)
    }

    fun <T> parseClaimFromAccessToken(accessToken: String, claimName: String): T = accessTokenUtils.parseClaim(accessToken, claimName)

    fun <T> parseClaimFromRefreshToken(refreshToken: String, claimName: String): T = refreshTokenUtils.parseClaim(refreshToken, claimName)

    fun parseClaimsFromRefreshToken(refreshToken: String): Map<String, Any> = refreshTokenUtils.parseClaims(refreshToken)

    /**
     * 남은 만료 시간 milliseconds 단위로 반환
     */
    fun getRemainingExpirationTimeFromRefreshToken(refreshToken: String): Long =
        refreshTokenUtils.getRemainingExpirationTime(refreshToken)

    /**
     * 같은 계정에서 생성된 access, refresh token 인지 확인
     */
    fun verifyTokensFromSameAccount(accessToken: String, refreshToken: String) {
        val accountIdFromAccessToken: String = parseClaimFromAccessToken(accessToken, ACCOUNT_ID_CLAIM_NAME)
        val accountIdFromRefreshToken: String = parseClaimFromRefreshToken(refreshToken, ACCOUNT_ID_CLAIM_NAME)

        if (!accountIdFromAccessToken.equals(accountIdFromRefreshToken)) {
            throw CouponApplicationException(NOT_TOKENS_FROM_SAME_ACCOUNT)
        }
    }

    /**
     * access token 유효성과 만료 여부 확인
     */
    fun validateAccessToken(accessToken: String) {
        accessTokenUtils.validateToken(accessToken)
    }

    /**
     * refresh token 유효성과 만료 여부 확인
     */
    fun validateRefreshToken(refreshToken: String) {
        refreshTokenUtils.validateToken(refreshToken)
    }

    fun extractAccessTokenFrom(header: String?): String? {
        return header?.run {
            if (!startsWith(BEARER_PREFIX)) {
                return null
            }
            substring(BEARER_PREFIX.length)
        }
    }

    fun saveRefreshToken(refreshToken: String) {
        val refreshTokenClaims = parseClaimsFromRefreshToken(refreshToken)
        val accountId = refreshTokenClaims[ACCOUNT_ID_CLAIM_NAME] as String
        val refreshTokenChainId = refreshTokenClaims[REFRESH_TOKEN_CHAIN_CLAIM_NAME] as String
        val refreshTokenId = refreshTokenClaims[Claims.ID] as String
        val ttl = getRemainingExpirationTimeFromRefreshToken(refreshToken)

        refreshTokenRedisRepository.save(RefreshTokenRedis(accountId, refreshTokenChainId, refreshTokenId, ttl))
    }

    /**
     * refresh token을 처음 사용하는 경우라면
     *
     * redis에서 해당 token chain에 저장된 가장 마지막으로 생성한 refresh token과 일치해야 한다
     */
    fun isReusingRefreshToken(refreshToken: String): Boolean {
        val refreshTokenClaims = parseClaimsFromRefreshToken(refreshToken)
        val accountId = refreshTokenClaims[ACCOUNT_ID_CLAIM_NAME] as String
        val refreshTokenChainId = refreshTokenClaims[REFRESH_TOKEN_CHAIN_CLAIM_NAME] as String
        val refreshTokenId = refreshTokenClaims[Claims.ID] as String

        val id = RefreshTokenRedis.generateId(accountId, refreshTokenChainId)
        // redis의 token chain내의 가장 최근에 생성된 refresh token이 없으면 이미 revoke 처리된 상태
        val latestRefreshTokenId = refreshTokenRedisRepository.findByIdOrNull(id)
            ?: throw CouponApplicationException(ALREADY_LOGGED_OUT)

        // 일치하지 않은 경우는 토큰 재발급 요청시 사용한 RT보다 나중에 만들어진 RT가 존재
        // 결국 해당 RT는 재사용된 토큰
        if (!refreshTokenId.equals(latestRefreshTokenId)) {
            return true
        }

        return false
    }

    fun revokeRefreshTokenChain(refreshToken: String) {
        val refreshTokenClaims = parseClaimsFromRefreshToken(refreshToken)
        val accountId = refreshTokenClaims[ACCOUNT_ID_CLAIM_NAME] as String
        val refreshTokenChainId = refreshTokenClaims[REFRESH_TOKEN_CHAIN_CLAIM_NAME] as String

        val id = RefreshTokenRedis.generateId(accountId, refreshTokenChainId)
        refreshTokenRedisRepository.findByIdOrNull(id)?.let {
            refreshTokenRedisRepository.delete(it)
        } ?: throw CouponApplicationException(ALREADY_LOGGED_OUT)
    }
}