import { useEffect, useRef, useState, useCallback } from 'react'

type ServerSentEventMessage = {
  type: string
  date?: string
  timestamp?: number
  [key: string]: unknown
}

type UseServerSentEventsOptions = {
  url: string
  eventName?: string // If provided, listens for named events instead of default 'message'
  onMessage?: (message: ServerSentEventMessage) => void
  onError?: (error: Event) => void
  onOpen?: () => void
  onClose?: () => void
  enabled?: boolean
}

export function useServerSentEvents({
  url,
  eventName,
  onMessage,
  onError,
  onOpen,
  onClose,
  enabled = true,
}: UseServerSentEventsOptions) {
  const [isConnected, setIsConnected] = useState(false)
  const eventSourceRef = useRef<EventSource | null>(null)
  const optionsRef = useRef({ eventName, onMessage, onError, onOpen, onClose, enabled })

  useEffect(() => {
    optionsRef.current = { eventName, onMessage, onError, onOpen, onClose, enabled }
  }, [eventName, onMessage, onError, onOpen, onClose, enabled])

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

      const messageHandler = (event: MessageEvent) => {
        try {
          const message = JSON.parse(event.data) as ServerSentEventMessage
          options.onMessage?.(message)
        } catch (error) {
          console.error('Error parsing SSE message:', error)
        }
      }

      // Use addEventListener for named events, onmessage for default
      if (options.eventName) {
        eventSource.addEventListener(options.eventName, messageHandler)
      } else {
        eventSource.onmessage = messageHandler
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
