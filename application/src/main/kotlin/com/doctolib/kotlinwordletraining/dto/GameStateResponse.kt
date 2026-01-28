package com.doctolib.kotlinwordletraining.dto

import com.doctolib.kotlinwordletraining.entity.GameStatus

data class GameStateResponse(
    val status: GameStatus = GameStatus.NOT_STARTED,
    val attemptsCount: Int = 0,
    val attempts: List<AttemptInfo> = emptyList(),
    val maxAttempts: Int = 6,
)

data class AttemptInfo(val guess: String = "", val feedback: String = "")
