import type { Attempt, GameState } from '@api/models/WordOfTheDayResponse'

const STORAGE_KEY = 'wordle-game-state'
const STORAGE_DATE_KEY = 'wordle-game-date'

export type LocalStorageGameState = {
  attempts: Attempt[]
  currentWord: string
  gameState: GameState
  date: string
}

export function getLocalStorageGameState(
  date: string
): LocalStorageGameState | null {
  const stored = localStorage.getItem(STORAGE_KEY)
  const storedDate = localStorage.getItem(STORAGE_DATE_KEY)

  if (!stored || storedDate !== date) {
    return null
  }

  try {
    const state = JSON.parse(stored) as LocalStorageGameState
    return state.date === date ? state : null
  } catch {
    return null
  }
}

export function saveLocalStorageGameState(state: LocalStorageGameState): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
  localStorage.setItem(STORAGE_DATE_KEY, state.date)
}

export function createEmptyGameState(date: string): LocalStorageGameState {
  return {
    attempts: [],
    currentWord: '',
    gameState: 'not_started',
    date,
  }
}

export function clearLocalStorageGameState(): void {
  localStorage.removeItem(STORAGE_KEY)
  localStorage.removeItem(STORAGE_DATE_KEY)
}

export function getCurrentLocalStorageGameState(): LocalStorageGameState | null {
  const stored = localStorage.getItem(STORAGE_KEY)
  const storedDate = localStorage.getItem(STORAGE_DATE_KEY)

  if (!stored || !storedDate) {
    return null
  }

  try {
    return JSON.parse(stored) as LocalStorageGameState
  } catch {
    return null
  }
}
