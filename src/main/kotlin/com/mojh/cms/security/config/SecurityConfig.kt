package com.mojh.cms.security.config

import com.mojh.cms.security.AUTH_GET_URL
import com.mojh.cms.security.AUTH_POST_URL
import com.mojh.cms.security.exception.CustomAccessDeniedHandler
import com.mojh.cms.security.exception.JwtAuthenticationEntryPoint
import com.mojh.cms.security.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http {
            httpBasic { disable() }
            csrf { disable() }
            sessionManagement { SessionCreationPolicy.STATELESS }
            // 구체적인 경로를 더 먼저 선언
            authorizeRequests {
                authorize("/seller/**", hasRole("SELLER"))
                authorize("/customer/**", hasRole("CUSTOMER"))
                AUTH_GET_URL.forEach { authorize(HttpMethod.GET, it, authenticated) }
                AUTH_POST_URL.forEach { authorize(HttpMethod.POST, it, authenticated) }
                authorize(anyRequest, permitAll)
            }
            addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            exceptionHandling {
                authenticationEntryPoint = jwtAuthenticationEntryPoint
                accessDeniedHandler = customAccessDeniedHandler
            }
        }
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}