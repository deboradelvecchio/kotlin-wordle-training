import { http, HttpResponse } from 'msw'
import type { HealthResponse } from '@api/models/HealthResponse'
import type { WordOfTheDayResponse } from '@api/models/WordOfTheDayResponse'
import type { AttemptResponse } from '@api/models/AttemptResponse'
import type { LeaderboardResponse } from '@api/models/LeaderboardResponse'

const API_BASE_URL = '/kotlin-wordle-training/api'

export const handlers = [
  // Health check
  http.get(`${API_BASE_URL}/health`, () => {
    return HttpResponse.json<HealthResponse>({
      status: 'ok',
      service: 'kotlin-wordle-training',
    })
  }),

  // Word of the day
  http.get(`${API_BASE_URL}/word-of-the-day`, () => {
    return HttpResponse.json<WordOfTheDayResponse>({
      date: '2025-01-15',
      gameState: 'not_started',
      attempts: [],
    })
  }),

  // Attempt word
  http.post(`${API_BASE_URL}/attempt`, async ({ request }) => {
    const body = (await request.json()) as { word: string }
    const { word } = body

    // Mock logic: check if word is correct
    const correctWord = 'HELLO'
    const isCorrect = word.toUpperCase() === correctWord

    const feedback = Array.from(word.toUpperCase()).map((letter, index) => {
      const correctLetter = correctWord[index]
      if (letter === correctLetter) {
        return { letter, status: 'correct' as const }
      }
      if (correctWord.includes(letter)) {
        return { letter, status: 'present' as const }
      }
      return { letter, status: 'absent' as const }
    })

    const gameState = isCorrect ? 'won' : 'in_progress'
    const attemptsRemaining = isCorrect ? 0 : 5 // Mock: assume 5 attempts remaining

    return HttpResponse.json<AttemptResponse>({
      word: word.toUpperCase(),
      feedback,
      isCorrect,
      gameState,
      attemptsRemaining,
    })
  }),

  // Game state
  http.get(`${API_BASE_URL}/game-state`, () => {
    return HttpResponse.json({
      gameState: 'in_progress',
      attempts: [],
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
