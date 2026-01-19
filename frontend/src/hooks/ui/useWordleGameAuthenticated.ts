import { useState, useEffect, useCallback, useMemo } from 'react'
import { useWordOfTheDay } from '@hooks/queries/useWordOfTheDay'
import { useAttemptWord } from '@hooks/queries/useAttemptWord'
import type { GameState } from '@api/models/WordOfTheDayResponse'

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
      if (
        currentWord.length >= 5 ||
        (gameState !== 'in_progress' && gameState !== 'not_started')
      ) {
        return
      }
      setCurrentWord(prev => prev + letter)
    },
    [currentWord.length, gameState]
  )

  const handleBackspace = useCallback(() => {
    if (currentWord.length === 0) return
    setCurrentWord(prev => prev.slice(0, -1))
  }, [currentWord.length])

  const handleEnter = useCallback(() => {
    if (
      currentWord.length !== 5 ||
      (gameState !== 'in_progress' && gameState !== 'not_started')
    ) {
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

  useEffect(() => {
    if (gameState === 'won' || gameState === 'lost') return

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Enter') {
        handleEnter()
      } else if (e.key === 'Backspace') {
        handleBackspace()
      } else if (e.key.length === 1 && /[A-Za-z]/.test(e.key)) {
        handleLetterInput(e.key.toUpperCase())
      }
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [gameState, handleEnter, handleBackspace, handleLetterInput])

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
