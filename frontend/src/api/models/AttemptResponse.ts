import type { LetterFeedback, GameState, Attempt } from './WordOfTheDayResponse'

export type AttemptResponse = {
  word: string
  feedback: LetterFeedback[]
  isCorrect: boolean
  gameState: GameState
  attemptsRemaining: number
  attempts?: Attempt[]
}
