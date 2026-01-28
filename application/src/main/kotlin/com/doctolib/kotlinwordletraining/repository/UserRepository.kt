package com.doctolib.kotlinwordletraining.repository

import com.doctolib.kotlinwordletraining.entity.User
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, UUID> {
    fun findByExternalId(externalId: String): User?

    fun existsByExternalId(externalId: String): Boolean

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?
}
