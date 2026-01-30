package com.doctolib.kotlinwordletraining.dto

import com.doctolib.kotlinwordletraining.entity.GameStatus

data class AttemptResponse(
    val guess: String = "",
    val feedback: String = "",
    val attemptNumber: Int = 0,
    val status: GameStatus = GameStatus.NOT_STARTED,
)
