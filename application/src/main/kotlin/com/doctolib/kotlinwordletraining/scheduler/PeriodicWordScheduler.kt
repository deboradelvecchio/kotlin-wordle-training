package com.doctolib.kotlinwordletraining.scheduler

import com.doctolib.kotlinwordletraining.event.WordEventPublisher
import com.doctolib.kotlinwordletraining.service.WordFetcherService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["scheduler.periodic-word.enabled"],
    havingValue = "true",
    matchIfMissing = false,
)
class PeriodicWordScheduler(
    private val wordFetcherService: WordFetcherService,
    private val wordEventPublisher: WordEventPublisher,
) {
    @Scheduled(cron = "0 0 */3 * * *")
    fun generatePeriodicWord() {
        val word = wordFetcherService.fetchAndSaveNewWord()
        wordEventPublisher.publishNewWord(word)
    }
}
