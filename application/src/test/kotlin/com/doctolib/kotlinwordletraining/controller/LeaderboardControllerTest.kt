package com.doctolib.kotlinwordletraining.controller

import com.doctolib.kotlinwordletraining.dto.LeaderboardEntry
import com.doctolib.kotlinwordletraining.dto.LeaderboardResponse
import com.doctolib.kotlinwordletraining.entity.User
import com.doctolib.kotlinwordletraining.service.LeaderboardService
import com.doctolib.kotlinwordletraining.service.UserService
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class LeaderboardControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var leaderboardService: LeaderboardService
    private lateinit var userService: UserService

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
        leaderboardService = mock(LeaderboardService::class.java)
        userService = mock(UserService::class.java)

        val controller = LeaderboardController(leaderboardService, userService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `getLeaderboard returns leaderboard for authenticated user`() {
        val authentication = createMockAuthentication()
        val leaderboardResponse =
            LeaderboardResponse(
                entries =
                    listOf(
                        LeaderboardEntry("player1", 2, 60, 1),
                        LeaderboardEntry("player2", 3, 45, 2),
                    ),
                currentUserRank = 2,
            )

        `when`(userService.find(externalId)).thenReturn(testUser)
        `when`(leaderboardService.getLeaderboard(userId)).thenReturn(leaderboardResponse)

        mockMvc
            .perform(get("/api/leaderboard").principal(authentication))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.entries").isArray)
            .andExpect(jsonPath("$.entries.length()").value(2))
            .andExpect(jsonPath("$.entries[0].username").value("player1"))
            .andExpect(jsonPath("$.entries[0].rank").value(1))
            .andExpect(jsonPath("$.currentUserRank").value(2))
    }

    @Test
    fun `getLeaderboard returns empty leaderboard`() {
        val authentication = createMockAuthentication()
        val leaderboardResponse = LeaderboardResponse(entries = emptyList(), currentUserRank = null)

        `when`(userService.find(externalId)).thenReturn(testUser)
        `when`(leaderboardService.getLeaderboard(userId)).thenReturn(leaderboardResponse)

        mockMvc
            .perform(get("/api/leaderboard").principal(authentication))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.entries").isEmpty)
            .andExpect(jsonPath("$.currentUserRank").doesNotExist())
    }

    private fun createMockAuthentication(): JwtAuthenticationToken {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.claims).thenReturn(mapOf("preferred_username" to "testuser"))

        val authentication = mock(JwtAuthenticationToken::class.java)
        `when`(authentication.name).thenReturn(externalId)
        `when`(authentication.token).thenReturn(jwt)
        return authentication
    }
}
