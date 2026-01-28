package com.doctolib.kotlinwordletraining.event

import com.doctolib.kotlinwordletraining.sse.SseService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class WordEventConsumer(private val sseService: SseService) {

    private val logger = LoggerFactory.getLogger(WordEventConsumer::class.java)

    @KafkaListener(topics = [WordEventPublisher.TOPIC], groupId = "wordle-app")
    fun handleWordCreated(event: WordEvent) {
        logger.info(
            "Received word event: id={}, type={}, wordId={}",
            event.id,
            event.type,
            event.data.wordId,
        )

        sseService.broadcast(
            "NEW_WORD_OF_THE_DAY",
            mapOf(
                "type" to "NEW_WORD_OF_THE_DAY",
                "date" to event.data.createdAt.toLocalDate().toString(),
                "timestamp" to System.currentTimeMillis(),
            ),
        )
    }
}
