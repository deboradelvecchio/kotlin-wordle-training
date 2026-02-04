package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.entity.User
import com.doctolib.kotlinwordletraining.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun findOrCreate(externalId: String, username: String, email: String): User {
        return userRepository.findByExternalId(externalId)
            ?: userRepository.save(
                User(externalId = externalId, username = username, email = email)
            )
    }

    fun getCurrentUser(): User? {
        val jwt =
            SecurityContextHolder.getContext().authentication?.principal as? Jwt ?: return null
        val externalId = jwt.subject ?: return null
        return userRepository.findByExternalId(externalId)
    }
}
