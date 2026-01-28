package com.doctolib.kotlinwordletraining.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "game_states")
class GameState(
    @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID? = null,
    @Column(name = "user_id") var userId: UUID? = null,
    @Column(name = "word_id") var wordId: UUID? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: GameStatus = GameStatus.NOT_STARTED,
    @Column(name = "attempts_count") var attemptsCount: Int = 0,
    @Column(name = "solved_at") var solvedAt: LocalDateTime? = null,
    @Column(name = "started_at") var startedAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") var updatedAt: LocalDateTime = LocalDateTime.now(),
)
