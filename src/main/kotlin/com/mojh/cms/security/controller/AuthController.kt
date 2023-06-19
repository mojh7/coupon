package com.mojh.cms.security.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.security.dto.request.LoginRequest
import com.mojh.cms.security.dto.request.LogoutRequest
import com.mojh.cms.security.dto.request.ReissueTokenRequest
import com.mojh.cms.security.service.AuthService
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest) =
        ApiResponse.succeed(authService.login(loginRequest))

    @PostMapping("/logout")
    fun logout(@RequestHeader(AUTHORIZATION) accessToken: String?,
               @Valid @RequestBody logoutRequest: LogoutRequest): ApiResponse<*> {
        authService.logout(accessToken, logoutRequest.refreshToken)
        return ApiResponse.succeed()
    }

    @PostMapping("/reissue")
    fun reissueAccessToken(@RequestHeader(AUTHORIZATION) accessToken: String?,
                           @Valid @RequestBody reissueTokenRequest: ReissueTokenRequest) =
        ApiResponse.succeed(authService.reissueTokens(accessToken, reissueTokenRequest.refreshToken))
}