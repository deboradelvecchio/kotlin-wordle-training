package com.doctolib.kotlinwordletraining.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

object UserUtils {

    fun getJwt(): Jwt? = SecurityContextHolder.getContext().authentication?.principal as? Jwt

    fun getExternalId(): String? = getJwt()?.subject

    fun getUsername(): String? = getJwt()?.getClaimAsString("preferred_username")

    fun getEmail(): String? = getJwt()?.getClaimAsString("email")
}
