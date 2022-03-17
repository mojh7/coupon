package com.mojh.cms.security.jwt

import com.mojh.cms.common.exception.CustomException
import com.mojh.cms.common.exception.ErrorCode
import com.mojh.cms.security.PERMIT_ALL_GET_URI
import com.mojh.cms.security.PERMIT_ALL_POST_URI
import com.mojh.cms.security.service.UserDetailsServiceImpl
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter(
    private val jwtTokenUtils: JwtTokenUtils,
    private val userDetailsServiceImpl: UserDetailsServiceImpl
) : OncePerRequestFilter() {

    private val pathMatcher: AntPathMatcher = AntPathMatcher()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        jwtTokenUtils.extractTokenFrom(request.getHeader(AUTHORIZATION))?.let{
            val accountId = jwtTokenUtils.parseAccountId(it)
            if (jwtTokenUtils.isBlockedAccessToken(it, accountId)) {
                throw CustomException(ErrorCode.ALREADY_LOGGED_OUT_MEMBER)
            }

            val userAdapter = userDetailsServiceImpl.loadUserByUsername(accountId)
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(userAdapter, null, userAdapter.authorities)
        }

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return when(request.method) {
            HttpMethod.GET.toString() -> Arrays.stream(PERMIT_ALL_GET_URI)
                .anyMatch { path -> pathMatcher.match(path, request.servletPath) }
            HttpMethod.POST.toString() -> Arrays.stream(PERMIT_ALL_POST_URI)
                .anyMatch { path -> pathMatcher.match(path, request.servletPath) }
            else -> false
        }
    }
}