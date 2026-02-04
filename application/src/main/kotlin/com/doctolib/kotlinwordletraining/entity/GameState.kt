package com.doctolib.kotlinwordletraining.entity

import com.doctolib.doctoboot.core.annotations.DataTaxonomy
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "game_states")
@DataTaxonomy(description = "Game state tracking for wordle sessions", owner = "modus")
open class GameState(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @DataTaxonomy open val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open val user: User? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    open val word: Word? = null,
    @Column(nullable = false) @DataTaxonomy open var state: String = "IN_PROGRESS",
    @Column(name = "attempts_count", nullable = false)
    @DataTaxonomy
    open var attemptsCount: Int = 0,
    @Column(name = "solved_at") @DataTaxonomy open var solvedAt: Instant? = null,
    @Column(name = "started_at", nullable = false, updatable = false)
    @DataTaxonomy
    open val startedAt: Instant = Instant.now(),
)
