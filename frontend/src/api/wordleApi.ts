import { get, post, put, del } from './client'

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
  // TODO: Add your Wordle API endpoints here
  // Example:
  // getGame: (id: string) => get<Game>(`/games/${id}`),
  // createGame: (data: CreateGameRequest) => post<Game>('/games', data),
  // updateGame: (id: string, data: UpdateGameRequest) => put<Game>(`/games/${id}`, data),
  // deleteGame: (id: string) => del<void>(`/games/${id}`),
}

// Export client functions for direct use if needed
export { get, post, put, del }
