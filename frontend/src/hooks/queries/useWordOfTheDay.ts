import { useQuery } from '@tanstack/react-query'
import { wordleApi } from '@api/wordleApi'

export function useWordOfTheDay() {
  return useQuery({
    queryKey: ['word-of-the-day'],
    queryFn: () => wordleApi.getWordOfTheDay(),
  })
}
