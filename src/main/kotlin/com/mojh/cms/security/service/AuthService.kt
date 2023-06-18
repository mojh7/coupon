package com.mojh.cms.security.service

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode.*
import com.mojh.cms.member.repository.MemberRepository
import com.mojh.cms.security.ACCOUNT_ID_CLAIM_NAME
import com.mojh.cms.security.dto.request.LoginRequest
import com.mojh.cms.security.dto.response.IssuedTokensResponse
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
    fun login(loginRequest: LoginRequest): IssuedTokensResponse {
        val member = memberRepository.findByAccountId(loginRequest.accountId)
            ?: throw CouponApplicationException(WRONG_ACCOUNT_ID)

        if (!passwordEncoder.matches(loginRequest.password, member.password)) {
            throw CouponApplicationException(PASSWORD_NOT_MATCHED)
        }

        val accessToken = jwtService.generateAccessToken(member.accountId)
        val refreshToken = jwtService.generateRefreshToken(member.accountId)

        jwtService.saveRefreshToken(refreshToken)

        return IssuedTokensResponse(accessToken, refreshToken)
    }

    @Transactional
    fun logout(accessTokenHeader: String?, refreshToken: String) {
        val accessToken = jwtService.extractAccessTokenFrom(accessTokenHeader)
            ?: throw CouponApplicationException(INVALID_TOKEN)

        jwtService.verifyTokensFromSameAccount(accessToken, refreshToken)
        jwtService.validateRefreshToken(refreshToken)

        jwtService.revokeRefreshTokenChain(refreshToken)
    }

    @Transactional
    fun reissueTokens(accessTokenHeader: String?, refreshToken: String): IssuedTokensResponse {
        val accessToken = jwtService.extractAccessTokenFrom(accessTokenHeader)
            ?: throw CouponApplicationException(INVALID_TOKEN)
        val accountId: String = jwtService.parseClaimFromRefreshToken(refreshToken, ACCOUNT_ID_CLAIM_NAME)

        jwtService.verifyTokensFromSameAccount(accessToken, refreshToken)
        jwtService.validateRefreshToken(refreshToken)

        // refresh token 재사용 감지
        if (jwtService.isReusingRefreshToken(refreshToken)) {
            jwtService.revokeRefreshTokenChain(refreshToken)
        }

        // access, refresh token 재발급
        val reissuedAccessToken = jwtService.generateAccessToken(accountId)
        val reissuedRefreshToken = jwtService.generateRefreshToken(accountId)
        jwtService.saveRefreshToken(reissuedRefreshToken)

        return IssuedTokensResponse(reissuedAccessToken, reissuedRefreshToken)
    }
}