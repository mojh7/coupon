package com.mojh.cms.security.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.mojh.cms.common.ApiResponse
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CustomAccessDeniedHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        LOGGER.warn(accessDeniedException.message)

        with(response) {
            status = HttpStatus.FORBIDDEN.value()
            contentType = MediaType.APPLICATION_JSON_VALUE
            characterEncoding = StandardCharsets.UTF_8.name();
            writer.print(objectMapper.writeValueAsString(
                ApiResponse.failed(accessDeniedException.message)))
        }
    }
}