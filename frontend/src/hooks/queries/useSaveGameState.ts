import { useMutation } from '@tanstack/react-query'
import { wordleApi } from '@api/wordleApi'
import type { SaveGameStateRequest } from '@api/models/SaveGameStateRequest'

export function useSaveGameState() {
  return useMutation({
    mutationFn: (request: SaveGameStateRequest) =>
      wordleApi.saveGameState(request),
  })
}
