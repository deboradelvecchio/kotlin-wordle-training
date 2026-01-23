// Core game types used throughout the frontend

export type LetterFeedback = {
  letter: string
  status: 'correct' | 'present' | 'absent'
}

export type Attempt = {
  word: string
  feedback: LetterFeedback[]
  attemptNumber: number
}

export type GameState = 'not_started' | 'in_progress' | 'won' | 'lost'
