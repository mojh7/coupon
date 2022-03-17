package com.mojh.cms.common.config

import com.mojh.cms.security.PERMIT_ALL_GET_URI
import com.mojh.cms.security.PERMIT_ALL_POST_URI
import com.mojh.cms.security.jwt.JwtAuthenticationEntryPoint
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
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http {
            httpBasic { disable() }
            csrf { disable() }
            sessionManagement { SessionCreationPolicy.STATELESS }
            authorizeRequests {
                PERMIT_ALL_GET_URI.forEach { authorize(HttpMethod.GET, it, permitAll) }
                PERMIT_ALL_POST_URI.forEach { authorize(HttpMethod.POST, it, permitAll) }
                authorize(anyRequest, authenticated)
            }
            addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            exceptionHandling {
                authenticationEntryPoint = jwtAuthenticationEntryPoint
            }
        }
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}