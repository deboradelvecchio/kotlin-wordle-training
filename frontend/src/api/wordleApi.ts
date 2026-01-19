import { get } from './client'
import type { HealthResponse } from './models/HealthResponse'

/**
 * Wordle API functions
 * Add your API endpoints here as you build the backend
 *
 * Example usage with React Query:
 *
 * const { data, isLoading } = useQuery({
 *   queryKey: ['game', id],
 *   queryFn: () => wordleApi.getGame(id),
 * })
 */

export const wordleApi = {
  getHealth: () => get<HealthResponse>('/health'),
}
