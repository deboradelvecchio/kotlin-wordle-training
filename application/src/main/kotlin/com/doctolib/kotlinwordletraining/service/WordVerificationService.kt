package com.doctolib.kotlinwordletraining.service

import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Service
class WordVerificationService(private val restClient: RestClient = RestClient.create()) {
    companion object {
        private const val DICTIONARY_API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/"
        private const val WORD_LENGTH = 5
    }

    fun validate(word: String): ValidationResult {
        val normalizedWord = word.lowercase().trim()

        if (normalizedWord.length != WORD_LENGTH) {
            return ValidationResult(false, "Word must be $WORD_LENGTH letters")
        }

        if (!normalizedWord.all { it.isLetter() }) {
            return ValidationResult(false, "Word must contain only letters")
        }

        if (!existsInDictionary(normalizedWord)) {
            return ValidationResult(false, "Word not found in dictionary")
        }

        return ValidationResult(true)
    }

    fun calculateFeedback(guess: String, target: String): List<LetterFeedback> {
        val normalizedGuess = guess.lowercase()
        val normalizedTarget = target.lowercase()

        val feedback = MutableList(WORD_LENGTH) { LetterFeedback.ABSENT }
        val targetLetterCounts = normalizedTarget.groupingBy { it }.eachCount().toMutableMap()

        // First pass: mark correct letters
        for (i in 0 until WORD_LENGTH) {
            if (normalizedGuess[i] == normalizedTarget[i]) {
                feedback[i] = LetterFeedback.CORRECT
                targetLetterCounts[normalizedGuess[i]] =
                    targetLetterCounts.getValue(normalizedGuess[i]) - 1
            }
        }

        // Second pass: mark present letters
        for (i in 0 until WORD_LENGTH) {
            if (feedback[i] != LetterFeedback.CORRECT) {
                val guessLetter = normalizedGuess[i]
                val remainingCount = targetLetterCounts.getOrDefault(guessLetter, 0)
                if (remainingCount > 0) {
                    feedback[i] = LetterFeedback.PRESENT
                    targetLetterCounts[guessLetter] = remainingCount - 1
                }
            }
        }

        return feedback
    }

    @Retryable(
        retryFor = [RestClientException::class],
        maxAttempts = 2,
        backoff = Backoff(delay = 500),
    )
    private fun existsInDictionary(word: String): Boolean {
        return try {
            restClient.get().uri("$DICTIONARY_API_URL$word").retrieve().toBodilessEntity()
            true
        } catch (e: RestClientException) {
            if (e.message?.contains("404") == true) {
                false
            } else {
                throw e
            }
        }
    }
}

data class ValidationResult(val valid: Boolean, val error: String? = null)

enum class LetterFeedback {
    CORRECT,
    PRESENT,
    ABSENT,
}
