package com.mojh.cms.security.service

import com.mojh.cms.common.exception.CustomException
import com.mojh.cms.common.exception.ErrorCode.*
import com.mojh.cms.member.dto.LoginRequest
import com.mojh.cms.member.repository.MemberRepository
import com.mojh.cms.security.dto.TokensResponse
import com.mojh.cms.security.jwt.JwtTokenUtils
import com.mojh.cms.security.jwt.TokenStatus
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ValueOperations
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import java.util.concurrent.TimeUnit


@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val redisson: RedissonClient,
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

        redisson.getBucket<String>(loginRequest.accountId)
            .set(refreshToken, REFRESH_TOKEN_VALID_TIME, TimeUnit.MILLISECONDS)

        return TokensResponse(accessToken, refreshToken)
    }

    @Transactional(readOnly = true)
    fun reissueAccessToken(accessToken: String?, refreshToken: String): String {
        val accountId: String = accessToken?.let { jwtTokenUtils.parseAccountId(it) } ?: throw CustomException(INVALID_TOKEN)

        when (jwtTokenUtils.validateTokenStatus(refreshToken)) {
            TokenStatus.EXPIRED -> throw CustomException(EXPIRED_TOKEN)
            TokenStatus.INVALID -> throw CustomException(INVALID_TOKEN)
        }

        redisson.getBucket<String?>(accountId).get()?.let {
            if (it != refreshToken) {
                throw CustomException(INVALID_TOKEN)
            }
        } ?: throw CustomException(LOGIN_REQUIRED)

        return jwtTokenUtils.createAccessToken(accessToken)
    }
}