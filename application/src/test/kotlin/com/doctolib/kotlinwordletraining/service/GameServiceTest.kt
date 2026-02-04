package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.entity.GameState
import com.doctolib.kotlinwordletraining.entity.GameStatus
import com.doctolib.kotlinwordletraining.entity.User
import com.doctolib.kotlinwordletraining.entity.Word
import com.doctolib.kotlinwordletraining.repository.GameAttemptRepository
import com.doctolib.kotlinwordletraining.repository.GameStateRepository
import com.doctolib.kotlinwordletraining.repository.UserRepository
import com.doctolib.kotlinwordletraining.repository.WordRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class GameServiceTest {

    @Mock private lateinit var userRepository: UserRepository

    @Mock private lateinit var wordRepository: WordRepository

    @Mock private lateinit var gameStateRepository: GameStateRepository

    @Mock private lateinit var gameAttemptRepository: GameAttemptRepository

    @Mock private lateinit var mockUser: User

    @Mock private lateinit var mockWord: Word

    private lateinit var gameService: GameService

    @BeforeEach
    fun setup() {
        gameService =
            GameService(userRepository, wordRepository, gameStateRepository, gameAttemptRepository)

        `when`(userRepository.findByExternalId("user123")).thenReturn(mockUser)
        `when`(wordRepository.findByWord("HELLO")).thenReturn(mockWord)
    }

    @Test
    fun `recordAttempt creates new game state when none exists`() {
        val feedbacks =
            listOf(
                LetterFeedback.CORRECT,
                LetterFeedback.PRESENT,
                LetterFeedback.ABSENT,
                LetterFeedback.ABSENT,
                LetterFeedback.ABSENT,
            )

        `when`(gameStateRepository.findByUserAndWord(mockUser, mockWord)).thenReturn(null)
        `when`(gameStateRepository.save(any())).thenAnswer { it.arguments[0] as GameState }

        val result = gameService.recordAttempt("user123", "HELLO", feedbacks)

        verify(gameStateRepository, times(2)).save(any())
        assertThat(result.state).isEqualTo(GameStatus.IN_PROGRESS)
        assertThat(result.attemptsCount).isEqualTo(1)
    }

    @Test
    fun `recordAttempt sets state to WON when all feedbacks are CORRECT`() {
        val existingGameState =
            GameState(
                id = 1L,
                user = mockUser,
                word = mockWord,
                state = GameStatus.IN_PROGRESS,
                attemptsCount = 3,
            )

        val feedbacks =
            listOf(
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
            )

        `when`(gameStateRepository.findByUserAndWord(mockUser, mockWord))
            .thenReturn(existingGameState)
        `when`(gameStateRepository.save(any())).thenAnswer { it.arguments[0] as GameState }

        val result = gameService.recordAttempt("user123", "HELLO", feedbacks)

        assertThat(result.state).isEqualTo(GameStatus.WON)
        assertThat(result.attemptsCount).isEqualTo(4)
    }

    @Test
    fun `recordAttempt sets state to LOST when reaching MAX_ATTEMPTS without winning`() {
        val existingGameState =
            GameState(
                id = 1L,
                user = mockUser,
                word = mockWord,
                state = GameStatus.IN_PROGRESS,
                attemptsCount = 5,
            )

        val feedbacks =
            listOf(
                LetterFeedback.PRESENT,
                LetterFeedback.CORRECT,
                LetterFeedback.ABSENT,
                LetterFeedback.ABSENT,
                LetterFeedback.CORRECT,
            )

        `when`(gameStateRepository.findByUserAndWord(mockUser, mockWord))
            .thenReturn(existingGameState)
        `when`(gameStateRepository.save(any())).thenAnswer { it.arguments[0] as GameState }

        val result = gameService.recordAttempt("user123", "HELLO", feedbacks)

        assertThat(result.state).isEqualTo(GameStatus.LOST)
        assertThat(result.attemptsCount).isEqualTo(6)
    }

    @Test
    fun `recordAttempt keeps state IN_PROGRESS when not winning and not at max attempts`() {
        val existingGameState =
            GameState(
                id = 1L,
                user = mockUser,
                word = mockWord,
                state = GameStatus.IN_PROGRESS,
                attemptsCount = 2,
            )

        val feedbacks =
            listOf(
                LetterFeedback.PRESENT,
                LetterFeedback.ABSENT,
                LetterFeedback.CORRECT,
                LetterFeedback.ABSENT,
                LetterFeedback.PRESENT,
            )

        `when`(gameStateRepository.findByUserAndWord(mockUser, mockWord))
            .thenReturn(existingGameState)
        `when`(gameStateRepository.save(any())).thenAnswer { it.arguments[0] as GameState }

        val result = gameService.recordAttempt("user123", "HELLO", feedbacks)

        assertThat(result.state).isEqualTo(GameStatus.IN_PROGRESS)
        assertThat(result.attemptsCount).isEqualTo(3)
    }

    @Test
    fun `recordAttempt increments attempts count on each call`() {
        val existingGameState =
            GameState(
                id = 1L,
                user = mockUser,
                word = mockWord,
                state = GameStatus.IN_PROGRESS,
                attemptsCount = 0,
            )

        val feedbacks =
            listOf(
                LetterFeedback.ABSENT,
                LetterFeedback.ABSENT,
                LetterFeedback.ABSENT,
                LetterFeedback.ABSENT,
                LetterFeedback.ABSENT,
            )

        `when`(gameStateRepository.findByUserAndWord(mockUser, mockWord))
            .thenReturn(existingGameState)
        `when`(gameStateRepository.save(any())).thenAnswer { it.arguments[0] as GameState }

        val result = gameService.recordAttempt("user123", "HELLO", feedbacks)

        assertThat(result.attemptsCount).isEqualTo(1)
    }

    @Test
    fun `recordAttempt transitions from NOT_STARTED to IN_PROGRESS on first attempt`() {
        val existingGameState =
            GameState(
                id = 1L,
                user = mockUser,
                word = mockWord,
                state = GameStatus.NOT_STARTED,
                attemptsCount = 0,
            )

        val feedbacks =
            listOf(
                LetterFeedback.CORRECT,
                LetterFeedback.ABSENT,
                LetterFeedback.ABSENT,
                LetterFeedback.ABSENT,
                LetterFeedback.ABSENT,
            )

        `when`(gameStateRepository.findByUserAndWord(mockUser, mockWord))
            .thenReturn(existingGameState)
        `when`(gameStateRepository.save(any())).thenAnswer { it.arguments[0] as GameState }

        val result = gameService.recordAttempt("user123", "HELLO", feedbacks)

        assertThat(result.state).isEqualTo(GameStatus.IN_PROGRESS)
        assertThat(result.attemptsCount).isEqualTo(1)
    }

    @Test
    fun `recordAttempt wins on first attempt if all correct`() {
        val existingGameState =
            GameState(
                id = 1L,
                user = mockUser,
                word = mockWord,
                state = GameStatus.NOT_STARTED,
                attemptsCount = 0,
            )

        val feedbacks =
            listOf(
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
                LetterFeedback.CORRECT,
            )

        `when`(gameStateRepository.findByUserAndWord(mockUser, mockWord))
            .thenReturn(existingGameState)
        `when`(gameStateRepository.save(any())).thenAnswer { it.arguments[0] as GameState }

        val result = gameService.recordAttempt("user123", "HELLO", feedbacks)

        assertThat(result.state).isEqualTo(GameStatus.WON)
        assertThat(result.attemptsCount).isEqualTo(1)
    }
}
