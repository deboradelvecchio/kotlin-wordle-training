package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.dto.LeaderboardEntry
import com.doctolib.kotlinwordletraining.dto.LeaderboardResponse
import com.doctolib.kotlinwordletraining.entity.GameState
import com.doctolib.kotlinwordletraining.entity.GameStatus
import com.doctolib.kotlinwordletraining.repository.GameStateRepository
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class LeaderboardService(
    private val wordFetcherService: WordFetcherService,
    private val rankingService: RankingService,
    private val gameStateRepository: GameStateRepository,
) {
    fun getLeaderboard(userId: UUID): LeaderboardResponse {
        val currentWord = wordFetcherService.getCurrentWord()

        val gameStates = gameStateRepository.findByWordIdAndStatus(currentWord.id!!, GameStatus.WON)

        val gameStateByUserId =
            gameStates.associateBy { it.userId }.filter { it.value.userId != null }

        val scores = gameStateByUserId.mapValues { calculateRank(it.value) }
        val sortedScores = scores.toList().sortedByDescending { it.second }
        val rankings =
            sortedScores.mapIndexed { index, (userId, _) ->
                LeaderboardEntry(
                    username = gameStateByUserId[userId]!!.userId.toString(),
                    attempts = gameStateByUserId[userId]!!.attemptsCount,
                    solveTimeSeconds =
                        calculateSolveTimeSeconds(
                            gameStateByUserId[userId]!!.solvedAt!!,
                            gameStateByUserId[userId]!!.startedAt,
                        ),
                    rank = index + 1,
                )
            }
        return LeaderboardResponse(
            entries = rankings,
            currentUserRank =
                sortedScores.indexOfFirst { it.first == userId }.takeIf { it >= 0 }?.let { it + 1 },
        )
    }

    private fun calculateRank(gameState: GameState): Long {
        return rankingService.calculateScore(
            gameState.attemptsCount,
            calculateSolveTimeSeconds(gameState.solvedAt!!, gameState.startedAt),
        )
    }

    private fun calculateSolveTimeSeconds(solvedAt: LocalDateTime, startedAt: LocalDateTime): Long {
        return solvedAt.toEpochSecond(ZoneOffset.UTC) - startedAt.toEpochSecond(ZoneOffset.UTC)
    }
}
