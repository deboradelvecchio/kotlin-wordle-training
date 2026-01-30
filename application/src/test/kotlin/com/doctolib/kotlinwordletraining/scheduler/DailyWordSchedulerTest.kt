package com.doctolib.kotlinwordletraining.scheduler

import com.doctolib.kotlinwordletraining.service.WordFetcherService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DailyWordSchedulerTest {

    @Mock private lateinit var wordFetcherService: WordFetcherService

    @InjectMocks private lateinit var scheduler: DailyWordScheduler

    @Test
    fun `generateDailyWord calls fetchAndSaveNewWord`() {
        scheduler.generateDailyWord()

        verify(wordFetcherService).fetchAndSaveNewWord()
    }
}
