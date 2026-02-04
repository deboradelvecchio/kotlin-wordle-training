package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.entity.GameState
import com.doctolib.kotlinwordletraining.entity.GameStatus
import com.doctolib.kotlinwordletraining.entity.User
import com.doctolib.kotlinwordletraining.entity.Word
import com.doctolib.kotlinwordletraining.repository.GameAttemptRepository
import com.doctolib.kotlinwordletraining.repository.GameStateRepository
import com.doctolib.kotlinwordletraining.repository.UserRepository
import com.doctolib.kotlinwordletraining.repository.WordRepository
import org.springframework.stereotype.Service

@Service
class GameService(
    private val userRepository: UserRepository,
    private val wordRepository: WordRepository,
    private val gameStateRepository: GameStateRepository,
    private val gameAttemptRepository: GameAttemptRepository,
) {
    companion object {
        const val MAX_ATTEMPTS = 6
    }

    fun recordAttempt(
        userId: String,
        targetWord: String,
        feedbacks: List<LetterFeedback>,
    ): GameState {
        val userEntity = userRepository.findByExternalId(userId)!!
        val wordEntity = wordRepository.findByWord(targetWord)!!
        val gameStateEntity = findOrCreateState(userEntity, wordEntity)
        val gameStateToPersist = updateGameState(gameStateEntity, feedbacks)
        return gameStateRepository.save(gameStateToPersist)
    }

    private fun updateGameState(
        gameStateEntity: GameState,
        feedbacks: List<LetterFeedback>,
    ): GameState {
        gameStateEntity.attemptsCount += 1
        if (gameStateEntity.attemptsCount > 0) gameStateEntity.state = GameStatus.IN_PROGRESS

        if (feedbacks.all { it == LetterFeedback.CORRECT }) {
            gameStateEntity.state = GameStatus.WON
        } else if (gameStateEntity.attemptsCount >= MAX_ATTEMPTS) {
            gameStateEntity.state = GameStatus.LOST
        }

        return gameStateEntity
    }

    private fun findOrCreateState(userEntity: User, wordEntity: Word): GameState {
        val gameState = gameStateRepository.findByUserAndWord(userEntity, wordEntity)
        if (gameState != null) return gameState

        val newState =
            GameState(
                user = userEntity,
                word = wordEntity,
                state = GameStatus.NOT_STARTED,
                attemptsCount = 0,
            )
        return gameStateRepository.save(newState)
    }
}
