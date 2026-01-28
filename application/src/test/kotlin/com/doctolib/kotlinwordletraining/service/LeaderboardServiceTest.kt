package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.entity.GameState
import com.doctolib.kotlinwordletraining.entity.GameStatus
import com.doctolib.kotlinwordletraining.entity.Word
import com.doctolib.kotlinwordletraining.repository.GameStateRepository
import java.time.LocalDateTime
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class LeaderboardServiceTest {

    private lateinit var leaderboardService: LeaderboardService
    private lateinit var wordFetcherService: WordFetcherService
    private lateinit var gameStateRepository: GameStateRepository

    private val wordId = UUID.randomUUID()
    private val currentWord = Word(id = wordId, word = "HELLO")

    @BeforeEach
    fun setup() {
        wordFetcherService = mock(WordFetcherService::class.java)
        gameStateRepository = mock(GameStateRepository::class.java)
        val rankingService = RankingService()

        `when`(wordFetcherService.getCurrentWord()).thenReturn(currentWord)

        leaderboardService =
            LeaderboardService(wordFetcherService, rankingService, gameStateRepository)
    }

    @Test
    fun `returns empty leaderboard when no games won`() {
        `when`(gameStateRepository.findByWordIdAndStatus(wordId, GameStatus.WON))
            .thenReturn(emptyList())

        val result = leaderboardService.getLeaderboard(UUID.randomUUID())

        assertThat(result.entries).isEmpty()
        assertThat(result.currentUserRank).isNull()
    }

    @Test
    fun `returns leaderboard sorted by score descending`() {
        val user1 = UUID.randomUUID()
        val user2 = UUID.randomUUID()
        val user3 = UUID.randomUUID()

        val now = LocalDateTime.now()
        val gameStates =
            listOf(
                createGameState(
                    user1,
                    attempts = 4,
                    startedAt = now,
                    solvedAt = now.plusSeconds(60),
                ),
                createGameState(
                    user2,
                    attempts = 2,
                    startedAt = now,
                    solvedAt = now.plusSeconds(120),
                ),
                createGameState(
                    user3,
                    attempts = 3,
                    startedAt = now,
                    solvedAt = now.plusSeconds(30),
                ),
            )

        `when`(gameStateRepository.findByWordIdAndStatus(wordId, GameStatus.WON))
            .thenReturn(gameStates)

        val result = leaderboardService.getLeaderboard(UUID.randomUUID())

        assertThat(result.entries).hasSize(3)
        assertThat(result.entries[0].attempts).isEqualTo(2)
        assertThat(result.entries[0].rank).isEqualTo(1)
        assertThat(result.entries[1].attempts).isEqualTo(3)
        assertThat(result.entries[1].rank).isEqualTo(2)
        assertThat(result.entries[2].attempts).isEqualTo(4)
        assertThat(result.entries[2].rank).isEqualTo(3)
    }

    @Test
    fun `returns currentUserRank when user is in leaderboard`() {
        val currentUser = UUID.randomUUID()
        val otherUser = UUID.randomUUID()

        val now = LocalDateTime.now()
        val gameStates =
            listOf(
                createGameState(
                    otherUser,
                    attempts = 2,
                    startedAt = now,
                    solvedAt = now.plusSeconds(60),
                ),
                createGameState(
                    currentUser,
                    attempts = 3,
                    startedAt = now,
                    solvedAt = now.plusSeconds(60),
                ),
            )

        `when`(gameStateRepository.findByWordIdAndStatus(wordId, GameStatus.WON))
            .thenReturn(gameStates)

        val result = leaderboardService.getLeaderboard(currentUser)

        assertThat(result.currentUserRank).isEqualTo(2)
    }

    @Test
    fun `returns null currentUserRank when user not in leaderboard`() {
        val otherUser = UUID.randomUUID()
        val nonParticipant = UUID.randomUUID()

        val now = LocalDateTime.now()
        val gameStates =
            listOf(
                createGameState(
                    otherUser,
                    attempts = 2,
                    startedAt = now,
                    solvedAt = now.plusSeconds(60),
                )
            )

        `when`(gameStateRepository.findByWordIdAndStatus(wordId, GameStatus.WON))
            .thenReturn(gameStates)

        val result = leaderboardService.getLeaderboard(nonParticipant)

        assertThat(result.currentUserRank).isNull()
    }

    private fun createGameState(
        userId: UUID,
        attempts: Int,
        startedAt: LocalDateTime,
        solvedAt: LocalDateTime,
    ): GameState {
        return GameState(
            id = UUID.randomUUID(),
            userId = userId,
            wordId = wordId,
            status = GameStatus.WON,
            attemptsCount = attempts,
            startedAt = startedAt,
            solvedAt = solvedAt,
        )
    }
}
