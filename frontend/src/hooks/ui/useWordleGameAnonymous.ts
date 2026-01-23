import { useCallback, useMemo, useState } from 'react'
import { useAttemptWord } from '@hooks/queries/useAttemptWord'
import { useKeyboardEvents } from './useKeyboardEvents'
import type { GameState } from '@api/models/types'
import { canInputLetter, canSubmitWord } from '@utils/gameState'
import {
  getLocalStorageGameState,
  saveLocalStorageGameState,
  createEmptyGameState,
} from '@utils/localStorage'
import { parseFeedback, isCorrectAnswer } from '@utils/feedbackParser'

// Get today's date in YYYY-MM-DD format
function getTodayDate(): string {
  return new Date().toISOString().split('T')[0]
}

export function useWordleGameAnonymous() {
  const attemptMutation = useAttemptWord()
  const [currentWord, setCurrentWord] = useState('')
  const todayDate = useMemo(() => getTodayDate(), [])

  const gameState = useMemo<GameState>(() => {
    const state = getLocalStorageGameState(todayDate)
    if (!state) {
      return 'not_started'
    }

    // If there are attempts but state is not_started, game is in progress
    if (state.attempts.length > 0 && state.gameState === 'not_started') {
      return 'in_progress'
    }

    return state.gameState || 'not_started'
  }, [todayDate])

  const attempts = useMemo(() => {
    const state = getLocalStorageGameState(todayDate)
    return state?.attempts || []
  }, [todayDate])

  const handleLetterInput = useCallback(
    (letter: string) => {
      if (!canInputLetter(currentWord, gameState)) {
        return
      }

      setCurrentWord(prev => prev + letter)
    },
    [currentWord, gameState]
  )

  const handleBackspace = useCallback(() => {
    if (currentWord.length === 0) {
      return
    }

    setCurrentWord(prev => prev.slice(0, -1))
  }, [currentWord.length])

  const handleEnter = useCallback(() => {
    if (!canSubmitWord(currentWord, gameState)) {
      return
    }

    attemptMutation.mutate(
      { guess: currentWord },
      {
        onSuccess: data => {
          setCurrentWord('')

          const state =
            getLocalStorageGameState(todayDate) ||
            createEmptyGameState(todayDate)

          // Convert backend format to frontend format
          const feedbackArray = parseFeedback(data.guess, data.feedback)
          const newGameState: GameState = isCorrectAnswer(data.feedback)
            ? 'won'
            : data.attemptNumber >= 6
              ? 'lost'
              : 'in_progress'

          state.attempts.push({
            word: data.guess,
            feedback: feedbackArray,
            attemptNumber: data.attemptNumber,
          })
          state.gameState = newGameState
          state.currentWord = ''

          saveLocalStorageGameState(state)
        },
      }
    )
  }, [currentWord, gameState, todayDate, attemptMutation])

  useKeyboardEvents(gameState, {
    onLetterInput: handleLetterInput,
    onBackspace: handleBackspace,
    onEnter: handleEnter,
  })

  return {
    attempts,
    currentWord,
    gameState,
    isLoading: attemptMutation.isPending,
    error: attemptMutation.error,
    handleLetterInput,
    handleBackspace,
    handleEnter,
    isAuthenticated: false,
  }
}

// Re-export for use in useWordleGame migration
export { getCurrentLocalStorageGameState as getLocalStorageGameState } from '@utils/localStorage'
