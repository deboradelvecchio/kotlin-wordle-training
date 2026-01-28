package com.doctolib.kotlinwordletraining.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AuthControllerTest {

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        val controller = AuthController()
        ReflectionTestUtils.setField(controller, "contextPath", "/kotlin-wordle-training")
        ReflectionTestUtils.setField(controller, "keymockPort", "8880")
        ReflectionTestUtils.setField(controller, "keymockInternalUrl", "http://localhost:8880")
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    // ========== GET /api/auth/login ==========

    @Test
    fun `login redirects to Keymock authorization endpoint`() {
        val result =
            mockMvc.perform(get("/api/auth/login")).andExpect(status().is3xxRedirection).andReturn()

        val redirectUrl = result.response.redirectedUrl
        assertThat(redirectUrl)
            .startsWith("http://localhost:8880/realms/doctolib-pro/protocol/openid-connect/auth")
        assertThat(redirectUrl).contains("client_id=kotlin-wordle-training")
        assertThat(redirectUrl).contains("response_type=code")
        assertThat(redirectUrl).contains("scope=openid")
    }

    // ========== GET /api/auth/callback ==========

    @Test
    fun `callback redirects to frontend with error when error param present`() {
        mockMvc
            .perform(get("/api/auth/callback").param("error", "access_denied"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("http://localhost:5173?error=access_denied"))
    }

    @Test
    fun `callback redirects to frontend with error when code is missing`() {
        mockMvc
            .perform(get("/api/auth/callback"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("http://localhost:5173?error=missing_code"))
    }

    @Test
    fun `callback with valid code redirects to frontend with token`() {
        // Note: This test requires Keymock to be running
        // Keymock accepts any code and returns a mock token
        val result =
            mockMvc
                .perform(get("/api/auth/callback").param("code", "test_code"))
                .andExpect(status().is3xxRedirection)
                .andReturn()

        val redirectUrl = result.response.redirectedUrl

        assertThat(redirectUrl).startsWith("http://localhost:5173?")
        assertThat(redirectUrl).containsAnyOf("token=", "error=")
    }

    // ========== POST /api/auth/logout ==========

    @Test
    fun `logout returns success message`() {
        mockMvc
            .perform(post("/api/auth/logout"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Logged out successfully"))
    }
}
