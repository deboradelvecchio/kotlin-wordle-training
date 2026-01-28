package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.entity.Word
import com.doctolib.kotlinwordletraining.repository.WordRepository
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WordFetcherService(
    private val wordRepository: WordRepository,
    @Value("\${wordle.word-validity-hours:24}") private val wordValidityHours: Long,
) {
    private val URL_TEMPLATE = "https://random-word-api.herokuapp.com/word?length=%d"
    private val restTemplate = RestTemplate()

    /**
     * Returns the current word, fetching a new one if needed.
     *
     * RACE CONDITION NOTE: If two users call this method simultaneously when no word exists (or
     * word is expired), both might create a new word. We use @Synchronized to prevent this.
     *
     * Alternative solutions for production/distributed systems:
     * 1. @Synchronized (current) - Works only for single JVM instance
     * 2. Database Lock (@Lock(PESSIMISTIC_WRITE)) - Works with multiple instances, but impacts
     *
     * ```
     *    performance
     * ```
     * 3. Unique constraint + retry - Add unique constraint on date, catch
     *
     * ```
     *    DataIntegrityViolationException
     * ```
     * 4. Scheduled Job (recommended) - Generate words proactively instead of lazy loading
     *
     * In Phase 3, this will be replaced by a Scheduled Job that generates words every 3 hours.
     */
    @Synchronized
    fun getCurrentWord(): Word {
        val word = wordRepository.findTopByOrderByCreatedAtDesc()
        if (
            word == null ||
                word.createdAt.isBefore(LocalDateTime.now().minusHours(wordValidityHours))
        ) {
            return fetchAndSaveNewWord()
        }
        return word
    }

    fun fetchAndSaveNewWord(): Word {
        val word = fetchWord()
        wordRepository.save(word)
        return word
    }

    private fun getUrl(length: Int = 5): String {
        return String.format(URL_TEMPLATE, length)
    }

    private fun fetchWord(length: Int = 5): Word {
        val url = getUrl(length)
        val response = restTemplate.getForObject(url, Array<String>::class.java)
        if (response == null || response.isEmpty()) {
            throw RuntimeException("No word found")
        }
        return Word(word = response[0])
    }
}
