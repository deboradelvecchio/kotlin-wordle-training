import type { GameState } from '@api/models/WordOfTheDayResponse'

export const GAME_CONSTANTS = {
  MAX_ATTEMPTS: 6,
  WORD_LENGTH: 5,
} as const

export function isGameActive(gameState: GameState): boolean {
  return gameState === 'in_progress' || gameState === 'not_started'
}

export function isGameFinished(gameState: GameState): boolean {
  return gameState === 'won' || gameState === 'lost'
}

export function canSubmitWord(
  currentWord: string,
  gameState: GameState
): boolean {
  return (
    currentWord.length === GAME_CONSTANTS.WORD_LENGTH && isGameActive(gameState)
  )
}

export function canInputLetter(
  currentWord: string,
  gameState: GameState
): boolean {
  return (
    currentWord.length < GAME_CONSTANTS.WORD_LENGTH && isGameActive(gameState)
  )
}
