package com.mojh.cms.security.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.member.dto.LoginRequest
import com.mojh.cms.member.dto.ReissueTokenRequest
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

    @PostMapping("/reissue")
    fun reissueAccessToken(@RequestHeader(AUTHORIZATION) accessToken: String?,
                           @Valid @RequestBody reissueTokenRequest: ReissueTokenRequest) =
        ResponseEntity.ok()
            .header(AUTHORIZATION, authService.reissueAccessToken(accessToken, reissueTokenRequest.refreshToken))
            .body(ApiResponse.succeed());
}