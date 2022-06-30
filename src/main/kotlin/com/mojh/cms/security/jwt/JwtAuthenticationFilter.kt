package com.mojh.cms.security.jwt

import com.mojh.cms.common.exception.CouponApplicationException
import com.mojh.cms.common.exception.ErrorCode
import com.mojh.cms.security.AUTH_EXCEPTION_INFO
import com.mojh.cms.security.AUTH_GET_URL
import com.mojh.cms.security.AUTH_POST_URL
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
        try {
            jwtTokenUtils.extractTokenFrom(request.getHeader(AUTHORIZATION))?.let{
                jwtTokenUtils.validateToken(it)
                val accountId = jwtTokenUtils.parseAccountId(it)
                if (jwtTokenUtils.isBlockedAccessToken(it, accountId)) {
                    throw CouponApplicationException(ErrorCode.ALREADY_LOGGED_OUT_MEMBER)
                }

                val memberAdapter = userDetailsServiceImpl.loadUserByUsername(accountId)
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(memberAdapter, null, memberAdapter.authorities)
            }
        } catch (ex: CouponApplicationException) {
            request.setAttribute(AUTH_EXCEPTION_INFO, ex);
        }

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return when(request.method) {
            HttpMethod.GET.toString() -> !Arrays.stream(AUTH_GET_URL)
                .anyMatch { path -> pathMatcher.match(path, request.servletPath) }
            HttpMethod.POST.toString() -> !Arrays.stream(AUTH_POST_URL)
                .anyMatch { path -> pathMatcher.match(path, request.servletPath) }
            else -> true
        }
    }
}