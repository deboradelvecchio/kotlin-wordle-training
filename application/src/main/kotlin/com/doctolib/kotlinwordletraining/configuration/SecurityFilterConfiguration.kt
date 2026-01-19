package com.doctolib.kotlinwordletraining.configuration

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.web.cors.CorsConfigurationSource

/**
 * Security configuration for HTTP endpoints and filter chains. Configures different security filter
 * chains based on authentication settings, with separate handling for public endpoints and internal
 * interservice communication.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityFilterConfiguration {
    companion object {
        private const val INTERNAL_PATH = "/__internal__/**"
    }

    @Bean
    @ConditionalOnProperty(name = ["doctoboot.authentication.enabled"], havingValue = "false")
    fun openEndpointsSecurityFilterChain(
        http: HttpSecurity,
        corsConfigurationSource: CorsConfigurationSource,
    ): SecurityFilterChain {
        val openEndpointMatchers = NegatedRequestMatcher(AntPathRequestMatcher(INTERNAL_PATH))
        return http
            .securityMatcher(openEndpointMatchers)
            .cors { cors -> cors.configurationSource(corsConfigurationSource) }
            .authorizeHttpRequests { request -> request.anyRequest().permitAll() }
            .csrf { it.disable() }
            .build()
    }

    @Bean
    @ConditionalOnProperty(name = ["doctoboot.authentication.enabled"], havingValue = "true")
    fun openEndpointsSecurityFilterChainWithAnonymousAuth(
        http: HttpSecurity,
        corsConfigurationSource: CorsConfigurationSource,
        @Qualifier("anonymousAuthorizationAuthenticationManagerResolver")
        anonymousAuthenticationManagerResolver: AuthenticationManagerResolver<HttpServletRequest>,
        @Qualifier("httpAuthorizationBearerTokenResolver") bearerTokenResolver: BearerTokenResolver,
    ): SecurityFilterChain {
        val openEndpointMatchers = NegatedRequestMatcher(AntPathRequestMatcher(INTERNAL_PATH))
        return http
            .securityMatcher(openEndpointMatchers)
            .authorizeHttpRequests { request -> request.anyRequest().permitAll() }
            .cors { cors -> cors.configurationSource(corsConfigurationSource) }
            .oauth2ResourceServer { oauth2 ->
                oauth2.authenticationManagerResolver(anonymousAuthenticationManagerResolver)
                oauth2.bearerTokenResolver(bearerTokenResolver)
            }
            .csrf { it.disable() }
            .build()
    }

    @Bean
    @ConditionalOnProperty(name = ["doctoboot.authentication.enabled"], havingValue = "true")
    fun interserviceSecurityFilterJwtAuthorization(
        http: HttpSecurity,
        @Qualifier("interserviceAuthorizationAuthenticationManagerResolver")
        authenticationManagerResolver: AuthenticationManagerResolver<HttpServletRequest>,
        @Qualifier("interserviceAuthorizationBearerTokenResolver")
        bearerTokenResolver: BearerTokenResolver,
    ): SecurityFilterChain {
        return http
            .securityMatcher(INTERNAL_PATH)
            .authorizeHttpRequests { auth -> auth.anyRequest().authenticated() }
            .oauth2ResourceServer { oauth2 ->
                oauth2.authenticationManagerResolver(authenticationManagerResolver)
                oauth2.bearerTokenResolver(bearerTokenResolver)
            }
            .csrf { it.disable() }
            .build()
    }
}
