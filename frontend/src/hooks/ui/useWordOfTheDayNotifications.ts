import { useQueryClient } from '@tanstack/react-query'
import { useServerSentEvents } from './useServerSentEvents'
import { useWordOfTheDay } from '@hooks/queries/useWordOfTheDay'
import toast from 'react-hot-toast'

const SSE_URL =
  import.meta.env.VITE_SSE_URL ||
  `http://localhost:8080/kotlin-wordle-training/api/events/word-of-the-day`

/**
 * Hook that listens for Server-Sent Events notifications about new words of the day
 * and automatically refetches the word of the day when a notification is received.
 */
export function useWordOfTheDayNotifications() {
  const queryClient = useQueryClient()
  const { data: currentWordOfTheDay } = useWordOfTheDay()

  const { isConnected } = useServerSentEvents({
    url: SSE_URL,
    enabled: true,
    onMessage: message => {
      if (message.type === 'NEW_WORD_OF_THE_DAY') {
        const newDate = message.date as string

        if (newDate && newDate !== currentWordOfTheDay?.date) {
          toast.success('A new word of the day is available!')
          queryClient.invalidateQueries({ queryKey: ['word-of-the-day'] })
        }
      }
    },
    onError: error => {
      console.error('SSE error:', error)
    },
    onOpen: () => {
      console.log('SSE connected')
    },
    onClose: () => {
      console.log('SSE disconnected')
    },
  })

  return {
    isConnected,
  }
}
