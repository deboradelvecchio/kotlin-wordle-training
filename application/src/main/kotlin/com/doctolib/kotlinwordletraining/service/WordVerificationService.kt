package com.doctolib.kotlinwordletraining.service

import org.springframework.stereotype.Service

// Esempio:
// Target: HELPS
// Guess:  HEPLO
// Feedback: "CCPAA"
// Attenzione ai casi edge:
// Lettere duplicate nel guess
// Lettere duplicate nel target
// Case sensitivity (uppercase / lowercase)

@Service
class WordVerificationService {
    fun verifyWord(target: String, guess: String): String {
        val adjustedTarget = target.uppercase()
        val adjustedGuess = guess.uppercase()
        val feedback = CharArray(5) { 'A' }
        val remainingTarget = adjustedTarget.toMutableList()

        for (i in 0 until 5) {
            if (adjustedGuess[i] == adjustedTarget[i]) {
                feedback[i] = 'C'
                remainingTarget[i] = ' '
            }
        }

        for (i in 0 until 5) {
            if (feedback[i] == 'A') {
                val index = remainingTarget.indexOf(adjustedGuess[i])
                if (index != -1) {
                    feedback[i] = 'P'
                    remainingTarget[index] = ' '
                }
            }
        }

        return String(feedback)
    }
}
