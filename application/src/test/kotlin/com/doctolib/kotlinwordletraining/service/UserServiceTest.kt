package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@Import(UserService::class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired private lateinit var userService: UserService

    @Autowired private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
    }

    @Test
    fun `should create user`() {
        val user = userService.createUser(username = "testuser", email = "test@example.com")

        assertThat(user.id).isNotNull()
        assertThat(user.username).isEqualTo("testuser")
        assertThat(user.email).isEqualTo("test@example.com")
        assertThat(user.externalId).isNull()
    }

    @Test
    fun `should create user with external id`() {
        val user =
            userService.createUser(
                username = "testuser",
                email = "test@example.com",
                externalId = "ext-123",
            )

        assertThat(user.id).isNotNull()
        assertThat(user.externalId).isEqualTo("ext-123")
    }

    @Test
    fun `should find user by id`() {
        val created = userService.createUser(username = "findme", email = "find@example.com")

        val found = userService.findById(created.id!!)

        assertThat(found).isNotNull
        assertThat(found?.username).isEqualTo("findme")
    }

    @Test
    fun `should return null when finding user by non-existent id`() {
        val found = userService.findById(999L)

        assertThat(found).isNull()
    }

    @Test
    fun `should find user by username`() {
        userService.createUser(username = "searchuser", email = "search@example.com")

        val found = userService.findByUsername("searchuser")

        assertThat(found).isNotNull
        assertThat(found?.email).isEqualTo("search@example.com")
    }

    @Test
    fun `should find user by email`() {
        userService.createUser(username = "emailuser", email = "unique@example.com")

        val found = userService.findByEmail("unique@example.com")

        assertThat(found).isNotNull
        assertThat(found?.username).isEqualTo("emailuser")
    }

    @Test
    fun `should find user by external id`() {
        userService.createUser(
            username = "extuser",
            email = "ext@example.com",
            externalId = "ext-456",
        )

        val found = userService.findByExternalId("ext-456")

        assertThat(found).isNotNull
        assertThat(found?.username).isEqualTo("extuser")
    }

    @Test
    fun `should get all users`() {
        userService.createUser(username = "user1", email = "user1@example.com")
        userService.createUser(username = "user2", email = "user2@example.com")

        val users = userService.getAllUsers()

        assertThat(users).hasSize(2)
        assertThat(users.map { it.username }).containsExactlyInAnyOrder("user1", "user2")
    }

    @Test
    fun `should update user username`() {
        val user = userService.createUser(username = "original", email = "original@example.com")
        val originalUpdatedAt = user.updatedAt

        Thread.sleep(10) // Ensure updatedAt changes

        val updated = userService.updateUser(id = user.id!!, username = "updated")

        assertThat(updated).isNotNull
        assertThat(updated?.username).isEqualTo("updated")
        assertThat(updated?.email).isEqualTo("original@example.com")
        assertThat(updated?.updatedAt).isAfter(originalUpdatedAt)
    }

    @Test
    fun `should update user email`() {
        val user = userService.createUser(username = "user", email = "old@example.com")

        val updated = userService.updateUser(id = user.id!!, email = "new@example.com")

        assertThat(updated?.email).isEqualTo("new@example.com")
        assertThat(updated?.username).isEqualTo("user")
    }

    @Test
    fun `should update user external id`() {
        val user = userService.createUser(username = "user", email = "user@example.com")

        val updated = userService.updateUser(id = user.id!!, externalId = "new-ext-id")

        assertThat(updated?.externalId).isEqualTo("new-ext-id")
    }

    @Test
    fun `should return null when updating non-existent user`() {
        val updated = userService.updateUser(id = 999L, username = "newname")

        assertThat(updated).isNull()
    }

    @Test
    fun `should delete user`() {
        val user = userService.createUser(username = "deleteme", email = "delete@example.com")
        val userId = user.id!!

        val deleted = userService.deleteUser(userId)

        assertThat(deleted).isTrue()
        assertThat(userService.findById(userId)).isNull()
    }

    @Test
    fun `should return false when deleting non-existent user`() {
        val deleted = userService.deleteUser(999L)

        assertThat(deleted).isFalse()
    }

    @Test
    fun `should check if username exists`() {
        userService.createUser(username = "existing", email = "existing@example.com")

        assertThat(userService.existsByUsername("existing")).isTrue()
        assertThat(userService.existsByUsername("nonexistent")).isFalse()
    }

    @Test
    fun `should check if email exists`() {
        userService.createUser(username = "user", email = "exists@example.com")

        assertThat(userService.existsByEmail("exists@example.com")).isTrue()
        assertThat(userService.existsByEmail("notfound@example.com")).isFalse()
    }

    @Test
    fun `should get current user from JWT with external id`() {
        val user =
            userService.createUser(
                username = "jwtuser",
                email = "jwt@example.com",
                externalId = "oauth-123",
            )

        val jwt =
            Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("oauth-123")
                .claim("preferred_username", "jwtuser")
                .build()
        val authentication = JwtAuthenticationToken(jwt)
        SecurityContextHolder.getContext().authentication = authentication

        val currentUser = userService.getCurrentUser()

        assertThat(currentUser).isNotNull
        assertThat(currentUser?.id).isEqualTo(user.id)
        assertThat(currentUser?.externalId).isEqualTo("oauth-123")

        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should get current user from JWT with username claim`() {
        val user = userService.createUser(username = "claimuser", email = "claim@example.com")

        val jwt =
            Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("some-id")
                .claim("preferred_username", "claimuser")
                .build()
        val authentication = JwtAuthenticationToken(jwt)
        SecurityContextHolder.getContext().authentication = authentication

        val currentUser = userService.getCurrentUser()

        assertThat(currentUser).isNotNull
        assertThat(currentUser?.username).isEqualTo("claimuser")

        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should get current user from JWT subject as username`() {
        val user = userService.createUser(username = "subjectuser", email = "subject@example.com")

        val jwt = Jwt.withTokenValue("token").header("alg", "none").subject("subjectuser").build()
        val authentication = JwtAuthenticationToken(jwt)
        SecurityContextHolder.getContext().authentication = authentication

        val currentUser = userService.getCurrentUser()

        assertThat(currentUser).isNotNull
        assertThat(currentUser?.username).isEqualTo("subjectuser")

        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should return null when no authentication in security context`() {
        SecurityContextHolder.clearContext()

        val currentUser = userService.getCurrentUser()

        assertThat(currentUser).isNull()
    }

    @Test
    fun `should return null when user not found in database`() {
        val jwt = Jwt.withTokenValue("token").header("alg", "none").subject("nonexistent").build()
        val authentication = JwtAuthenticationToken(jwt)
        SecurityContextHolder.getContext().authentication = authentication

        val currentUser = userService.getCurrentUser()

        assertThat(currentUser).isNull()

        SecurityContextHolder.clearContext()
    }
}
