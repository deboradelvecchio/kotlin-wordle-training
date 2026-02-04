package com.doctolib.kotlinwordletraining.util

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

object UserUtils {

    data class AuthenticationInfo(val username: String?, val externalId: String?)

    /**
     * Extracts authentication information from the current SecurityContext.
     *
     * @return AuthenticationInfo with username and/or externalId, or null if no authentication
     *   present
     */
    fun getAuthenticationInfo(): AuthenticationInfo? {
        val authentication = SecurityContextHolder.getContext().authentication ?: return null

        // Handle JWT authentication
        if (authentication.principal is Jwt) {
            val jwt = authentication.principal as Jwt
            val externalId = jwt.subject
            val username = jwt.getClaim<String>("preferred_username") ?: jwt.subject

            return AuthenticationInfo(username = username, externalId = externalId)
        }

        // Handle username/password authentication or other auth types
        val username = authentication.name
        return AuthenticationInfo(username = username, externalId = null)
    }
}
