package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.entity.User
import com.doctolib.kotlinwordletraining.repository.UserRepository
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class UserServiceTest {

    private val userRepository = mock(UserRepository::class.java)
    private val userService = UserService(userRepository)

    @Test
    fun `findOrCreate returns existing user when found`() {
        `when`(userRepository.findByExternalId("abc-123"))
            .thenReturn(
                User(externalId = "abc-123", username = "testuser", email = "testuser@example.com")
            )
        val user = userService.findOrCreate("abc-123", "testuser", "testuser@example.com")
        verify(userRepository).findByExternalId("abc-123")
        verify(userRepository, never()).save(any())

        assertThat(user.username).isEqualTo("testuser")
        assertThat(user.email).isEqualTo("testuser@example.com")
    }

    @Test
    fun `findOrCreate creates new user when not found`() {
        `when`(userRepository.findByExternalId("abc-123")).thenReturn(null)
        `when`(userRepository.save(any())).thenAnswer { invocation ->
            val user = invocation.arguments[0] as User
            user.id = UUID.randomUUID()
            user
        }
        val user = userService.findOrCreate("abc-123", "testuser", "testuser@example.com")
        verify(userRepository).save(any())

        assertThat(user.username).isEqualTo("testuser")
        assertThat(user.email).isEqualTo("testuser@example.com")
    }
}
