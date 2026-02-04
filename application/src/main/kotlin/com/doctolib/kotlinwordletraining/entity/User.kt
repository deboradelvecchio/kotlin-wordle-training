package com.doctolib.kotlinwordletraining.entity

import com.doctolib.doctoboot.core.annotations.DataTaxonomy
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
@DataTaxonomy(description = "User account for wordle training app", owner = "modus")
open class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @DataTaxonomy open val id: Long? = null,
    @Column(nullable = false, unique = true)
    @DataTaxonomy(dataSubjectIdentityLikelihood = DataTaxonomy.Level.HIGH)
    open var username: String = "",
    @Column(nullable = false, unique = true)
    @DataTaxonomy(isSecret = true, dataSubjectIdentityLikelihood = DataTaxonomy.Level.HIGH)
    open var email: String = "",
    @Column(name = "external_id", nullable = false, unique = true)
    @DataTaxonomy(dataSubjectIdentityLikelihood = DataTaxonomy.Level.HIGH)
    open val externalId: String = "",
    @Column(name = "created_at", nullable = false, updatable = false)
    @DataTaxonomy
    open val createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false)
    @DataTaxonomy
    open var updatedAt: Instant = Instant.now(),
) {
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    open var gameStates: MutableList<GameState> = mutableListOf()

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    open var gameAttempts: MutableList<GameAttempt> = mutableListOf()

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}
