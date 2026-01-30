package com.doctolib.kotlinwordletraining.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RankingServiceTest {

    private val rankingService = RankingService()

    @Test
    fun `fewer attempts wins over faster time`() {
        val score2attempts = rankingService.calculateScore(attempts = 2, solveTimeSeconds = 120)
        val score3attempts = rankingService.calculateScore(attempts = 3, solveTimeSeconds = 30)

        assertThat(score2attempts).isGreaterThan(score3attempts)
    }

    @Test
    fun `same attempts - faster time wins`() {
        val scoreFast = rankingService.calculateScore(attempts = 3, solveTimeSeconds = 30)
        val scoreSlow = rankingService.calculateScore(attempts = 3, solveTimeSeconds = 120)

        assertThat(scoreFast).isGreaterThan(scoreSlow)
    }

    @Test
    fun `1 attempt is best possible`() {
        val score1 = rankingService.calculateScore(attempts = 1, solveTimeSeconds = 60)
        val score6 = rankingService.calculateScore(attempts = 6, solveTimeSeconds = 60)

        assertThat(score1).isGreaterThan(score6)
    }
}
