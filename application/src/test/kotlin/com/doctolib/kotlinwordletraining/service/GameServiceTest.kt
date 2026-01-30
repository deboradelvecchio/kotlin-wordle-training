package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.entity.GameState
import com.doctolib.kotlinwordletraining.entity.GameStatus
import com.doctolib.kotlinwordletraining.entity.Word
import com.doctolib.kotlinwordletraining.repository.GameAttemptRepository
import com.doctolib.kotlinwordletraining.repository.GameStateRepository
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class GameServiceTest {

    private val wordFetcherService = mock(WordFetcherService::class.java)
    private val wordVerificationService = mock(WordVerificationService::class.java)
    private val gameStateRepository = mock(GameStateRepository::class.java)
    private val gameAttemptRepository = mock(GameAttemptRepository::class.java)
    private val gameService =
        GameService(
            wordFetcherService,
            wordVerificationService,
            gameStateRepository,
            gameAttemptRepository,
        )

    @Test
    fun `getGameStateAnonymous returns empty state`() {
        val result = gameService.getGameStateAnonymous()

        assertThat(result.status).isEqualTo(GameStatus.NOT_STARTED)
        assertThat(result.attemptsCount).isEqualTo(0)
        assertThat(result.attempts).isEmpty()
        assertThat(result.maxAttempts).isEqualTo(6)
    }

    @Test
    fun `getGameStateAuthenticated returns NOT_STARTED when no game exists`() {
        val wordId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        `when`(wordFetcherService.getCurrentWord()).thenReturn(Word(id = wordId, word = "HELLO"))
        `when`(gameStateRepository.findByUserIdAndWordId(userId, wordId)).thenReturn(null)
        `when`(gameAttemptRepository.findByUserIdAndWordIdOrderByAttemptNumberAsc(userId, wordId))
            .thenReturn(emptyList())

        val result = gameService.getGameStateAuthenticated(userId)

        assertThat(result.status).isEqualTo(GameStatus.NOT_STARTED)
        assertThat(result.attemptsCount).isEqualTo(0)
        assertThat(result.attempts).isEmpty()
        assertThat(result.maxAttempts).isEqualTo(6)
    }

    @Test
    fun `submitAttemptAnonymous returns feedback without saving`() {
        val wordId = UUID.randomUUID()

        `when`(wordFetcherService.getCurrentWord()).thenReturn(Word(id = wordId, word = "HELLO"))
        `when`(wordVerificationService.verifyWord("HELLO", "WORLD")).thenReturn("AAPAA")

        val result = gameService.submitAttemptAnonymous("WORLD")

        assertThat(result.guess).isEqualTo("WORLD")
        assertThat(result.feedback).isEqualTo("AAPAA")
        assertThat(result.attemptNumber).isEqualTo(0)
        assertThat(result.status).isEqualTo(GameStatus.IN_PROGRESS)

        // Verify nothing was saved
        verify(gameStateRepository, never()).save(any())
        verify(gameAttemptRepository, never()).save(any())
    }

    @Test
    fun `submitAttemptAuthenticated creates new game state if none exists`() {
        val wordId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        `when`(wordFetcherService.getCurrentWord()).thenReturn(Word(id = wordId, word = "HELLO"))
        `when`(gameStateRepository.findByUserIdAndWordId(userId, wordId)).thenReturn(null)
        `when`(wordVerificationService.verifyWord("HELLO", "WORLD")).thenReturn("AAPAA")
        `when`(gameStateRepository.save(any())).thenAnswer { invocation ->
            val gameState = invocation.arguments[0] as GameState
            gameState.id = UUID.randomUUID()
            gameState
        }
        `when`(gameAttemptRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = gameService.submitAttemptAuthenticated(userId, "WORLD")

        assertThat(result.guess).isEqualTo("WORLD")
        assertThat(result.feedback).isEqualTo("AAPAA")
        assertThat(result.attemptNumber).isEqualTo(1)
        assertThat(result.status).isEqualTo(GameStatus.IN_PROGRESS)

        // Verify game state and attempt were saved
        verify(gameStateRepository, times(2)).save(any()) // once for create, once for update
        verify(gameAttemptRepository).save(any())
    }

    @Test
    fun `submitAttemptAuthenticated returns WON when guess is correct`() {
        val wordId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        `when`(wordFetcherService.getCurrentWord()).thenReturn(Word(id = wordId, word = "HELLO"))
        `when`(gameStateRepository.findByUserIdAndWordId(userId, wordId))
            .thenReturn(
                GameState(
                    id = UUID.randomUUID(),
                    userId = userId,
                    wordId = wordId,
                    status = GameStatus.IN_PROGRESS,
                    attemptsCount = 2,
                )
            )
        `when`(wordVerificationService.verifyWord("HELLO", "HELLO")).thenReturn("CCCCC")
        `when`(gameStateRepository.save(any())).thenAnswer { it.arguments[0] }
        `when`(gameAttemptRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = gameService.submitAttemptAuthenticated(userId, "HELLO")

        assertThat(result.guess).isEqualTo("HELLO")
        assertThat(result.feedback).isEqualTo("CCCCC")
        assertThat(result.attemptNumber).isEqualTo(3)
        assertThat(result.status).isEqualTo(GameStatus.WON)
    }

    @Test
    fun `submitAttemptAuthenticated returns LOST after 6 attempts`() {
        val wordId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        `when`(wordFetcherService.getCurrentWord()).thenReturn(Word(id = wordId, word = "HELLO"))
        `when`(gameStateRepository.findByUserIdAndWordId(userId, wordId))
            .thenReturn(
                GameState(
                    id = UUID.randomUUID(),
                    userId = userId,
                    wordId = wordId,
                    status = GameStatus.IN_PROGRESS,
                    attemptsCount = 5,
                )
            )
        `when`(wordVerificationService.verifyWord("HELLO", "WRONG")).thenReturn("AAAAA")
        `when`(gameStateRepository.save(any())).thenAnswer { it.arguments[0] }
        `when`(gameAttemptRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = gameService.submitAttemptAuthenticated(userId, "WRONG")

        assertThat(result.guess).isEqualTo("WRONG")
        assertThat(result.feedback).isEqualTo("AAAAA")
        assertThat(result.attemptNumber).isEqualTo(6)
        assertThat(result.status).isEqualTo(GameStatus.LOST)
    }
}
