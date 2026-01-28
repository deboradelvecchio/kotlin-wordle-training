import { useCallback } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { useServerSentEvents } from './useServerSentEvents'

const SSE_URL =
  'http://localhost:8080/kotlin-wordle-training/api/events/word-of-the-day'

/**
 * Hook to listen for new word notifications via Server-Sent Events.
 * Shows a toast notification when a new word is available.
 */
export function useWordNotifications() {
  const queryClient = useQueryClient()

  const handleMessage = useCallback(
    (message: { type: string }) => {
      if (message.type === 'NEW_WORD_OF_THE_DAY') {
        // Invalidate game state to trigger refetch
        queryClient.invalidateQueries({ queryKey: ['game-state'] })
        toast.success('🎉 A new word is available! Refresh to play.')
      }
    },
    [queryClient]
  )

  const handleError = useCallback(() => {
    console.debug('SSE connection error')
  }, [])

  const { isConnected } = useServerSentEvents({
    url: SSE_URL,
    eventName: 'NEW_WORD_OF_THE_DAY', // Listen for named event from backend
    onMessage: handleMessage,
    onError: handleError,
    enabled: true,
  })

  return { isConnected }
}
