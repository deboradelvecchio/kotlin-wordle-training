package com.doctolib.kotlinwordletraining.event

import com.doctolib.kotlinwordletraining.entity.Word
import java.time.LocalDateTime
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.kafka.core.KafkaTemplate

@ExtendWith(MockitoExtension::class)
class WordEventPublisherTest {

    @Mock private lateinit var kafkaTemplate: KafkaTemplate<String, WordEvent>

    @InjectMocks private lateinit var publisher: WordEventPublisher

    @Captor private lateinit var eventCaptor: ArgumentCaptor<WordEvent>

    @Test
    fun `publishNewWord sends event to correct topic with word data`() {
        val wordId = UUID.randomUUID()
        val createdAt = LocalDateTime.now()
        val word = Word(id = wordId, word = "test1", createdAt = createdAt)

        publisher.publishNewWord(word)

        verify(kafkaTemplate)
            .send(org.mockito.ArgumentMatchers.eq(WordEventPublisher.TOPIC), eventCaptor.capture())

        val capturedEvent = eventCaptor.value
        assertThat(capturedEvent.type).isEqualTo(WordEventPublisher.EVENT_TYPE)
        assertThat(capturedEvent.source).isEqualTo(WordEventPublisher.EVENT_SOURCE)
        assertThat(capturedEvent.specversion).isEqualTo("1.0")
        assertThat(capturedEvent.data.wordId).isEqualTo(wordId)
        assertThat(capturedEvent.data.word).isEqualTo("test1")
        assertThat(capturedEvent.data.createdAt).isEqualTo(createdAt)
    }

    @Test
    fun `publishNewWord throws exception when word id is null`() {
        val word = Word(id = null, word = "test1")

        assertThrows<IllegalStateException> { publisher.publishNewWord(word) }
    }
}
