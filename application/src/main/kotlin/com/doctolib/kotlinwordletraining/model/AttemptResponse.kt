package com.doctolib.kotlinwordletraining.model

import com.doctolib.kotlinwordletraining.entity.GameStatus
import com.doctolib.kotlinwordletraining.service.LetterFeedback

data class AttemptResponse(
    val guess: String,
    val feedback: List<LetterFeedback>,
    val attemptNumber: Int,
    val status: GameStatus,
)
