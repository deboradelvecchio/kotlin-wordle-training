import { useMutation } from '@tanstack/react-query'
import { wordleApi } from '@api/wordleApi'
import type { AttemptRequest } from '@api/models/AttemptRequest'

export function useAttemptWord() {
  return useMutation({
    mutationFn: (request: AttemptRequest) => wordleApi.attemptWord(request),
  })
}
