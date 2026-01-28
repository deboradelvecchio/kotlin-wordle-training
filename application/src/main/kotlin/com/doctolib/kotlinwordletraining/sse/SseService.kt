package com.doctolib.kotlinwordletraining.sse

import java.util.concurrent.CopyOnWriteArrayList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class SseService {

    private val logger = LoggerFactory.getLogger(SseService::class.java)
    private val emitters = CopyOnWriteArrayList<SseEmitter>()

    fun createEmitter(): SseEmitter {
        val emitter = SseEmitter(0L)

        emitters.add(emitter)

        emitter.onCompletion { emitters.remove(emitter) }
        emitter.onTimeout { emitters.remove(emitter) }
        emitter.onError { emitters.remove(emitter) }

        logger.info("New SSE client connected. Total clients: {}", emitters.size)
        return emitter
    }

    fun broadcast(eventName: String, data: Any) {
        logger.info("Broadcasting event '{}' to {} clients", eventName, emitters.size)

        emitters.forEach { emitter ->
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data))
            } catch (e: Exception) {
                logger.warn("Failed to send to client, removing: {}", e.message)
                emitters.remove(emitter)
            }
        }
    }
}
