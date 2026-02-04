package com.doctolib.kotlinwordletraining.entity

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
        assertThat(user.createdAt).isNull() // Set by @PrePersist
        assertThat(user.updatedAt).isNull() // Set by @PrePersist
    }

    @Test
    fun `should allow updating mutable fields`() {
        val user = User(username = "original", email = "original@example.com")

        user.username = "updated"
        user.email = "updated@example.com"
        user.externalId = "ext-456"

        assertThat(user.username).isEqualTo("updated")
        assertThat(user.email).isEqualTo("updated@example.com")
        assertThat(user.externalId).isEqualTo("ext-456")
    }
}
