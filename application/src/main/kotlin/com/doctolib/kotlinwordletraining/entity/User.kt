package com.doctolib.kotlinwordletraining.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Table(name = "users")
@Entity
class User(
    @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID? = null,
    @Column(unique = true, name = "external_id") val externalId: String = "",
    @Column(unique = true) var username: String = "",
    @Column(unique = true) var email: String = "",
    @Column(name = "created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at") var updatedAt: LocalDateTime = LocalDateTime.now(),
)
