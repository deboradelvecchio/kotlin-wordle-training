import { useState, useCallback, useMemo } from 'react'
import { useWordOfTheDay } from '@hooks/queries/useWordOfTheDay'
import { useAttemptWord } from '@hooks/queries/useAttemptWord'
import { useKeyboardEvents } from './useKeyboardEvents'
import type { GameState } from '@api/models/WordOfTheDayResponse'
import { canInputLetter, canSubmitWord } from '@utils/gameState'

export function useWordleGameAuthenticated() {
  const { data: wordOfTheDayData, isLoading, error } = useWordOfTheDay()
  const attemptMutation = useAttemptWord()

  const [currentWord, setCurrentWord] = useState('')

  const gameState = useMemo<GameState>(() => {
    return wordOfTheDayData?.gameState || 'not_started'
  }, [wordOfTheDayData])

  const attempts = useMemo(() => {
    return wordOfTheDayData?.attempts || []
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
    if (!canSubmitWord(currentWord, gameState)) {
      return
    }

    attemptMutation.mutate(
      { word: currentWord },
      {
        onSuccess: () => {
          setCurrentWord('')
        },
      }
    )
  }, [currentWord, gameState, attemptMutation])

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
    isAuthenticated: true,
  }
}
