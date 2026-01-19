package com.doctolib.kotlinwordletraining.controller

import com.doctolib.kotlinwordletraining.model.HealthResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HealthController {

    @GetMapping("/health")
    @PreAuthorize("permitAll()")
    fun health(): HealthResponse {
        return HealthResponse(status = "ok", service = "kotlin-wordle-training")
    }
}
