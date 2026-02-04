package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.entity.User
import com.doctolib.kotlinwordletraining.repository.UserRepository
import com.doctolib.kotlinwordletraining.util.UserUtils
import java.time.Instant
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(private val userRepository: UserRepository) {

    fun createUser(username: String, email: String, externalId: String? = null): User {
        return userRepository.save(
            User(username = username, email = email, externalId = externalId)
        )
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): User? {
        return userRepository.findByIdOrNull(id)
    }

    @Transactional(readOnly = true)
    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    @Transactional(readOnly = true)
    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    @Transactional(readOnly = true)
    fun findByExternalId(externalId: String): User? {
        return userRepository.findByExternalId(externalId)
    }

    @Transactional(readOnly = true)
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun updateUser(
        id: Long,
        username: String? = null,
        email: String? = null,
        externalId: String? = null,
    ): User? {
        val user = userRepository.findByIdOrNull(id) ?: return null

        username?.let { user.username = it }
        email?.let { user.email = it }
        externalId?.let { user.externalId = it }
        user.updatedAt = Instant.now()

        return userRepository.save(user)
    }

    fun deleteUser(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    @Transactional(readOnly = true)
    fun existsByUsername(username: String): Boolean {
        return userRepository.findByUsername(username) != null
    }

    @Transactional(readOnly = true)
    fun existsByEmail(email: String): Boolean {
        return userRepository.findByEmail(email) != null
    }

    @Transactional(readOnly = true)
    fun getCurrentUser(): User? {
        val authInfo = UserUtils.getAuthenticationInfo() ?: return null

        // Try to find user by external_id first (common with OAuth providers)
        authInfo.externalId?.let { externalId ->
            userRepository.findByExternalId(externalId)?.let {
                return it
            }
        }

        // Fall back to username
        authInfo.username?.let { username ->
            return userRepository.findByUsername(username)
        }

        return null
    }
}
