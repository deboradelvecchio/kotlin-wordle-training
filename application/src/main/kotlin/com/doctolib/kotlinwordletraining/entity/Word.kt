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
@Table(name = "words")
class Word(
    @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID? = null,
    var word: String = "",
    @Column(name = "created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
)
