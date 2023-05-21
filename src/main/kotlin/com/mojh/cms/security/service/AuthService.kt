package com.mojh.cms.security.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode.*
import com.mojh.cms.member.repository.MemberRepository
import com.mojh.cms.security.dto.TokensResponse
import com.mojh.cms.security.dto.request.LoginRequest
import com.mojh.cms.security.jwt.JwtTokenUtils
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenUtils: JwtTokenUtils
) {


    @Transactional
    fun login(loginRequest: LoginRequest): TokensResponse {
        val member = memberRepository.findByAccountId(loginRequest.accountId)
            ?: throw CouponApplicationException(WRONG_ACCOUNT_ID)

        if (!passwordEncoder.matches(loginRequest.password, member.password)) {
            throw CouponApplicationException(PASSWORD_NOT_MATCHED)
        }

        val accessToken = jwtTokenUtils.createAccessToken(loginRequest.accountId)
        val refreshToken = jwtTokenUtils.createRefreshToken()

        jwtTokenUtils.addRefreshToken(loginRequest.accountId, refreshToken)

        return TokensResponse(accessToken, refreshToken)
    }

    @Transactional
    fun logout(accessTokenHeader: String?, refreshToken: String) {
        jwtTokenUtils.validateToken(refreshToken)

        val accessToken = accessTokenHeader?.let { jwtTokenUtils.extractTokenFrom(it) } ?: throw CouponApplicationException(INVALID_TOKEN)
        val accountId = jwtTokenUtils.parseAccountId(accessToken)

        if (jwtTokenUtils.isBlockedAccessToken(accountId, accessToken)) {
            throw CouponApplicationException(ALREADY_LOGGED_OUT_MEMBER)
        }

        // refresh token redis에서 제거
        if (!jwtTokenUtils.containsRefreshToken(accountId, refreshToken)) {
            throw CouponApplicationException(ALREADY_LOGGED_OUT_MEMBER)
        }
        jwtTokenUtils.removeRefreshToken(accountId, refreshToken)

        // access token blacklist 등록
        jwtTokenUtils.addAccessTokenBlackList(accountId, accessToken)
    }

    @Transactional(readOnly = true)
    fun reissueAccessToken(accessTokenHeader: String?, refreshToken: String): String {
        val accountId: String = accessTokenHeader?.let {
            jwtTokenUtils.extractTokenFrom(it)
        }?.let {
            jwtTokenUtils.parseAccountId(it)
        } ?: throw CouponApplicationException(INVALID_TOKEN)

        jwtTokenUtils.validateToken(refreshToken)

        if (!jwtTokenUtils.containsRefreshToken(accountId, refreshToken)) {
            throw CouponApplicationException(NEED_TO_LOGIN_AGAIN)
        }

        return jwtTokenUtils.createAccessToken(accountId)
    }
}