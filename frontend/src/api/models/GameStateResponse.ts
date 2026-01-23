import type { GameStatus } from './AttemptResponse'

export type AttemptInfo = {
  guess: string
  feedback: string
}

export type GameStateResponse = {
  status: GameStatus
  attemptsCount: number
  attempts: AttemptInfo[]
  maxAttempts: number
}
