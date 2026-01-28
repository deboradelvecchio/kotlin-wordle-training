package com.doctolib.kotlinwordletraining.repository

import com.doctolib.kotlinwordletraining.entity.GameState
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface GameStateRepository : JpaRepository<GameState, UUID> {
    fun findByUserId(userId: UUID): GameState?

    fun findByUserIdAndWordId(userId: UUID, wordId: UUID): GameState?
}
