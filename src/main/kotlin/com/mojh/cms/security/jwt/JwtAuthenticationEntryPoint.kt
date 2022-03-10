package com.mojh.cms.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.mojh.cms.common.ApiResponse
import org.apache.logging.log4j.LogManager
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.AuthenticationException

@Component
class JwtAuthenticationEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        LOGGER.error(authException)

        with(response) {
            status = UNAUTHORIZED.value()
            contentType = APPLICATION_JSON_VALUE
            val responseBody = objectMapper.writeValueAsString(ApiResponse.failed(UNAUTHORIZED, authException.message))
            writer.print(responseBody)
        }
    }
}