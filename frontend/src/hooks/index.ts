/**
 * React Query hooks for API calls
 * Export all hooks from this file for convenient imports
 */

// Query hooks (API calls)
export { useHealth } from './queries/useHealth'
export { useWordOfTheDay } from './queries/useWordOfTheDay'
export { useAttemptWord } from './queries/useAttemptWord'
export { useGameState } from './queries/useGameState'
export { useLeaderboard } from './queries/useLeaderboard'
export { useSaveGameState } from './queries/useSaveGameState'

// UI hooks (game logic)
export { useWordleGame } from './ui/useWordleGame'
export { useWordleGameAnonymous } from './ui/useWordleGameAnonymous'
export { useWordleGameAuthenticated } from './ui/useWordleGameAuthenticated'
