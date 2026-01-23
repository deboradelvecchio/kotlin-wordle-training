import { useAuth } from '@contexts/AuthContext'
import { useWordleGameAnonymous } from './useWordleGameAnonymous'
import { useWordleGameAuthenticated } from './useWordleGameAuthenticated'

export function useWordleGame() {
  const { isAuthenticated } = useAuth()
  const anonymousGame = useWordleGameAnonymous()
  const authenticatedGame = useWordleGameAuthenticated()

  return isAuthenticated ? authenticatedGame : anonymousGame
}
