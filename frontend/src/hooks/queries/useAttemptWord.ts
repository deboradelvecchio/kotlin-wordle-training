import { useMutation, useQueryClient } from '@tanstack/react-query'
import { wordleApi } from '@api/wordleApi'
import type { AttemptRequest } from '@api/models/AttemptRequest'
import type { WordOfTheDayResponse } from '@api/models/WordOfTheDayResponse'
import type { AttemptResponse } from '@api/models/AttemptResponse'

export function useAttemptWord() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: AttemptRequest) => wordleApi.attemptWord(request),
    onSuccess: (data: AttemptResponse) => {
      if (data.attempts !== undefined) {
        const currentData = queryClient.getQueryData<WordOfTheDayResponse>([
          'word-of-the-day',
        ])
        if (currentData) {
          queryClient.setQueryData<WordOfTheDayResponse>(['word-of-the-day'], {
            ...currentData,
            attempts: data.attempts,
            gameState: data.gameState,
          })
        }
      }
    },
  })
}
