import type { LetterFeedback, GameState, Attempt } from './WordOfTheDayResponse'

export type AttemptResponse = {
  word: string // The word attempted
  feedback: LetterFeedback[]
  isCorrect: boolean
  gameState: GameState
  attemptsRemaining: number
  solvedWord?: string // Only returned if game is won (and user is authenticated)
  attempts?: Attempt[] // Complete attempts array (only if authenticated) - avoids extra API call
}
