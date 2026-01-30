// file: util/JwtUtils.kt
package com.doctolib.kotlinwordletraining.util

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

/** Top-level functions - idiomatic Kotlin approach */
fun extractExternalId(authentication: Authentication?): String? {
    return authentication?.name
}

fun extractUsername(authentication: Authentication?): String? {
    val jwt = (authentication as? JwtAuthenticationToken)?.token
    return jwt?.claims?.get("preferred_username")?.toString()
}

fun extractEmail(authentication: Authentication?): String? {
    val jwt = (authentication as? JwtAuthenticationToken)?.token
    return jwt?.claims?.get("email")?.toString()
}

/*
 * Alternative approach: Object (more Java-like)
 *
 * object JwtUtils {
 *     fun extractExternalId(authentication: Authentication?): String? { ... }
 *     fun extractUsername(authentication: Authentication?): String? { ... }
 *     fun extractEmail(authentication: Authentication?): String? { ... }
 * }
 *
 * Usage: JwtUtils.extractUsername(auth)
 */
