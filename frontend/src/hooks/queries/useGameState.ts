import { useQuery } from '@tanstack/react-query'
import { wordleApi } from '@api/wordleApi'

/**
 * React Query hook for fetching current game state (authenticated users only)
 */
export function useGameState() {
  return useQuery({
    queryKey: ['game-state'],
    queryFn: () => wordleApi.getGameState(),
    retry: false,
  })
}
