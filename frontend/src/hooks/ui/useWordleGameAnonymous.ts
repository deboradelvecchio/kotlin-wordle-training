import { useCallback, useMemo, useState } from 'react'
import { useWordOfTheDay } from '@hooks/queries/useWordOfTheDay'
import { useAttemptWord } from '@hooks/queries/useAttemptWord'
import { useKeyboardEvents } from './useKeyboardEvents'
import type { GameState } from '@api/models/WordOfTheDayResponse'
import { canInputLetter, canSubmitWord } from '@utils/gameState'
import {
  getLocalStorageGameState,
  saveLocalStorageGameState,
  createEmptyGameState,
} from '@utils/localStorage'

export function useWordleGameAnonymous() {
  const { data: wordOfTheDayData, isLoading, error } = useWordOfTheDay()
  const attemptMutation = useAttemptWord()

  const [currentWord, setCurrentWord] = useState('')

  const gameState = useMemo<GameState>(() => {
    if (!wordOfTheDayData) {
      return 'not_started'
    }

    const state = getLocalStorageGameState(wordOfTheDayData.date)
    if (!state) {
      return 'not_started'
    }

    // If there are attempts but state is not_started, game is in progress
    if (state.attempts.length > 0 && state.gameState === 'not_started') {
      return 'in_progress'
    }

    return state.gameState || 'not_started'
  }, [wordOfTheDayData])

  const attempts = useMemo(() => {
    if (!wordOfTheDayData) {
      return []
    }

    const state = getLocalStorageGameState(wordOfTheDayData.date)
    return state?.attempts || []
  }, [wordOfTheDayData])

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
    if (!canSubmitWord(currentWord, gameState) || !wordOfTheDayData) {
      return
    }

    attemptMutation.mutate(
      { word: currentWord },
      {
        onSuccess: data => {
          setCurrentWord('')

          const state =
            getLocalStorageGameState(wordOfTheDayData.date) ||
            createEmptyGameState(wordOfTheDayData.date)

          state.attempts.push({
            word: data.word,
            feedback: data.feedback,
            attemptNumber: state.attempts.length + 1,
          })
          state.gameState = data.gameState
          state.currentWord = ''

          saveLocalStorageGameState(state)
        },
      }
    )
  }, [currentWord, gameState, wordOfTheDayData, attemptMutation])

  useKeyboardEvents(gameState, {
    onLetterInput: handleLetterInput,
    onBackspace: handleBackspace,
    onEnter: handleEnter,
  })

  return {
    wordOfTheDay: wordOfTheDayData,
    attempts,
    currentWord,
    gameState,
    isLoading: isLoading || attemptMutation.isPending,
    error: error || attemptMutation.error,
    handleLetterInput,
    handleBackspace,
    handleEnter,
    isAuthenticated: false,
  }
}

// Re-export for use in useWordleGame migration
export { getCurrentLocalStorageGameState as getLocalStorageGameState } from '@utils/localStorage'
