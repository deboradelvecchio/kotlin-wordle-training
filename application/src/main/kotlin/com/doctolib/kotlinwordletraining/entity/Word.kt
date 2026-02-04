package com.doctolib.kotlinwordletraining.entity

import com.doctolib.doctoboot.core.annotations.DataTaxonomy
import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "words")
@DataTaxonomy(description = "Daily words for wordle game", owner = "modus")
open class Word(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @DataTaxonomy open val id: Long? = null,
    @Column(nullable = false) @DataTaxonomy open val word: String = "",
    @Column(name = "game_date", nullable = false, unique = true)
    @DataTaxonomy
    open val gameDate: LocalDate = LocalDate.now(),
    @Column(name = "created_at", nullable = false, updatable = false)
    @DataTaxonomy
    open val createdAt: Instant = Instant.now(),
) {
    @OneToMany(mappedBy = "word", fetch = FetchType.LAZY)
    open var gameStates: MutableList<GameState> = mutableListOf()

    @OneToMany(mappedBy = "wordEntity", fetch = FetchType.LAZY)
    open var gameAttempts: MutableList<GameAttempt> = mutableListOf()
}
