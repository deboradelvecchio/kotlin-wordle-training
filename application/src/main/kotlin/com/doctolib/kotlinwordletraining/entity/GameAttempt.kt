package com.doctolib.kotlinwordletraining.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "game_attempts")
class GameAttempt(
    @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID? = null,
    @Column(name = "user_id") var userId: UUID? = null,
    @Column(name = "word_id") var wordId: UUID? = null,
    @Column(name = "attempt_number") var attemptNumber: Int = 0,
    @Column(name = "created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "word") var word: String = "",
    @Column(name = "feedback") var feedback: String = "",
)
