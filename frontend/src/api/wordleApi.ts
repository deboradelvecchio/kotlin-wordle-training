import { get, post } from './client'
import type { HealthResponse } from './models/HealthResponse'
import type { WordOfTheDayResponse } from './models/WordOfTheDayResponse'
import type { AttemptRequest } from './models/AttemptRequest'
import type { AttemptResponse } from './models/AttemptResponse'
import type { GameStateResponse } from './models/GameStateResponse'
import type { LeaderboardResponse } from './models/LeaderboardResponse'
import type { SaveGameStateRequest } from './models/SaveGameStateRequest'

export const wordleApi = {
  getHealth: () => get<HealthResponse>('/health'),
  getWordOfTheDay: () => get<WordOfTheDayResponse>('/word-of-the-day'),
  attemptWord: (request: AttemptRequest) =>
    post<AttemptResponse>('/attempt', request),
  getGameState: () => get<GameStateResponse>('/game-state'),
  getLeaderboard: () => get<LeaderboardResponse>('/leaderboard'),
  saveGameState: (request: SaveGameStateRequest) =>
    post<void>('/game-state', request),
  login: () => {
    window.location.href = '/kotlin-wordle-training/api/auth/login'
  },
}
