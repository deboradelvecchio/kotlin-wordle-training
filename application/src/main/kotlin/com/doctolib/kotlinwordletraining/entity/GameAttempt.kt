package com.doctolib.kotlinwordletraining.entity

import com.doctolib.doctoboot.core.annotations.DataTaxonomy
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "game_attempts")
@DataTaxonomy(description = "User attempts for wordle game", owner = "modus")
open class GameAttempt(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @DataTaxonomy open val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open val user: User? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    open val wordEntity: Word? = null,
    @Column(nullable = false) @DataTaxonomy open val word: String = "",
    @Column(name = "attempt_number", nullable = false)
    @DataTaxonomy
    open val attemptNumber: Int = 0,
    @Column(nullable = false) @DataTaxonomy open val feedback: String = "",
    @Column(name = "created_at", nullable = false, updatable = false)
    @DataTaxonomy
    open val createdAt: Instant = Instant.now(),
)
