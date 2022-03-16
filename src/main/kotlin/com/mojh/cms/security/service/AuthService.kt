package com.mojh.cms.security.service

import com.mojh.cms.common.exception.CustomException
import com.mojh.cms.common.exception.ErrorCode.*
import com.mojh.cms.member.dto.LoginRequest
import com.mojh.cms.member.repository.MemberRepository
import com.mojh.cms.security.ACCESS_TOKEN_REDIS_KEY_PREFIX
import com.mojh.cms.security.REFRESH_TOKEN_REDIS_KEY_PREFIX
import com.mojh.cms.security.dto.TokensResponse
import com.mojh.cms.security.jwt.JwtTokenUtils
import org.redisson.api.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit


@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenUtils: JwtTokenUtils
) {
    @Value("\${jwt.refresh-token-valid-time}")
    private val REFRESH_TOKEN_VALID_TIME: Long = 0

    @Transactional
    fun login(loginRequest: LoginRequest): TokensResponse {
        val member = memberRepository.findByAccountId(loginRequest.accountId)
            ?: throw CustomException(WRONG_ACCOUNT_ID)

        if (!passwordEncoder.matches(loginRequest.password, member.password)) {
            throw CustomException(PASSWORD_NOT_MATCHED)
        }

        val accessToken = jwtTokenUtils.createAccessToken(loginRequest.accountId)
        val refreshToken = jwtTokenUtils.createRefreshToken()

        jwtTokenUtils.getRefreshTokenRSetCache(loginRequest.accountId)
            .add(refreshToken, REFRESH_TOKEN_VALID_TIME, TimeUnit.MILLISECONDS)

        return TokensResponse(accessToken, refreshToken)
    }

    @Transactional
    fun logout(accessToken: String, refreshToken: String) {
        jwtTokenUtils.validateToken(refreshToken)

        val accountId = jwtTokenUtils.parseAccountId(accessToken)

        // refresh token redis에서 제거
        val refreshTokenSet = jwtTokenUtils.getRefreshTokenRSetCache(accountId)
        if (refreshTokenSet.contains(refreshToken)) {
            throw CustomException(ALREADY_LOGGED_OUT_MEMBER)
        }
        refreshTokenSet.remove(refreshToken)

        // access token blacklist 등록
        jwtTokenUtils.getAccessTokenRSetCache(accountId)
            .add(accessToken, jwtTokenUtils.getRemainingExpirationTime(accessToken), TimeUnit.MILLISECONDS)
    }

    @Transactional(readOnly = true)
    fun reissueAccessToken(accessToken: String?, refreshToken: String): String {
        val accountId: String = accessToken?.let { jwtTokenUtils.parseAccountId(it) } ?: throw CustomException(INVALID_TOKEN)

        jwtTokenUtils.validateToken(refreshToken)

        val refreshTokenSet = jwtTokenUtils.getRefreshTokenRSetCache(accountId)
        if (!refreshTokenSet.contains(refreshToken)) {
            throw CustomException(NEED_TO_LOGIN_AGAIN)
        }

        return jwtTokenUtils.createAccessToken(accountId)
    }
}