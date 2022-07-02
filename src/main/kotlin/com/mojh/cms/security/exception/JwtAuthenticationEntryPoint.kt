package com.mojh.cms.security.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.mojh.cms.common.ApiResponse
import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.security.AUTH_EXCEPTION_INFO
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtAuthenticationEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    override fun commence(request: HttpServletRequest,
                          response: HttpServletResponse,
                          authException: AuthenticationException) {
        val ex: CouponApplicationException? = request.getAttribute(AUTH_EXCEPTION_INFO)?.let{
            it as CouponApplicationException
        }
        val responseBody = if (ex != null) {
            LOGGER.warn(ex)
            objectMapper.writeValueAsString(ApiResponse.failed(ex.errorCode))
        } else {
            LOGGER.warn(authException)
            objectMapper.writeValueAsString(ApiResponse.failed(UNAUTHORIZED, authException.message))
        }

        with(response) {
            status = UNAUTHORIZED.value()
            contentType = APPLICATION_JSON_VALUE
            characterEncoding = StandardCharsets.UTF_8.name();
            writer.print(responseBody)
        }
    }
}