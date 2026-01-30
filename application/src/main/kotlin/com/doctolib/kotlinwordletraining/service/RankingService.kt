package com.doctolib.kotlinwordletraining.service

import org.springframework.stereotype.Service

@Service
class RankingService {

    companion object {
        const val ATTEMPTS_BASE = 7
        const val ATTEMPTS_WEIGHT = 1000L
    }

    fun calculateScore(attempts: Int, solveTimeSeconds: Long): Long {
        // Priority to few attempts, then to fast solve time
        return (ATTEMPTS_BASE - attempts) * ATTEMPTS_WEIGHT - solveTimeSeconds
    }
}
