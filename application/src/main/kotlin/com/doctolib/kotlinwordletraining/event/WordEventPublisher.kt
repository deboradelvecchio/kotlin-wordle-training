package com.doctolib.kotlinwordletraining.event

import com.doctolib.kotlinwordletraining.entity.Word
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

/**
 * Publishes word events to Kafka following CloudEvents specification.
 *
 * CloudEvents is a specification for describing event data in a common way. See:
 * https://cloudevents.io/
 *
 * Event naming conventions (Doctolib standard):
 * - type: evt.<domain>.<entity>.<action> (e.g., evt.wordle.word.created)
 * - source: doctolib://<app-name>
 * - topic: evt.<domain>.<entity> (type without action)
 *
 * In production, we use the Outbox Pattern with Avro serialization:
 * - Events are saved to an outbox table in the same DB transaction
 * - Debezium CDC picks up changes and publishes to Kafka
 * - This ensures at-least-once delivery without dual-write problems See: doctoboot-outbox module
 */
@Service
class WordEventPublisher(private val kafkaTemplate: KafkaTemplate<String, WordEvent>) {

    companion object {
        const val TOPIC = "evt.wordle.word"
        const val EVENT_TYPE = "evt.wordle.word.created"
        const val EVENT_SOURCE = "doctolib://kotlin-wordle-training"
    }

    fun publishNewWord(word: Word) {
        val event =
            WordEvent(
                data =
                    WordEventData(
                        wordId = word.id ?: throw IllegalStateException("Word ID cannot be null"),
                        word = word.word,
                        createdAt = word.createdAt,
                    )
            )
        kafkaTemplate.send(TOPIC, event)
    }
}

data class WordEvent(
    val id: String = UUID.randomUUID().toString(),
    val specversion: String = "1.0",
    val source: String = WordEventPublisher.EVENT_SOURCE,
    val type: String = WordEventPublisher.EVENT_TYPE,
    val time: Instant = Instant.now(),
    val data: WordEventData,
)

data class WordEventData(val wordId: UUID, val word: String, val createdAt: LocalDateTime)
