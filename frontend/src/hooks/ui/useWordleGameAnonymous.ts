import { useEffect, useCallback, useMemo, useState } from 'react'
import { useWordOfTheDay } from '@hooks/queries/useWordOfTheDay'
import { useAttemptWord } from '@hooks/queries/useAttemptWord'
import type { Attempt, GameState } from '@api/models/WordOfTheDayResponse'

const STORAGE_KEY = 'wordle-game-state'
const STORAGE_DATE_KEY = 'wordle-game-date'

type LocalStorageState = {
  attempts: Attempt[]
  currentWord: string
  gameState: GameState
  word: string
  date: string
}

function getLocalStorageState(date: string): LocalStorageState | null {
  const stored = localStorage.getItem(STORAGE_KEY)
  const storedDate = localStorage.getItem(STORAGE_DATE_KEY)

  if (!stored || storedDate !== date) return null

  try {
    const state = JSON.parse(stored) as LocalStorageState
    return state.date === date ? state : null
  } catch {
    return null
  }
}

function saveLocalStorageState(state: LocalStorageState): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
  localStorage.setItem(STORAGE_DATE_KEY, state.date)
}

export function useWordleGameAnonymous() {
  const { data: wordOfTheDayData, isLoading, error } = useWordOfTheDay()
  const attemptMutation = useAttemptWord()

  const [currentWord, setCurrentWord] = useState('')

  const gameState = useMemo<GameState>(() => {
    if (!wordOfTheDayData) return 'not_started'

    const state = getLocalStorageState(wordOfTheDayData.date)
    if (!state) return 'not_started'

    if (state.attempts.length > 0 && state.gameState === 'not_started') {
      return 'in_progress'
    }
    return state.gameState || 'not_started'
  }, [wordOfTheDayData])

  const attempts = useMemo<Attempt[]>(() => {
    if (!wordOfTheDayData) return []
    const state = getLocalStorageState(wordOfTheDayData.date)
    return state?.attempts || []
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
      (gameState !== 'in_progress' && gameState !== 'not_started') ||
      !wordOfTheDayData
    ) {
      return
    }

    attemptMutation.mutate(
      { word: currentWord },
      {
        onSuccess: data => {
          setCurrentWord('')

          const state = getLocalStorageState(wordOfTheDayData.date) || {
            attempts: [],
            currentWord: '',
            gameState: 'not_started',
            word: wordOfTheDayData.word,
            date: wordOfTheDayData.date,
          }

          state.attempts.push({
            word: data.word,
            feedback: data.feedback,
            attemptNumber: state.attempts.length + 1,
          })
          state.gameState = data.gameState
          state.currentWord = ''

          saveLocalStorageState(state)
        },
      }
    )
  }, [currentWord, gameState, wordOfTheDayData, attemptMutation])

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
    isAuthenticated: false,
  }
}

export function getLocalStorageGameState(): LocalStorageState | null {
  const stored = localStorage.getItem(STORAGE_KEY)
  const storedDate = localStorage.getItem(STORAGE_DATE_KEY)

  if (!stored || !storedDate) return null

  try {
    return JSON.parse(stored) as LocalStorageState
  } catch {
    return null
  }
}
