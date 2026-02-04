package com.doctolib.kotlinwordletraining.controller

import com.doctolib.kotlinwordletraining.model.AttemptRequest
import com.doctolib.kotlinwordletraining.model.AttemptResponse
import com.doctolib.kotlinwordletraining.service.GameService
import com.doctolib.kotlinwordletraining.service.JwtUtils
import com.doctolib.kotlinwordletraining.service.WordFetcherService
import com.doctolib.kotlinwordletraining.service.WordVerificationService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api")
class WordleController(
    private val wordVerificationService: WordVerificationService,
    private val wordFetcherService: WordFetcherService,
    private val gameService: GameService,
    private val jwtUtils: JwtUtils,
) {
    @PostMapping("/attempt")
    @PreAuthorize("permitAll()")
    fun sendAttempt(@RequestBody request: AttemptRequest): AttemptResponse {
        val userId =
            jwtUtils.getExternalId()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID not found")

        val validationResult = wordVerificationService.validate(request.guess)
        if (!validationResult.valid) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, validationResult.error)
        }

        val todayWord = wordFetcherService.getTodayWord().word
        val feedback = wordVerificationService.calculateFeedback(request.guess, todayWord)
        val gameAttempt = gameService.recordAttempt(userId, todayWord, feedback)

        return AttemptResponse(
            guess = request.guess,
            feedback = feedback,
            attemptNumber = gameAttempt.attemptsCount,
            status = gameAttempt.state,
        )
    }
}
