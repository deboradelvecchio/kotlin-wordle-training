package com.doctolib.kotlinwordletraining.controller

import com.doctolib.kotlinwordletraining.event.WordEventPublisher
import com.doctolib.kotlinwordletraining.service.WordFetcherService
import org.springframework.context.annotation.Profile
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
@Profile("dev")
class TestController(
    private val wordFetcherService: WordFetcherService,
    private val wordEventPublisher: WordEventPublisher,
) {

    @PostMapping("/trigger-new-word")
    @PreAuthorize("permitAll()")
    fun triggerNewWord(): Map<String, String> {
        val word = wordFetcherService.fetchAndSaveNewWord()
        wordEventPublisher.publishNewWord(word)
        return mapOf("status" to "ok", "wordId" to word.id.toString())
    }
}
