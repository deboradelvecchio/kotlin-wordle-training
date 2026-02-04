package com.doctolib.kotlinwordletraining.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class UserUtilsTest {

    @AfterEach
    fun cleanup() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should extract authentication info from JWT with subject and preferred_username`() {
        val jwt =
            Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("oauth-external-123")
                .claim("preferred_username", "johndoe")
                .build()
        val authentication = JwtAuthenticationToken(jwt)
        SecurityContextHolder.getContext().authentication = authentication

        val authInfo = UserUtils.getAuthenticationInfo()

        assertThat(authInfo).isNotNull
        assertThat(authInfo?.externalId).isEqualTo("oauth-external-123")
        assertThat(authInfo?.username).isEqualTo("johndoe")
    }

    @Test
    fun `should extract authentication info from JWT with only subject`() {
        val jwt = Jwt.withTokenValue("token").header("alg", "none").subject("username123").build()
        val authentication = JwtAuthenticationToken(jwt)
        SecurityContextHolder.getContext().authentication = authentication

        val authInfo = UserUtils.getAuthenticationInfo()

        assertThat(authInfo).isNotNull
        assertThat(authInfo?.externalId).isEqualTo("username123")
        assertThat(authInfo?.username).isEqualTo("username123")
    }

    @Test
    fun `should extract authentication info from username password authentication`() {
        val authentication = UsernamePasswordAuthenticationToken("testuser", "password")
        SecurityContextHolder.getContext().authentication = authentication

        val authInfo = UserUtils.getAuthenticationInfo()

        assertThat(authInfo).isNotNull
        assertThat(authInfo?.username).isEqualTo("testuser")
        assertThat(authInfo?.externalId).isNull()
    }

    @Test
    fun `should return null when no authentication in context`() {
        SecurityContextHolder.clearContext()

        val authInfo = UserUtils.getAuthenticationInfo()

        assertThat(authInfo).isNull()
    }

    @Test
    fun `should handle JWT with empty subject gracefully`() {
        val jwt =
            Jwt.withTokenValue("token").header("alg", "none").claim("some_claim", "value").build()
        val authentication = JwtAuthenticationToken(jwt)
        SecurityContextHolder.getContext().authentication = authentication

        val authInfo = UserUtils.getAuthenticationInfo()

        assertThat(authInfo).isNotNull
        assertThat(authInfo?.externalId).isNull()
        assertThat(authInfo?.username).isNull()
    }
}
