package com.doctolib.kotlinwordletraining.controller

import com.doctolib.kotlinwordletraining.dto.AttemptRequest
import com.doctolib.kotlinwordletraining.dto.AttemptResponse
import com.doctolib.kotlinwordletraining.dto.GameStateResponse
import com.doctolib.kotlinwordletraining.service.GameService
import com.doctolib.kotlinwordletraining.service.UserService
import com.doctolib.kotlinwordletraining.util.extractEmail
import com.doctolib.kotlinwordletraining.util.extractExternalId
import com.doctolib.kotlinwordletraining.util.extractUsername
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class GameController(private val gameService: GameService, private val userService: UserService) {

    @GetMapping("/game-state")
    @PreAuthorize("permitAll()")
    fun getGameState(authentication: Authentication?): GameStateResponse {
        return if (authentication != null) {
            val externalId =
                    extractExternalId(authentication) ?: throw RuntimeException("No external ID")
            val username = extractUsername(authentication) ?: "unknown"
            val email = extractEmail(authentication) ?: ""
            val user = userService.findOrCreate(externalId, username, email)
            gameService.getGameStateAuthenticated(userId = user.id!!)
        } else {
            gameService.getGameStateAnonymous()
        }
    }

    @PostMapping("/attempt")
    @PreAuthorize("permitAll()")
    fun submitAttempt(
            @RequestBody request: AttemptRequest,
            authentication: Authentication?,
    ): AttemptResponse {
        return if (authentication != null) {
            val externalId =
                    extractExternalId(authentication) ?: throw RuntimeException("No external ID")
            val username = extractUsername(authentication) ?: "unknown"
            val email = extractEmail(authentication) ?: ""
            val user = userService.findOrCreate(externalId, username, email)

            gameService.submitAttemptAuthenticated(user.id!!, request.guess)
        } else {
            gameService.submitAttemptAnonymous(request.guess)
        }
    }
}
