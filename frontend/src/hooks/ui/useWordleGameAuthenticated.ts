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
      feedback: Array<{
        letter: string
        status: 'correct' | 'present' | 'absent'
      }>
      attemptNumber: number
    }>
  >([])
  const [localGameState, setLocalGameState] = useState<GameState>('not_started')

  // Convert backend attempts to frontend format
  // Use local attempts only if they extend server state (for optimistic updates)
  const { attempts, isUsingLocalState } = useMemo(() => {
    const serverAttempts = gameStateData?.attempts ?? []
    const serverFormatted = serverAttempts.map((a, index) => ({
      word: a.guess,
      feedback: parseFeedback(a.guess, a.feedback),
      attemptNumber: index + 1,
    }))

    // If local attempts exist and are consistent with server (same prefix), use local
    // This allows optimistic updates while preventing stale data after user change
    if (
      localAttempts.length > 0 &&
      localAttempts.length >= serverFormatted.length
    ) {
      const isConsistent = serverFormatted.every(
        (serverAttempt, i) => localAttempts[i]?.word === serverAttempt.word
      )
      if (isConsistent) {
        return { attempts: localAttempts, isUsingLocalState: true }
      }
    }

    return { attempts: serverFormatted, isUsingLocalState: false }
  }, [gameStateData, localAttempts])

  // Convert backend status to frontend GameState
  const gameState = useMemo<GameState>(() => {
    const serverState = (() => {
      if (!gameStateData) return 'not_started'
      const status = gameStateData.status
      if (status === 'NOT_STARTED') return 'not_started' as GameState
      if (status === 'IN_PROGRESS') return 'in_progress' as GameState
      if (status === 'WON') return 'won' as GameState
      if (status === 'LOST') return 'lost' as GameState
      return 'not_started' as GameState
    })()

    // Only use local state if we're also using local attempts (optimistic update)
    if (localGameState !== 'not_started' && isUsingLocalState) {
      return localGameState
    }

    return serverState
  }, [gameStateData, localGameState, isUsingLocalState])

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
