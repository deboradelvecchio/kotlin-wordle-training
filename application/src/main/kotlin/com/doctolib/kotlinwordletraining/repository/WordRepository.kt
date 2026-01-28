package com.doctolib.kotlinwordletraining.repository

import com.doctolib.kotlinwordletraining.entity.Word
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface WordRepository : JpaRepository<Word, UUID> {
    fun findTopByOrderByCreatedAtDesc(): Word?
}
