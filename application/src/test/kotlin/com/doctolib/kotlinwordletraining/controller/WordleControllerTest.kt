package com.doctolib.kotlinwordletraining.controller

import com.doctolib.kotlinwordletraining.entity.GameState
import com.doctolib.kotlinwordletraining.entity.GameStatus
import com.doctolib.kotlinwordletraining.entity.Word
import com.doctolib.kotlinwordletraining.model.AttemptRequest
import com.doctolib.kotlinwordletraining.service.GameService
import com.doctolib.kotlinwordletraining.service.JwtUtils
import com.doctolib.kotlinwordletraining.service.LetterFeedback
import com.doctolib.kotlinwordletraining.service.ValidationResult
import com.doctolib.kotlinwordletraining.service.WordFetcherService
import com.doctolib.kotlinwordletraining.service.WordVerificationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockitoExtension::class)
class WordleControllerTest {

    @Mock private lateinit var wordVerificationService: WordVerificationService

    @Mock private lateinit var wordFetcherService: WordFetcherService

    @Mock private lateinit var gameService: GameService

    @Mock private lateinit var jwtUtils: JwtUtils

    @Mock private lateinit var mockWord: Word

    @Mock private lateinit var mockGameState: GameState

    private lateinit var wordleController: WordleController

    @BeforeEach
    fun setup() {
        wordleController =
            WordleController(wordVerificationService, wordFetcherService, gameService, jwtUtils)
    }

    @Test
    fun `sendAttempt returns 400 when guess is invalid`() {
        `when`(jwtUtils.getExternalId()).thenReturn("user123")
        `when`(wordVerificationService.validate("WOR"))
            .thenReturn(ValidationResult(false, "Word must be 5 letters"))

        val request = AttemptRequest("WOR")

        val exception =
            assertThrows<ResponseStatusException> { wordleController.sendAttempt(request) }

        assertThat(exception.statusCode.value()).isEqualTo(400)
        assertThat(exception.reason).isEqualTo("Word must be 5 letters")
    }

    @Test
    fun `sendAttempt returns 400 when guess contains non-letters`() {
        `when`(jwtUtils.getExternalId()).thenReturn("user123")
        `when`(wordVerificationService.validate("WOR12"))
            .thenReturn(ValidationResult(false, "Word must contain only letters"))

        val request = AttemptRequest("WOR12")

        val exception =
            assertThrows<ResponseStatusException> { wordleController.sendAttempt(request) }

        assertThat(exception.statusCode.value()).isEqualTo(400)
        assertThat(exception.reason).isEqualTo("Word must contain only letters")
    }

    @Test
    fun `sendAttempt returns 400 when word not in dictionary`() {
        `when`(jwtUtils.getExternalId()).thenReturn("user123")
        `when`(wordVerificationService.validate("ZZZZZ"))
            .thenReturn(ValidationResult(false, "Word not found in dictionary"))

        val request = AttemptRequest("ZZZZZ")

        val exception =
            assertThrows<ResponseStatusException> { wordleController.sendAttempt(request) }

        assertThat(exception.statusCode.value()).isEqualTo(400)
        assertThat(exception.reason).isEqualTo("Word not found in dictionary")
    }

    @Test
    fun `sendAttempt returns 401 when user ID is not found in JWT`() {
        `when`(jwtUtils.getExternalId()).thenReturn(null)

        val request = AttemptRequest("WORLD")

        val exception =
            assertThrows<ResponseStatusException> { wordleController.sendAttempt(request) }

        assertThat(exception.statusCode.value()).isEqualTo(401)
        assertThat(exception.reason).isEqualTo("User ID not found")
    }

    @Test
    fun `sendAttempt returns correct feedback for valid attempt`() {
        val guess = "WORLD"
        val targetWord = "HELLO"
        val feedbacks =
            listOf(
                LetterFeedback.ABSENT,
                LetterFeedback.PRESENT,
                LetterFeedback.ABSENT,
                LetterFeedback.CORRECT,
                LetterFeedback.ABSENT,
            )

        `when`(jwtUtils.getExternalId()).thenReturn("user123")
        `when`(mockWord.word).thenReturn(targetWord)
        `when`(wordVerificationService.validate(guess)).thenReturn(ValidationResult(true))
        `when`(wordFetcherService.getTodayWord()).thenReturn(mockWord)
        `when`(wordVerificationService.calculateFeedback(guess, targetWord)).thenReturn(feedbacks)

        `when`(mockGameState.attemptsCount).thenReturn(1)
        `when`(mockGameState.state).thenReturn(GameStatus.IN_PROGRESS)
        `when`(gameService.recordAttempt(eq("user123"), eq(targetWord), eq(feedbacks)))
            .thenReturn(mockGameState)

        val request = AttemptRequest(guess)
        val response = wordleController.sendAttempt(request)

        assertThat(response.guess).isEqualTo(guess)
        assertThat(response.feedback).isEqualTo(feedbacks)
        assertThat(response.attemptNumber).isEqualTo(1)
        assertThat(response.status).isEqualTo(GameStatus.IN_PROGRESS)
    }

    @Test
    fun `sendAttempt returns WON status when all letters are correct`() {
        val guess = "HELLO"
        val targetWord = "HELLO"
        val feedbacks =
            listOf(
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
            )

        `when`(jwtUtils.getExternalId()).thenReturn("user123")
        `when`(mockWord.word).thenReturn(targetWord)
        `when`(wordVerificationService.validate(guess)).thenReturn(ValidationResult(true))
        `when`(wordFetcherService.getTodayWord()).thenReturn(mockWord)
        `when`(wordVerificationService.calculateFeedback(guess, targetWord)).thenReturn(feedbacks)

        `when`(mockGameState.attemptsCount).thenReturn(3)
        `when`(mockGameState.state).thenReturn(GameStatus.WON)
        `when`(gameService.recordAttempt(eq("user123"), eq(targetWord), eq(feedbacks)))
            .thenReturn(mockGameState)

        val request = AttemptRequest(guess)
        val response = wordleController.sendAttempt(request)

        assertThat(response.status).isEqualTo(GameStatus.WON)
        assertThat(response.attemptNumber).isEqualTo(3)
        assertThat(response.feedback).allMatch { it == LetterFeedback.CORRECT }
    }

    @Test
    fun `sendAttempt returns LOST status when max attempts reached`() {
        val guess = "WORLD"
        val targetWord = "HELLO"
        val feedbacks =
            listOf(
                LetterFeedback.ABSENT,
                LetterFeedback.PRESENT,
                LetterFeedback.ABSENT,
                LetterFeedback.CORRECT,
                LetterFeedback.ABSENT,
            )

        `when`(jwtUtils.getExternalId()).thenReturn("user123")
        `when`(mockWord.word).thenReturn(targetWord)
        `when`(wordVerificationService.validate(guess)).thenReturn(ValidationResult(true))
        `when`(wordFetcherService.getTodayWord()).thenReturn(mockWord)
        `when`(wordVerificationService.calculateFeedback(guess, targetWord)).thenReturn(feedbacks)

        `when`(mockGameState.attemptsCount).thenReturn(6)
        `when`(mockGameState.state).thenReturn(GameStatus.LOST)
        `when`(gameService.recordAttempt(eq("user123"), eq(targetWord), eq(feedbacks)))
            .thenReturn(mockGameState)

        val request = AttemptRequest(guess)
        val response = wordleController.sendAttempt(request)

        assertThat(response.status).isEqualTo(GameStatus.LOST)
        assertThat(response.attemptNumber).isEqualTo(6)
    }

    @Test
    fun `sendAttempt handles multiple attempts with increasing attempt numbers`() {
        val guess = "WORLD"
        val targetWord = "HELLO"
        val feedbacks =
            listOf(
                LetterFeedback.ABSENT,
                LetterFeedback.PRESENT,
                LetterFeedback.ABSENT,
                LetterFeedback.CORRECT,
                LetterFeedback.ABSENT,
            )

        `when`(jwtUtils.getExternalId()).thenReturn("user123")
        `when`(mockWord.word).thenReturn(targetWord)
        `when`(wordVerificationService.validate(guess)).thenReturn(ValidationResult(true))
        `when`(wordFetcherService.getTodayWord()).thenReturn(mockWord)
        `when`(wordVerificationService.calculateFeedback(guess, targetWord)).thenReturn(feedbacks)

        `when`(mockGameState.attemptsCount).thenReturn(4)
        `when`(mockGameState.state).thenReturn(GameStatus.IN_PROGRESS)
        `when`(gameService.recordAttempt(eq("user123"), eq(targetWord), eq(feedbacks)))
            .thenReturn(mockGameState)

        val request = AttemptRequest(guess)
        val response = wordleController.sendAttempt(request)

        assertThat(response.attemptNumber).isEqualTo(4)
        assertThat(response.status).isEqualTo(GameStatus.IN_PROGRESS)
    }

    @Test
    fun `sendAttempt normalizes guess case before processing`() {
        val guess = "WoRlD"
        val targetWord = "HELLO"
        val feedbacks =
            listOf(
                LetterFeedback.ABSENT,
                LetterFeedback.PRESENT,
                LetterFeedback.ABSENT,
                LetterFeedback.CORRECT,
                LetterFeedback.ABSENT,
            )

        `when`(jwtUtils.getExternalId()).thenReturn("user123")
        `when`(mockWord.word).thenReturn(targetWord)
        `when`(wordVerificationService.validate(guess)).thenReturn(ValidationResult(true))
        `when`(wordFetcherService.getTodayWord()).thenReturn(mockWord)
        `when`(wordVerificationService.calculateFeedback(guess, targetWord)).thenReturn(feedbacks)

        `when`(mockGameState.attemptsCount).thenReturn(1)
        `when`(mockGameState.state).thenReturn(GameStatus.IN_PROGRESS)
        `when`(gameService.recordAttempt(eq("user123"), eq(targetWord), eq(feedbacks)))
            .thenReturn(mockGameState)

        val request = AttemptRequest(guess)
        val response = wordleController.sendAttempt(request)

        assertThat(response.guess).isEqualTo(guess)
        assertThat(response.feedback).isEqualTo(feedbacks)
    }
}
