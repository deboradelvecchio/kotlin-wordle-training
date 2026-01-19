import { useState, useEffect, useCallback } from 'react'
import { useWordOfTheDay } from './useWordOfTheDay'
import { useAttemptWord } from './useAttemptWord'
import type {
  Attempt,
  GameState,
  WordOfTheDayResponse,
} from '../api/models/WordOfTheDayResponse'

const STORAGE_KEY = 'wordle-game-state'
const STORAGE_DATE_KEY = 'wordle-game-date'

type LocalStorageState = {
  attempts: Attempt[]
  currentWord: string
  gameState: GameState
  word: string
  date: string
}

export function useWordleGame() {
  const [currentWord, setCurrentWord] = useState('')
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  // Fetch word of the day
  const { data: wordOfTheDayData, isLoading, error } = useWordOfTheDay()

  // Attempt mutation
  const attemptMutation = useAttemptWord()

  // Check if user is authenticated (has attempts/gameState in response)
  useEffect(() => {
    if (
      wordOfTheDayData?.attempts !== undefined ||
      wordOfTheDayData?.gameState !== undefined
    ) {
      setIsAuthenticated(true)
    } else {
      setIsAuthenticated(false)
    }
  }, [wordOfTheDayData])

  // Load from localStorage if not authenticated
  useEffect(() => {
    if (!isAuthenticated && wordOfTheDayData) {
      const stored = localStorage.getItem(STORAGE_KEY)
      const storedDate = localStorage.getItem(STORAGE_DATE_KEY)

      if (stored && storedDate === wordOfTheDayData.date) {
        try {
          const state: LocalStorageState = JSON.parse(stored)
          if (state.date === wordOfTheDayData.date) {
            setCurrentWord(state.currentWord || '')
          }
        } catch (e) {
          // Invalid stored data, ignore
        }
      } else {
        // New day, clear old state
        localStorage.removeItem(STORAGE_KEY)
        localStorage.removeItem(STORAGE_DATE_KEY)
        setCurrentWord('')
      }
    } else if (isAuthenticated) {
      // Clear localStorage when authenticated
      setCurrentWord('')
    }
  }, [isAuthenticated, wordOfTheDayData])

  // Handle physical keyboard input
  useEffect(() => {
    const gameState =
      isAuthenticated && wordOfTheDayData?.gameState
        ? wordOfTheDayData.gameState
        : (() => {
            const stored = localStorage.getItem(STORAGE_KEY)
            const storedDate = localStorage.getItem(STORAGE_DATE_KEY)
            if (stored && storedDate === wordOfTheDayData?.date) {
              try {
                const state: LocalStorageState = JSON.parse(stored)
                if (
                  state.attempts.length > 0 &&
                  state.gameState === 'not_started'
                ) {
                  return 'in_progress'
                }
                return state.gameState || 'not_started'
              } catch {
                return 'not_started'
              }
            }
            return 'not_started'
          })()

    const handleKeyDown = (e: KeyboardEvent) => {
      if (gameState === 'won' || gameState === 'lost') return

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
  }, [isAuthenticated, wordOfTheDayData])

  const handleLetterInput = useCallback(
    (letter: string) => {
      const currentGameState =
        isAuthenticated && wordOfTheDayData?.gameState
          ? wordOfTheDayData.gameState
          : (() => {
              const stored = localStorage.getItem(STORAGE_KEY)
              const storedDate = localStorage.getItem(STORAGE_DATE_KEY)
              if (stored && storedDate === wordOfTheDayData?.date) {
                try {
                  const state: LocalStorageState = JSON.parse(stored)
                  if (
                    state.attempts.length > 0 &&
                    state.gameState === 'not_started'
                  ) {
                    return 'in_progress'
                  }
                  return state.gameState || 'not_started'
                } catch {
                  return 'not_started'
                }
              }
              return 'not_started'
            })()

      if (
        currentWord.length < 5 &&
        (currentGameState === 'in_progress' ||
          currentGameState === 'not_started')
      ) {
        const newWord = currentWord + letter
        setCurrentWord(newWord)
        // Update localStorage if not authenticated
        if (!isAuthenticated && wordOfTheDayData) {
          const stored = localStorage.getItem(STORAGE_KEY)
          const storedDate = localStorage.getItem(STORAGE_DATE_KEY)
          let state: LocalStorageState

          if (stored && storedDate === wordOfTheDayData.date) {
            state = JSON.parse(stored)
          } else {
            state = {
              attempts: [],
              currentWord: '',
              gameState: 'not_started',
              word: wordOfTheDayData.word,
              date: wordOfTheDayData.date,
            }
          }

          state.currentWord = newWord
          localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
          localStorage.setItem(STORAGE_DATE_KEY, state.date)
        }
      }
    },
    [currentWord, isAuthenticated, wordOfTheDayData]
  )

  const handleBackspace = useCallback(() => {
    if (currentWord.length > 0) {
      const newWord = currentWord.slice(0, -1)
      setCurrentWord(newWord)
      // Update localStorage if not authenticated
      if (!isAuthenticated && wordOfTheDayData) {
        const stored = localStorage.getItem(STORAGE_KEY)
        const storedDate = localStorage.getItem(STORAGE_DATE_KEY)
        if (stored && storedDate === wordOfTheDayData.date) {
          try {
            const state: LocalStorageState = JSON.parse(stored)
            state.currentWord = newWord
            localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
          } catch {
            // Ignore
          }
        }
      }
    }
  }, [currentWord, isAuthenticated, wordOfTheDayData])

  const handleEnter = useCallback(() => {
    const currentGameState =
      isAuthenticated && wordOfTheDayData?.gameState
        ? wordOfTheDayData.gameState
        : (() => {
            const stored = localStorage.getItem(STORAGE_KEY)
            const storedDate = localStorage.getItem(STORAGE_DATE_KEY)
            if (stored && storedDate === wordOfTheDayData?.date) {
              try {
                const state: LocalStorageState = JSON.parse(stored)
                if (
                  state.attempts.length > 0 &&
                  state.gameState === 'not_started'
                ) {
                  return 'in_progress'
                }
                return state.gameState || 'not_started'
              } catch {
                return 'not_started'
              }
            }
            return 'not_started'
          })()

    if (
      currentWord.length === 5 &&
      (currentGameState === 'in_progress' || currentGameState === 'not_started')
    ) {
      attemptMutation.mutate(
        { word: currentWord },
        {
          onSuccess: data => {
            if (!isAuthenticated && wordOfTheDayData) {
              // Update localStorage
              const stored = localStorage.getItem(STORAGE_KEY)
              const storedDate = localStorage.getItem(STORAGE_DATE_KEY)
              let state: LocalStorageState

              if (stored && storedDate === wordOfTheDayData.date) {
                state = JSON.parse(stored)
              } else {
                state = {
                  attempts: [],
                  currentWord: '',
                  gameState: 'not_started',
                  word: wordOfTheDayData.word,
                  date: wordOfTheDayData.date,
                }
              }

              // Add new attempt
              state.attempts.push({
                word: data.word,
                feedback: data.feedback,
                attemptNumber: state.attempts.length + 1,
              })
              state.gameState = data.gameState
              state.currentWord = ''

              localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
              localStorage.setItem(STORAGE_DATE_KEY, state.date)
              setCurrentWord('')
            } else {
              setCurrentWord('')
            }
          },
        }
      )
    }
  }, [currentWord, isAuthenticated, wordOfTheDayData, attemptMutation])

  const attempts: Attempt[] =
    isAuthenticated && wordOfTheDayData?.attempts
      ? wordOfTheDayData.attempts
      : (() => {
          const stored = localStorage.getItem(STORAGE_KEY)
          const storedDate = localStorage.getItem(STORAGE_DATE_KEY)
          if (stored && storedDate === wordOfTheDayData?.date) {
            try {
              const state: LocalStorageState = JSON.parse(stored)
              return state.attempts || []
            } catch {
              return []
            }
          }
          return []
        })()

  const gameState: GameState =
    isAuthenticated && wordOfTheDayData?.gameState
      ? wordOfTheDayData.gameState
      : (() => {
          const stored = localStorage.getItem(STORAGE_KEY)
          const storedDate = localStorage.getItem(STORAGE_DATE_KEY)
          if (stored && storedDate === wordOfTheDayData?.date) {
            try {
              const state: LocalStorageState = JSON.parse(stored)
              // If there are attempts, game is in progress (unless won/lost)
              if (
                state.attempts.length > 0 &&
                state.gameState === 'not_started'
              ) {
                return 'in_progress'
              }
              return state.gameState || 'not_started'
            } catch {
              return 'not_started'
            }
          }
          return 'not_started'
        })()

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
    isAuthenticated,
  }
}
