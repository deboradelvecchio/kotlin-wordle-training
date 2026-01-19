import { useQuery } from '@tanstack/react-query'
import { wordleApi } from '@api/wordleApi'

/**
 * React Query hook for fetching leaderboard (requires authentication)
 */
export function useLeaderboard() {
  return useQuery({
    queryKey: ['leaderboard'],
    queryFn: () => wordleApi.getLeaderboard(),
    retry: false,
  })
}
