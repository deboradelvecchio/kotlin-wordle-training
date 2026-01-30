package com.doctolib.kotlinwordletraining.repository

import com.doctolib.kotlinwordletraining.entity.GameAttempt
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface GameAttemptRepository : JpaRepository<GameAttempt, UUID> {
    fun findByUserIdAndWordIdOrderByAttemptNumberAsc(userId: UUID, wordId: UUID): List<GameAttempt>
}
