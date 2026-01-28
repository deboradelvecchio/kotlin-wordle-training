package com.doctolib.kotlinwordletraining.scheduler

import com.doctolib.kotlinwordletraining.entity.Word
import com.doctolib.kotlinwordletraining.event.WordEventPublisher
import com.doctolib.kotlinwordletraining.service.WordFetcherService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DailyWordSchedulerTest {

    @Mock private lateinit var wordFetcherService: WordFetcherService
    @Mock private lateinit var wordEventPublisher: WordEventPublisher

    @InjectMocks private lateinit var scheduler: DailyWordScheduler

    @Test
    fun `generateDailyWord calls fetchAndSaveNewWord and publishes event`() {
        val word = Word(word = "test1")
        `when`(wordFetcherService.fetchAndSaveNewWord()).thenReturn(word)

        scheduler.generateDailyWord()

        verify(wordFetcherService).fetchAndSaveNewWord()
        verify(wordEventPublisher).publishNewWord(word)
    }
}
