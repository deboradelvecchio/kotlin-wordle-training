package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.dto.AttemptInfo
import com.doctolib.kotlinwordletraining.dto.AttemptResponse
import com.doctolib.kotlinwordletraining.dto.GameStateResponse
import com.doctolib.kotlinwordletraining.entity.GameAttempt
import com.doctolib.kotlinwordletraining.entity.GameState
import com.doctolib.kotlinwordletraining.entity.GameStatus
import com.doctolib.kotlinwordletraining.repository.GameAttemptRepository
import com.doctolib.kotlinwordletraining.repository.GameStateRepository
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class GameService(
    private val wordFetcherService: WordFetcherService,
    private val wordVerificationService: WordVerificationService,
    private val gameStateRepository: GameStateRepository,
    private val gameAttemptRepository: GameAttemptRepository,
) {
    fun getGameStateAuthenticated(userId: UUID): GameStateResponse {
        val word = wordFetcherService.getCurrentWord()
        val gameState = gameStateRepository.findByUserIdAndWordId(userId, word.id!!)
        val attempts =
            gameAttemptRepository.findByUserIdAndWordIdOrderByAttemptNumberAsc(userId, word.id!!)

        return GameStateResponse(
            status = gameState?.status ?: GameStatus.NOT_STARTED,
            attemptsCount = gameState?.attemptsCount ?: 0,
            attempts = attempts.map { AttemptInfo(guess = it.word, feedback = it.feedback) },
            maxAttempts = 6,
        )
    }

    fun getGameStateAnonymous(): GameStateResponse {
        return GameStateResponse(GameStatus.NOT_STARTED, 0, emptyList(), 6)
    }

    fun submitAttemptAuthenticated(userId: UUID, guess: String): AttemptResponse {
        val word = wordFetcherService.getCurrentWord()

        val gameState =
            gameStateRepository.findByUserIdAndWordId(userId, word.id!!)
                ?: gameStateRepository.save(GameState(userId = userId, wordId = word.id))

        val feedback = wordVerificationService.verifyWord(word.word, guess)

        val attemptNumber = gameState.attemptsCount + 1
        gameAttemptRepository.save(
            GameAttempt(
                userId = userId,
                wordId = word.id,
                attemptNumber = attemptNumber,
                word = guess,
                feedback = feedback,
            )
        )

        gameState.attemptsCount = attemptNumber
        gameState.status =
            when {
                feedback == "CCCCC" -> GameStatus.WON
                attemptNumber >= 6 -> GameStatus.LOST
                else -> GameStatus.IN_PROGRESS
            }
        if (gameState.status == GameStatus.WON) {
            gameState.solvedAt = LocalDateTime.now()
        }
        gameStateRepository.save(gameState)

        return AttemptResponse(guess, feedback, attemptNumber, gameState.status)
    }

    fun submitAttemptAnonymous(guess: String): AttemptResponse {
        val word = wordFetcherService.getCurrentWord()
        val feedback = wordVerificationService.verifyWord(word.word, guess)

        return AttemptResponse(guess, feedback, 0, GameStatus.IN_PROGRESS)
    }
}
