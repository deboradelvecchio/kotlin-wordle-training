package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.entity.User
import com.doctolib.kotlinwordletraining.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun findOrCreate(externalId: String, username: String, email: String): User {
        val userFromRepo = userRepository.findByExternalId(externalId)
        return userFromRepo
            ?: userRepository.save(
                User(externalId = externalId, username = username, email = email)
            )
    }

    fun getCurrentUser(): User? {
        val externalId = SecurityContextHolder.getContext().authentication?.name
        return externalId?.let { userRepository.findByExternalId(it) }
    }

    // fun getCurrentUser(): User? {
    //     val authentication = SecurityContextHolder.getContext().authentication
    //     val externalId = authentication?.name
    //     if (externalId != null) {
    //         return userRepository.findByExternalId(externalId)
    //     }
    //     return null
}
