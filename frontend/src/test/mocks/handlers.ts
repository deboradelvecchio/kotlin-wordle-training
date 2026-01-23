import { http, HttpResponse } from 'msw'
import type { HealthResponse } from '@api/models/HealthResponse'
import type { AttemptResponse } from '@api/models/AttemptResponse'
import type { LeaderboardResponse } from '@api/models/LeaderboardResponse'
import type { GameStateResponse } from '@api/models/GameStateResponse'

const API_BASE_URL = '/kotlin-wordle-training/api'

export const handlers = [
  // Health check
  http.get(`${API_BASE_URL}/health`, () => {
    return HttpResponse.json<HealthResponse>({
      status: 'ok',
      service: 'kotlin-wordle-training',
    })
  }), 

  http.post(`${API_BASE_URL}/attempt`, async ({ request }) => {
    const body = (await request.json()) as { guess: string }
    const { guess } = body

    // Mock logic: check if word is correct
    const correctWord = 'HELLO'
    const isCorrect = guess.toUpperCase() === correctWord

    // Generate feedback in backend format (CCPAA)
    const feedback = Array.from(guess.toUpperCase())
      .map((letter, index) => {
        const correctLetter = correctWord[index]
        if (letter === correctLetter) {
          return 'C'
        }
        if (correctWord.includes(letter)) {
          return 'P'
        }
        return 'A'
      })
      .join('')

    const status = isCorrect ? 'WON' : 'IN_PROGRESS'

    return HttpResponse.json<AttemptResponse>({
      guess: guess.toUpperCase(),
      feedback,
      attemptNumber: 1,
      status,
    })
  }),

  // Game state - new backend format
  http.get(`${API_BASE_URL}/game-state`, () => {
    return HttpResponse.json<GameStateResponse>({
      status: 'NOT_STARTED',
      attemptsCount: 0,
      attempts: [],
      maxAttempts: 6,
    })
  }),

  // Leaderboard
  http.get(`${API_BASE_URL}/leaderboard`, () => {
    return HttpResponse.json<LeaderboardResponse>({
      entries: [
        { rank: 1, username: 'player1', attempts: 3, solveTimeSeconds: 120 },
        { rank: 2, username: 'player2', attempts: 4, solveTimeSeconds: 180 },
        { rank: 3, username: 'player3', attempts: 5, solveTimeSeconds: 240 },
      ],
      currentUserRank: 2,
    })
  }),

  // Save game state
  http.post(`${API_BASE_URL}/game-state`, () => {
    return HttpResponse.json(null, { status: 200 })
  }),
]
