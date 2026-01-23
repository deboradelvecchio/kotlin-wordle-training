import { useCallback, useMemo, useState } from 'react'
import { useAttemptWord } from '@hooks/queries/useAttemptWord'
import { useKeyboardEvents } from './useKeyboardEvents'
import type { GameState, Attempt } from '@api/models/types'
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

  // Load initial state from localStorage
  const initialState = useMemo(() => {
    const state = getLocalStorageGameState(todayDate)
    return state || createEmptyGameState(todayDate)
  }, [todayDate])

  // Use state instead of useMemo to enable re-renders
  const [attempts, setAttempts] = useState<Attempt[]>(initialState.attempts || [])
  const [gameState, setGameState] = useState<GameState>(
    initialState.gameState || 'not_started'
  )

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
          // Convert backend format to frontend format
          const feedbackArray = parseFeedback(data.guess, data.feedback)
          
          // Calculate attempt number locally (backend returns 0 for anonymous)
          const attemptNumber = attempts.length + 1
          
          const newGameState: GameState = isCorrectAnswer(data.feedback)
            ? 'won'
            : attemptNumber >= 6
              ? 'lost'
              : 'in_progress'

          const newAttempt: Attempt = {
            word: data.guess,
            feedback: feedbackArray,
            attemptNumber,
          }

          // Update state
          const newAttempts = [...attempts, newAttempt]
          setAttempts(newAttempts)
          setGameState(newGameState)
          setCurrentWord('')

          // Save to localStorage
          const state = getLocalStorageGameState(todayDate) || createEmptyGameState(todayDate)
          state.attempts = newAttempts
          state.gameState = newGameState
          state.currentWord = ''
          saveLocalStorageGameState(state)
        },
      }
    )
  }, [currentWord, gameState, todayDate, attemptMutation, attempts])

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
