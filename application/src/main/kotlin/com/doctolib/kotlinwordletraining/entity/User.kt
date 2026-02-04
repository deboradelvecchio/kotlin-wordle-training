package com.doctolib.kotlinwordletraining.entity

import com.doctolib.doctoboot.core.annotations.DataTaxonomy
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
@DataTaxonomy(description = "User account for wordle training app", owner = "modus")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @DataTaxonomy val id: Long? = null,
    @Column(nullable = false, unique = true)
    @DataTaxonomy(dataSubjectIdentityLikelihood = DataTaxonomy.Level.HIGH)
    var username: String = "",
    @Column(nullable = false, unique = true)
    @DataTaxonomy(isSecret = true, dataSubjectIdentityLikelihood = DataTaxonomy.Level.HIGH)
    var email: String = "",
    @Column(name = "external_id", nullable = false, unique = true)
    @DataTaxonomy(dataSubjectIdentityLikelihood = DataTaxonomy.Level.HIGH)
    val externalId: String = "",
    @Column(name = "created_at", nullable = false, updatable = false)
    @DataTaxonomy
    val createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false)
    @DataTaxonomy
    var updatedAt: Instant = Instant.now(),
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}
