import type { HealthResponse } from '@api/models/HealthResponse'
import type { AttemptResponse } from '@api/models/AttemptResponse'
import type { LeaderboardResponse } from '@api/models/LeaderboardResponse'
import type { GameStateResponse } from '@api/models/GameStateResponse'
import type { Attempt } from '@api/models/types'

export const mockHealthResponse: HealthResponse = {
  status: 'ok',
  service: 'kotlin-wordle-training',
}

export const mockGameStateNotStarted: GameStateResponse = {
  status: 'NOT_STARTED',
  attemptsCount: 0,
  attempts: [],
  maxAttempts: 6,
}

export const mockGameStateInProgress: GameStateResponse = {
  status: 'IN_PROGRESS',
  attemptsCount: 1,
  attempts: [
    {
      guess: 'WORLD',
      feedback: 'APPPA',
    },
  ],
  maxAttempts: 6,
}

export const mockGameStateWon: GameStateResponse = {
  status: 'WON',
  attemptsCount: 2,
  attempts: [
    {
      guess: 'WORLD',
      feedback: 'APPPA',
    },
    {
      guess: 'HELLO',
      feedback: 'CCCCC',
    },
  ],
  maxAttempts: 6,
}

export const mockAttemptResponseCorrect: AttemptResponse = {
  guess: 'HELLO',
  feedback: 'CCCCC',
  attemptNumber: 2,
  status: 'WON',
}

export const mockAttemptResponseIncorrect: AttemptResponse = {
  guess: 'WORLD',
  feedback: 'APPPA',
  attemptNumber: 1,
  status: 'IN_PROGRESS',
}

export const mockLeaderboardResponse: LeaderboardResponse = {
  entries: [
    { rank: 1, username: 'player1', attempts: 3, solveTimeSeconds: 120 },
    { rank: 2, username: 'player2', attempts: 4, solveTimeSeconds: 180 },
    { rank: 3, username: 'player3', attempts: 5, solveTimeSeconds: 240 },
  ],
  currentUserRank: 2,
}

// Helper to create attempts in the old format (for component tests)
export const createMockAttempt = (
  word: string,
  feedbackStatuses: Array<'correct' | 'present' | 'absent'>
): Attempt => ({
  word,
  feedback: feedbackStatuses.map((status, i) => ({
    letter: word[i],
    status,
  })),
  attemptNumber: 1,
})
