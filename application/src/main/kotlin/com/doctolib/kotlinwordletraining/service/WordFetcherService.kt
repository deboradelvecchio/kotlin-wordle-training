package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.entity.Word
import com.doctolib.kotlinwordletraining.repository.WordRepository
import java.time.LocalDate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Service
class WordFetcherService(
    private val wordRepository: WordRepository,
    private val restClient: RestClient = RestClient.create(),
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val WORD_API_URL = "https://random-word-api.herokuapp.com/word?length=5"
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 1000L
    }

    fun getTodayWord(): Word {
        val today = LocalDate.now()
        return wordRepository.findByGameDate(today) ?: fetchAndStoreWord(today)
    }

    private fun fetchAndStoreWord(gameDate: LocalDate): Word {
        val wordText = fetchWordWithRetry()
        return wordRepository.save(Word(word = wordText, gameDate = gameDate))
    }

    private fun fetchWordWithRetry(): String {
        var lastException: Exception? = null

        repeat(MAX_RETRIES) { attempt ->
            try {
                return fetchWord()
            } catch (e: RestClientException) {
                lastException = e
                logger.warn(
                    "Attempt ${attempt + 1}/$MAX_RETRIES failed to fetch word: ${e.message}"
                )
                if (attempt < MAX_RETRIES - 1) {
                    Thread.sleep(RETRY_DELAY_MS)
                }
            }
        }

        throw WordFetchException("Failed to fetch word after $MAX_RETRIES attempts", lastException)
    }

    private fun fetchWord(): String {
        val response = restClient.get().uri(WORD_API_URL).retrieve().body(Array<String>::class.java)

        val word =
            response?.firstOrNull() ?: throw WordFetchException("Empty response from word API")

        return word.lowercase()
    }
}

class WordFetchException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)
