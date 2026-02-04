package com.doctolib.kotlinwordletraining.repository

import com.doctolib.kotlinwordletraining.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
    }

    @Test
    fun `should save and retrieve user`() {
        val user = User(username = "testuser", email = "test@example.com")

        // Before save, timestamps should be null
        assertThat(user.createdAt).isNull()
        assertThat(user.updatedAt).isNull()

        val savedUser = userRepository.save(user)

        // After save, timestamps should be set by @CreationTimestamp and @UpdateTimestamp
        assertThat(savedUser.id).isNotNull()
        assertThat(savedUser.username).isEqualTo("testuser")
        assertThat(savedUser.email).isEqualTo("test@example.com")
        assertThat(savedUser.createdAt).isNotNull()
        assertThat(savedUser.updatedAt).isNotNull()
        // Timestamps should be in the same second
        assertThat(savedUser.updatedAt!!.epochSecond).isEqualTo(savedUser.createdAt!!.epochSecond)
    }

    @Test
    fun `should find user by username`() {
        val user = User(username = "findme", email = "findme@example.com")
        userRepository.save(user)

        val found = userRepository.findByUsername("findme")

        assertThat(found).isNotNull
        assertThat(found?.email).isEqualTo("findme@example.com")
    }

    @Test
    fun `should return null when username not found`() {
        val found = userRepository.findByUsername("nonexistent")

        assertThat(found).isNull()
    }

    @Test
    fun `should find user by email`() {
        val user = User(username = "emailuser", email = "unique@example.com")
        userRepository.save(user)

        val found = userRepository.findByEmail("unique@example.com")

        assertThat(found).isNotNull
        assertThat(found?.username).isEqualTo("emailuser")
    }

    @Test
    fun `should return null when email not found`() {
        val found = userRepository.findByEmail("nonexistent@example.com")

        assertThat(found).isNull()
    }

    @Test
    fun `should find user by external id`() {
        val user = User(username = "extuser", email = "ext@example.com", externalId = "ext-123")
        userRepository.save(user)

        val found = userRepository.findByExternalId("ext-123")

        assertThat(found).isNotNull
        assertThat(found?.username).isEqualTo("extuser")
    }

    @Test
    fun `should return null when external id not found`() {
        val found = userRepository.findByExternalId("nonexistent")

        assertThat(found).isNull()
    }

    @Test
    fun `should update user`() {
        val user = User(username = "original", email = "original@example.com")
        val savedUser = userRepository.save(user)

        savedUser.username = "updated"
        savedUser.email = "updated@example.com"
        val updatedUser = userRepository.save(savedUser)

        assertThat(updatedUser.id).isEqualTo(savedUser.id)
        assertThat(updatedUser.username).isEqualTo("updated")
        assertThat(updatedUser.email).isEqualTo("updated@example.com")
        // Note: updated_at is automatically updated via @PreUpdate callback in entity
    }

    @Test
    fun `should delete user`() {
        val user = User(username = "deleteme", email = "delete@example.com")
        val savedUser = userRepository.save(user)
        val userId = savedUser.id!!

        userRepository.deleteById(userId)

        assertThat(userRepository.findById(userId)).isEmpty
    }
}
