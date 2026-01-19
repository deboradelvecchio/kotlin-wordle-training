export type WordOfTheDayResponse = {
  date: string
  attempts?: Attempt[]
  gameState?: GameState
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
