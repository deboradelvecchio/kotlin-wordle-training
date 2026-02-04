package com.doctolib.kotlinwordletraining.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
open class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open val id: Long? = null,
    @Column(nullable = false, unique = true) open var username: String = "",
    @Column(nullable = false, unique = true) open var email: String = "",
    @Column(name = "external_id", nullable = false, unique = true) open val externalId: String = "",
    @Column(name = "created_at", nullable = false, updatable = false)
    open val createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false) open var updatedAt: Instant = Instant.now(),
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}
