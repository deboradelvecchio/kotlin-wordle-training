import { useQuery } from '@tanstack/react-query'
import { wordleApi } from '../api/wordleApi'

/**
 * React Query hook for fetching the word of the day
 */
export function useWordOfTheDay() {
  return useQuery({
    queryKey: ['word-of-the-day'],
    queryFn: () => wordleApi.getWordOfTheDay(),
  })
}
