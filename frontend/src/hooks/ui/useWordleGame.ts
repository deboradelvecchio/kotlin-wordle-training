import { useEffect } from 'react'
import { useAuth } from '@contexts/AuthContext'
import { useWordOfTheDay } from '@hooks/queries/useWordOfTheDay'
import { useWordleGameAnonymous } from './useWordleGameAnonymous'
import { useWordleGameAuthenticated } from './useWordleGameAuthenticated'
import { useSaveGameState } from '@hooks/queries/useSaveGameState'
import {
  getCurrentLocalStorageGameState,
  clearLocalStorageGameState,
} from '@utils/localStorage'

export function useWordleGame() {
  const { isAuthenticated } = useAuth()
  const { data: wordOfTheDayData } = useWordOfTheDay()
  const anonymousGame = useWordleGameAnonymous()
  const authenticatedGame = useWordleGameAuthenticated()
  const saveGameState = useSaveGameState()

  useEffect(() => {
    if (!isAuthenticated || !wordOfTheDayData) {
      return
    }

    const localStorageState = getCurrentLocalStorageGameState()

    if (
      localStorageState &&
      localStorageState.date === wordOfTheDayData.date &&
      localStorageState.attempts.length > 0
    ) {
      saveGameState.mutate({
        attempts: localStorageState.attempts,
        date: localStorageState.date,
      })

      clearLocalStorageGameState()
    }
  }, [isAuthenticated, wordOfTheDayData, saveGameState])

  return isAuthenticated ? authenticatedGame : anonymousGame
}
