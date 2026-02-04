package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.entity.Word
import com.doctolib.kotlinwordletraining.repository.WordRepository
import java.time.LocalDate
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Service
class WordFetcherService(
    private val wordRepository: WordRepository,
    private val restClient: RestClient = RestClient.create(),
) {
    companion object {
        private const val WORD_API_URL = "https://random-word-api.herokuapp.com/word?length=5"
    }

    fun getTodayWord(): Word {
        val today = LocalDate.now()
        return wordRepository.findByGameDate(today) ?: fetchAndStoreWord(today)
    }

    private fun fetchAndStoreWord(gameDate: LocalDate): Word {
        val wordText = fetchWord()
        return wordRepository.save(Word(word = wordText, gameDate = gameDate))
    }

    @Retryable(
        retryFor = [RestClientException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000),
    )
    fun fetchWord(): String {
        val response = restClient.get().uri(WORD_API_URL).retrieve().body(Array<String>::class.java)

        val word =
            response?.firstOrNull() ?: throw WordFetchException("Empty response from word API")

        return word.lowercase()
    }
}

class WordFetchException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)
