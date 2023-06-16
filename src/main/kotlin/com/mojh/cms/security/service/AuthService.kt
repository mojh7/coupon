package com.mojh.cms.security.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode.*
import com.mojh.cms.member.repository.MemberRepository
import com.mojh.cms.security.dto.TokensResponse
import com.mojh.cms.security.dto.request.LoginRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    @Transactional
    fun login(loginRequest: LoginRequest): TokensResponse {
        val member = memberRepository.findByAccountId(loginRequest.accountId)
            ?: throw CouponApplicationException(WRONG_ACCOUNT_ID)

        if (!passwordEncoder.matches(loginRequest.password, member.password)) {
            throw CouponApplicationException(PASSWORD_NOT_MATCHED)
        }

        val accessToken = jwtService.generateAccessToken(loginRequest.accountId)
        val refreshToken = jwtService.generateRefreshToken()

        jwtService.addRefreshToken(loginRequest.accountId, refreshToken)

        return TokensResponse(accessToken, refreshToken)
    }

    @Transactional
    fun logout(accessTokenHeader: String?, refreshToken: String) {
        jwtService.validateAccessToken(refreshToken)

        val accessToken = accessTokenHeader?.let { jwtService.extractAccessTokenFrom(it) } ?: throw CouponApplicationException(INVALID_TOKEN)
        val accountId = jwtService.parseAccountIdFromAccessToken(accessToken)

        // refresh token redis에서 제거
        if (!jwtService.containsRefreshToken(accountId, refreshToken)) {
            throw CouponApplicationException(ALREADY_LOGGED_OUT_MEMBER)
        }
        jwtService.removeRefreshToken(accountId, refreshToken)
    }

    @Transactional(readOnly = true)
    fun reissueAccessToken(accessTokenHeader: String?, refreshToken: String): String {
        val accountId: String = jwtService.extractAccessTokenFrom(accessTokenHeader)?.let {
            jwtService.parseAccountIdFromAccessToken(it)
        } ?: throw CouponApplicationException(INVALID_TOKEN)

        jwtService.validateRefreshToken(refreshToken)

        if (!jwtService.containsRefreshToken(accountId, refreshToken)) {
            throw CouponApplicationException(NEED_TO_LOGIN_AGAIN)
        }

        return jwtService.generateAccessToken(accountId)
    }
}