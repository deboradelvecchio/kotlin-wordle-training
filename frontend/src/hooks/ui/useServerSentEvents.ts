import { useEffect, useRef, useState, useCallback } from 'react'

type ServerSentEventMessage = {
  type: string
  date?: string
  timestamp?: number
  [key: string]: unknown
}

type UseServerSentEventsOptions = {
  url: string
  onMessage?: (message: ServerSentEventMessage) => void
  onError?: (error: Event) => void
  onOpen?: () => void
  onClose?: () => void
  enabled?: boolean
}

export function useServerSentEvents({
  url,
  onMessage,
  onError,
  onOpen,
  onClose,
  enabled = true,
}: UseServerSentEventsOptions) {
  const [isConnected, setIsConnected] = useState(false)
  const eventSourceRef = useRef<EventSource | null>(null)
  const optionsRef = useRef({ onMessage, onError, onOpen, onClose, enabled })

  useEffect(() => {
    optionsRef.current = { onMessage, onError, onOpen, onClose, enabled }
  }, [onMessage, onError, onOpen, onClose, enabled])

  const connect = useCallback(() => {
    const options = optionsRef.current

    if (
      !options.enabled ||
      eventSourceRef.current?.readyState === EventSource.OPEN
    ) {
      return
    }

    try {
      const eventSource = new EventSource(url)
      eventSourceRef.current = eventSource

      eventSource.onopen = () => {
        setIsConnected(true)
        options.onOpen?.()
      }

      eventSource.onmessage = event => {
        try {
          const message = JSON.parse(event.data) as ServerSentEventMessage
          options.onMessage?.(message)
        } catch (error) {
          console.error('Error parsing SSE message:', error)
        }
      }

      eventSource.onerror = error => {
        options.onError?.(error)

        // EventSource automatically reconnects, but we track the state
        if (eventSource.readyState === EventSource.CLOSED) {
          setIsConnected(false)
          options.onClose?.()
        }
      }
    } catch (error) {
      console.error('Error creating EventSource connection:', error)
    }
  }, [url])

  const closeConnection = useCallback(() => {
    if (eventSourceRef.current) {
      eventSourceRef.current.close()
      eventSourceRef.current = null
    }
  }, [])

  const disconnect = useCallback(() => {
    closeConnection()
    setIsConnected(false)
  }, [closeConnection])

  useEffect(() => {
    const options = optionsRef.current

    if (options.enabled) {
      connect()
    } else {
      closeConnection()
    }

    return () => {
      closeConnection()
    }
  }, [enabled, connect, closeConnection])

  return {
    isConnected,
    connect,
    disconnect,
  }
}
