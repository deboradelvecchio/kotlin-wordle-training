import type { HealthResponse } from '@api/models/HealthResponse'
import type { WordOfTheDayResponse } from '@api/models/WordOfTheDayResponse'
import type { AttemptResponse } from '@api/models/AttemptResponse'
import type { LeaderboardResponse } from '@api/models/LeaderboardResponse'

export const mockHealthResponse: HealthResponse = {
  status: 'ok',
  service: 'kotlin-wordle-training',
}

export const mockWordOfTheDayResponse: WordOfTheDayResponse = {
  date: '2025-01-15',
  gameState: 'not_started',
  attempts: [],
}

export const mockWordOfTheDayInProgress: WordOfTheDayResponse = {
  date: '2025-01-15',
  gameState: 'in_progress',
  attempts: [
    {
      word: 'WORLD',
      feedback: [
        { letter: 'W', status: 'absent' },
        { letter: 'O', status: 'present' },
        { letter: 'R', status: 'absent' },
        { letter: 'L', status: 'present' },
        { letter: 'D', status: 'absent' },
      ],
      attemptNumber: 1,
    },
  ],
}

export const mockWordOfTheDayWon: WordOfTheDayResponse = {
  date: '2025-01-15',
  gameState: 'won',
  attempts: [
    {
      word: 'WORLD',
      feedback: [
        { letter: 'W', status: 'absent' },
        { letter: 'O', status: 'present' },
        { letter: 'R', status: 'absent' },
        { letter: 'L', status: 'present' },
        { letter: 'D', status: 'absent' },
      ],
      attemptNumber: 1,
    },
    {
      word: 'HELLO',
      feedback: [
        { letter: 'H', status: 'correct' },
        { letter: 'E', status: 'correct' },
        { letter: 'L', status: 'correct' },
        { letter: 'L', status: 'correct' },
        { letter: 'O', status: 'correct' },
      ],
      attemptNumber: 2,
    },
  ],
}

export const mockAttemptResponseCorrect: AttemptResponse = {
  word: 'HELLO',
  feedback: [
    { letter: 'H', status: 'correct' },
    { letter: 'E', status: 'correct' },
    { letter: 'L', status: 'correct' },
    { letter: 'L', status: 'correct' },
    { letter: 'O', status: 'correct' },
  ],
  isCorrect: true,
  gameState: 'won',
  attemptsRemaining: 0,
}

export const mockAttemptResponseIncorrect: AttemptResponse = {
  word: 'WORLD',
  feedback: [
    { letter: 'W', status: 'absent' },
    { letter: 'O', status: 'present' },
    { letter: 'R', status: 'absent' },
    { letter: 'L', status: 'present' },
    { letter: 'D', status: 'absent' },
  ],
  isCorrect: false,
  gameState: 'in_progress',
  attemptsRemaining: 5,
}

export const mockLeaderboardResponse: LeaderboardResponse = {
  entries: [
    { rank: 1, username: 'player1', attempts: 3, solveTimeSeconds: 120 },
    { rank: 2, username: 'player2', attempts: 4, solveTimeSeconds: 180 },
    { rank: 3, username: 'player3', attempts: 5, solveTimeSeconds: 240 },
  ],
  currentUserRank: 2,
}
