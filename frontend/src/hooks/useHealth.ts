import { useQuery } from '@tanstack/react-query'
import { wordleApi } from '../api/wordleApi'

/**
 * React Query hook for health check endpoint
 */
export function useHealth() {
  return useQuery({
    queryKey: ['health'],
    queryFn: () => wordleApi.getHealth(),
  })
}
