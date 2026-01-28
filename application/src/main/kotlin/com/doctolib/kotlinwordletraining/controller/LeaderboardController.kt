package com.doctolib.kotlinwordletraining.controller

import com.doctolib.kotlinwordletraining.dto.LeaderboardResponse
import com.doctolib.kotlinwordletraining.service.LeaderboardService
import com.doctolib.kotlinwordletraining.service.UserService
import com.doctolib.kotlinwordletraining.util.extractExternalId
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class LeaderboardController(
    private val leaderboardService: LeaderboardService,
    private val userService: UserService,
) {

    @GetMapping("/leaderboard")
    @PreAuthorize("isAuthenticated()")
    fun getLeaderboard(authentication: Authentication): LeaderboardResponse {
        val externalId = extractExternalId(authentication)
        val user =
            externalId?.let { userService.find(it) } ?: throw RuntimeException("User not found")
        return leaderboardService.getLeaderboard(user.id!!)
    }
}
