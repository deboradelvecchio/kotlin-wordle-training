package com.doctolib.kotlinwordletraining.repository

import com.doctolib.kotlinwordletraining.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByExternalId(externalId: String): User?

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?
}
