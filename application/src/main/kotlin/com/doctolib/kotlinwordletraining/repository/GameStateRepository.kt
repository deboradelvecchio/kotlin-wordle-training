package com.doctolib.kotlinwordletraining.repository

import com.doctolib.kotlinwordletraining.entity.GameState
import com.doctolib.kotlinwordletraining.entity.User
import com.doctolib.kotlinwordletraining.entity.Word
import org.springframework.data.jpa.repository.JpaRepository

interface GameStateRepository : JpaRepository<GameState, Long> {
    fun findByUserAndWord(user: User, word: Word): GameState?
}
