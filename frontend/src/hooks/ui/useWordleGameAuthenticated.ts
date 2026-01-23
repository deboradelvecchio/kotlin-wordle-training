import { useState, useCallback, useMemo } from 'react'
import { useAttemptWord } from '@hooks/queries/useAttemptWord'
import { useGameState } from '@hooks/queries/useGameState'
import { useKeyboardEvents } from './useKeyboardEvents'
import type { GameState } from '@api/models/types'
import { canInputLetter, canSubmitWord } from '@utils/gameState'
import { parseFeedback, isCorrectAnswer } from '@utils/feedbackParser'

export function useWordleGameAuthenticated() {
  const { data: gameStateData, isLoading, error, refetch } = useGameState()
  const attemptMutation = useAttemptWord()

  const [currentWord, setCurrentWord] = useState('')
  const [localAttempts, setLocalAttempts] = useState<
    Array<{
      word: string
      feedback: Array<{ letter: string; status: 'correct' | 'present' | 'absent' }>
      attemptNumber: number
    }>
  >([])
  const [localGameState, setLocalGameState] = useState<GameState>('not_started')

  // Convert backend status to frontend GameState
  const gameState = useMemo<GameState>(() => {
    if (localGameState !== 'not_started') {
      return localGameState
    }
    if (!gameStateData) {
      return 'not_started'
    }
    const status = gameStateData.status
    if (status === 'NOT_STARTED') return 'not_started'
    if (status === 'IN_PROGRESS') return 'in_progress'
    if (status === 'WON') return 'won'
    if (status === 'LOST') return 'lost'
    return 'not_started'
  }, [gameStateData, localGameState])

  // Convert backend attempts to frontend format
  const attempts = useMemo(() => {
    if (localAttempts.length > 0) {
      return localAttempts
    }
    if (!gameStateData?.attempts) {
      return []
    }
    return gameStateData.attempts.map((a, index) => ({
      word: a.guess,
      feedback: parseFeedback(a.guess, a.feedback),
      attemptNumber: index + 1,
    }))
  }, [gameStateData, localAttempts])

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

          // Convert backend format to frontend format
          const feedbackArray = parseFeedback(data.guess, data.feedback)
          const newGameState: GameState = isCorrectAnswer(data.feedback)
            ? 'won'
            : data.attemptNumber >= 6
              ? 'lost'
              : 'in_progress'

          setLocalAttempts(prev => [
            ...prev,
            {
              word: data.guess,
              feedback: feedbackArray,
              attemptNumber: data.attemptNumber,
            },
          ])
          setLocalGameState(newGameState)

          // Refetch to sync with server
          refetch()
        },
      }
    )
  }, [currentWord, gameState, attemptMutation, refetch])

  useKeyboardEvents(gameState, {
    onLetterInput: handleLetterInput,
    onBackspace: handleBackspace,
    onEnter: handleEnter,
  })

  return {
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
