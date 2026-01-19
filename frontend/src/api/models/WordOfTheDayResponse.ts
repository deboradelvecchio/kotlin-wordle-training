export type WordOfTheDayResponse = {
  word: string
  date: string // ISO date string
  attempts?: Attempt[] // Only if authenticated
  gameState?: GameState // Only if authenticated
}

export type Attempt = {
  word: string
  feedback: LetterFeedback[]
  attemptNumber: number
}

export type LetterFeedback = {
  letter: string
  status: 'correct' | 'present' | 'absent'
}

export type GameState = 'not_started' | 'in_progress' | 'won' | 'lost'
