package com.doctolib.kotlinwordletraining.repository

import com.doctolib.kotlinwordletraining.entity.Word
import java.time.LocalDate
import org.springframework.data.jpa.repository.JpaRepository

interface WordRepository : JpaRepository<Word, Long> {
    fun findByWord(word: String): Word?

    fun findByGameDate(gameDate: LocalDate): Word?
}
