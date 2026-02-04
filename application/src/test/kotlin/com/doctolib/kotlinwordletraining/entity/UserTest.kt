package com.doctolib.kotlinwordletraining.entity

import java.time.Instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `should create user with required fields`() {
        val user = User(username = "testuser", email = "test@example.com")

        assertThat(user.id).isNull()
        assertThat(user.username).isEqualTo("testuser")
        assertThat(user.email).isEqualTo("test@example.com")
        assertThat(user.externalId).isNull()
        assertThat(user.createdAt).isNotNull()
        assertThat(user.updatedAt).isNotNull()
    }

    @Test
    fun `should create user with all fields`() {
        val now = Instant.now()
        val user =
            User(
                id = 1L,
                username = "testuser",
                email = "test@example.com",
                externalId = "ext-123",
                createdAt = now,
                updatedAt = now,
            )

        assertThat(user.id).isEqualTo(1L)
        assertThat(user.username).isEqualTo("testuser")
        assertThat(user.email).isEqualTo("test@example.com")
        assertThat(user.externalId).isEqualTo("ext-123")
        assertThat(user.createdAt).isEqualTo(now)
        assertThat(user.updatedAt).isEqualTo(now)
    }

    @Test
    fun `should allow updating mutable fields`() {
        val user = User(username = "original", email = "original@example.com")

        user.username = "updated"
        user.email = "updated@example.com"
        user.externalId = "ext-456"
        user.updatedAt = Instant.now()

        assertThat(user.username).isEqualTo("updated")
        assertThat(user.email).isEqualTo("updated@example.com")
        assertThat(user.externalId).isEqualTo("ext-456")
    }
}
