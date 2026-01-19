package com.doctolib.kotlinwordletraining.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    @SerialName("status") val status: String,
    @SerialName("service") val service: String,
)
