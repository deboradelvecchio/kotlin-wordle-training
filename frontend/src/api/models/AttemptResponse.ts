export type GameStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'WON' | 'LOST'

export type AttemptResponse = {
  guess: string
  feedback: string // "CCPAA" format: C=correct, P=present, A=absent
  attemptNumber: number
  status: GameStatus
}
