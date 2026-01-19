import type { Attempt, GameState } from './WordOfTheDayResponse'

export type GameStateResponse = {
  gameState: GameState
  attempts: Attempt[]
  word: string
  date: string
}
