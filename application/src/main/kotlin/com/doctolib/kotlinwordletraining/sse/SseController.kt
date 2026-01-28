package com.doctolib.kotlinwordletraining.sse

import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/events")
class SseController(private val sseService: SseService) {

    @GetMapping("/word-of-the-day", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @PreAuthorize("permitAll()")
    fun wordOfTheDayEvents(): SseEmitter {
        return sseService.createEmitter()
    }
}
