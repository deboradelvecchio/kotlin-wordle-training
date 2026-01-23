import { useCallback } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { useServerSentEvents } from './useServerSentEvents'

const SSE_URL = 'http://localhost:8080/kotlin-wordle-training/api/events/new-word'

/**
 * Hook to listen for new word notifications via Server-Sent Events.
 * Shows a toast notification when a new word is available.
 * 
 * TODO: Implement backend SSE endpoint in Phase 3
 */
export function useWordNotifications() {
  const queryClient = useQueryClient()

  const handleMessage = useCallback(
    (message: { type: string }) => {
      if (message.type === 'NEW_WORD') {
        // Invalidate game state to trigger refetch
        queryClient.invalidateQueries({ queryKey: ['game-state'] })
        toast.success('🎉 A new word is available! Refresh to play.')
      }
    },
    [queryClient]
  )

  const handleError = useCallback(() => {
    console.debug('SSE connection not available (expected until Phase 3)')
  }, [])

  const { isConnected } = useServerSentEvents({
    url: SSE_URL,
    onMessage: handleMessage,
    onError: handleError,
    enabled: false, // Disabled until backend SSE is implemented
  })

  return { isConnected }
}
