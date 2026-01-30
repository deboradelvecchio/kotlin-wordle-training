package com.doctolib.kotlinwordletraining.controller

import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/api/auth")
class AuthController {
    @Value("\${server.servlet.context-path:/}") private lateinit var contextPath: String

    @Value("\${keymock.http.port:8880}") private lateinit var keymockPort: String

    @Value("\${keymock.internal.url:http://localhost:8880}")
    private lateinit var keymockInternalUrl: String

    private val restTemplate = RestTemplate()

    @GetMapping("/login")
    @PreAuthorize("permitAll()")
    fun login(response: HttpServletResponse) {
        val keymockAuthUrl =
            "http://localhost:$keymockPort/realms/doctolib-pro/protocol/openid-connect/auth"
        val clientId = "kotlin-wordle-training"
        val redirectUri = "http://localhost:8080$contextPath/api/auth/callback"
        val scope = "openid profile email"

        val authorizationUrl =
            "$keymockAuthUrl?" +
                "client_id=$clientId&" +
                "redirect_uri=$redirectUri&" +
                "response_type=code&" +
                "scope=$scope"

        response.sendRedirect(authorizationUrl)
    }

    @GetMapping("/callback")
    @PreAuthorize("permitAll()")
    fun callback(
        @RequestParam("code", required = false) code: String?,
        @RequestParam("error", required = false) error: String?,
        response: HttpServletResponse,
    ) {
        if (error != null) {
            response.sendRedirect("http://localhost:5173?error=$error")
            return
        }

        if (code == null) {
            response.sendRedirect("http://localhost:5173?error=missing_code")
            return
        }

        try {
            val tokenEndpoint =
                "$keymockInternalUrl/realms/doctolib-pro/protocol/openid-connect/token"
            val clientId = "kotlin-wordle-training"
            val redirectUri = "http://localhost:8080$contextPath/api/auth/callback"

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

            val formData = LinkedMultiValueMap<String, String>()
            formData.add("grant_type", "authorization_code")
            formData.add("code", code)
            formData.add("client_id", clientId)
            formData.add("redirect_uri", redirectUri)

            val request = HttpEntity(formData, headers)
            val tokenResponse = restTemplate.postForObject(tokenEndpoint, request, Map::class.java)
            val accessToken = tokenResponse?.get("access_token")?.toString()

            if (accessToken != null) {
                response.sendRedirect("http://localhost:5173?token=$accessToken")
            } else {
                response.sendRedirect("http://localhost:5173?error=token_exchange_failed")
            }
        } catch (e: Exception) {
            response.sendRedirect("http://localhost:5173?error=token_exchange_error")
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("permitAll()")
    fun logout(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }
}
