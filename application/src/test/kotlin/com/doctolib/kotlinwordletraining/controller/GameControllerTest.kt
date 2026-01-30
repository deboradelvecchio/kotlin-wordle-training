package com.doctolib.kotlinwordletraining.controller

import com.doctolib.kotlinwordletraining.dto.AttemptRequest
import com.doctolib.kotlinwordletraining.dto.AttemptResponse
import com.doctolib.kotlinwordletraining.dto.GameStateResponse
import com.doctolib.kotlinwordletraining.entity.GameStatus
import com.doctolib.kotlinwordletraining.entity.User
import com.doctolib.kotlinwordletraining.service.GameService
import com.doctolib.kotlinwordletraining.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class GameControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var gameService: GameService
    private lateinit var userService: UserService
    private val objectMapper = ObjectMapper()

    private val userId = UUID.randomUUID()
    private val externalId = "ext-123"
    private val testUser =
        User(
            id = userId,
            externalId = externalId,
            username = "testuser",
            email = "test@example.com",
        )

    @BeforeEach
    fun setup() {
        gameService = mock(GameService::class.java)
        userService = mock(UserService::class.java)

        val controller = GameController(gameService, userService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    // ========== GET /api/game-state ==========

    @Test
    fun `GET game-state returns empty state for anonymous user`() {
        `when`(gameService.getGameStateAnonymous())
            .thenReturn(GameStateResponse(GameStatus.NOT_STARTED, 0, emptyList(), 6))

        mockMvc
            .perform(get("/api/game-state"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("NOT_STARTED"))
            .andExpect(jsonPath("$.attemptsCount").value(0))
            .andExpect(jsonPath("$.maxAttempts").value(6))

        verify(gameService).getGameStateAnonymous()
    }

    @Test
    fun `GET game-state returns user state for authenticated user`() {
        val authentication = createMockAuthentication()
        `when`(userService.findOrCreate(externalId, "testuser", "test@example.com"))
            .thenReturn(testUser)
        `when`(gameService.getGameStateAuthenticated(userId))
            .thenReturn(GameStateResponse(GameStatus.IN_PROGRESS, 2, emptyList(), 6))

        mockMvc
            .perform(get("/api/game-state").principal(authentication))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.attemptsCount").value(2))

        verify(gameService).getGameStateAuthenticated(userId)
        verify(gameService, never()).getGameStateAnonymous()
    }

    // ========== POST /api/attempt ==========

    @Test
    fun `POST attempt returns feedback for anonymous user`() {
        `when`(gameService.submitAttemptAnonymous("HELLO"))
            .thenReturn(AttemptResponse("HELLO", "CCCCC", 0, GameStatus.IN_PROGRESS))

        mockMvc
            .perform(
                post("/api/attempt")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AttemptRequest("HELLO")))
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.guess").value("HELLO"))
            .andExpect(jsonPath("$.feedback").value("CCCCC"))

        verify(gameService).submitAttemptAnonymous("HELLO")
    }

    @Test
    fun `POST attempt returns feedback for authenticated user`() {
        val authentication = createMockAuthentication()
        `when`(userService.findOrCreate(externalId, "testuser", "test@example.com"))
            .thenReturn(testUser)
        `when`(gameService.submitAttemptAuthenticated(userId, "WORLD"))
            .thenReturn(AttemptResponse("WORLD", "AAPCA", 1, GameStatus.IN_PROGRESS))

        mockMvc
            .perform(
                post("/api/attempt")
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(AttemptRequest("WORLD")))
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.guess").value("WORLD"))
            .andExpect(jsonPath("$.feedback").value("AAPCA"))
            .andExpect(jsonPath("$.attemptNumber").value(1))

        verify(gameService).submitAttemptAuthenticated(userId, "WORLD")
    }

    private fun createMockAuthentication(): Authentication {
        val jwt = mock(org.springframework.security.oauth2.jwt.Jwt::class.java)
        `when`(jwt.claims)
            .thenReturn(mapOf("preferred_username" to "testuser", "email" to "test@example.com"))

        val authentication =
            mock(
                org.springframework.security.oauth2.server.resource.authentication
                        .JwtAuthenticationToken::class
                    .java
            )
        `when`(authentication.name).thenReturn(externalId) // <-- Questo è l'external ID!
        `when`(authentication.token).thenReturn(jwt)
        return authentication
    }
}
