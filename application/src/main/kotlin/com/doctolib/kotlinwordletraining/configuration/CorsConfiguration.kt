package com.doctolib.kotlinwordletraining.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config =
            CorsConfiguration().apply {
                // Allow localhost origins for development
                addAllowedOrigin("http://localhost:5173")
                addAllowedOrigin("http://127.0.0.1:5173")
                addAllowedHeader("*")
                addAllowedMethod("GET")
                addAllowedMethod("POST")
                addAllowedMethod("PUT")
                addAllowedMethod("DELETE")
                addAllowedMethod("OPTIONS")
                addAllowedMethod("PATCH")
                allowCredentials = false
                maxAge = 3600L
            }
        source.registerCorsConfiguration("/**", config)
        return source
    }
}
