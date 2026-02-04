package com.doctolib.kotlinwordletraining.repository

import com.doctolib.kotlinwordletraining.entity.GameAttempt
import com.doctolib.kotlinwordletraining.entity.User
import com.doctolib.kotlinwordletraining.entity.Word
import org.springframework.data.jpa.repository.JpaRepository

interface GameAttemptRepository : JpaRepository<GameAttempt, Long> {
    fun findByUserAndWordEntityOrderByAttemptNumberAsc(
        user: User,
        wordEntity: Word,
    ): List<GameAttempt>
}
