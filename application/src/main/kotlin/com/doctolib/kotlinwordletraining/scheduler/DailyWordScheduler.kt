package com.doctolib.kotlinwordletraining.scheduler

import com.doctolib.kotlinwordletraining.service.WordFetcherService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DailyWordScheduler(private val wordFetcherService: WordFetcherService) {
    @Scheduled(cron = "0 0 0 * * *")
    fun generateDailyWord() {
        wordFetcherService.fetchAndSaveNewWord()
    }
}
