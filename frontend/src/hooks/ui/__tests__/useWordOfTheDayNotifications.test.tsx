import React from 'react'
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { renderHook } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { useWordOfTheDayNotifications } from '../useWordOfTheDayNotifications'
import * as useWordOfTheDayHook from '@hooks/queries/useWordOfTheDay'
import * as useServerSentEventsHook from '../useServerSentEvents'

const mockToastSuccess = vi.fn()
const mockToastError = vi.fn()
const mockToastInfo = vi.fn()
const mockToastWarning = vi.fn()

vi.mock('react-hot-toast', () => ({
  default: {
    success: (...args: unknown[]) => mockToastSuccess(...args),
    error: (...args: unknown[]) => mockToastError(...args),
    info: (...args: unknown[]) => mockToastInfo(...args),
    warning: (...args: unknown[]) => mockToastWarning(...args),
  },
}))

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false, gcTime: 0 },
      mutations: { retry: false },
    },
  })

  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  )
}

describe('useWordOfTheDayNotifications', () => {
  let mockOnMessage:
    | ((message: { type: string; date?: string }) => void)
    | null = null

  beforeEach(() => {
    vi.clearAllMocks()
    mockToastSuccess.mockClear()
    mockToastError.mockClear()
    mockToastInfo.mockClear()
    mockToastWarning.mockClear()

    vi.spyOn(useWordOfTheDayHook, 'useWordOfTheDay').mockReturnValue({
      data: { date: '2024-01-01' },
      isLoading: false,
      isError: false,
      error: null,
      isPending: false,
      isSuccess: true,
      status: 'success',
      dataUpdatedAt: Date.now(),
      errorUpdatedAt: 0,
      failureCount: 0,
      failureReason: null,
      errorUpdateCount: 0,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isInitialLoading: false,
      isLoadingError: false,
      isPaused: false,
      isPlaceholderData: false,
      isRefetchError: false,
      isRefetching: false,
      isStale: false,
      refetch: vi.fn(),
      fetchStatus: 'idle',
      isEnabled: true,
      promise: Promise.resolve({ date: '2024-01-01' }),
    } as unknown as ReturnType<typeof useWordOfTheDayHook.useWordOfTheDay>)

    vi.spyOn(useServerSentEventsHook, 'useServerSentEvents').mockImplementation(
      options => {
        mockOnMessage = options.onMessage || null
        return {
          isConnected: true,
          connect: vi.fn(),
          disconnect: vi.fn(),
        }
      }
    )
  })

  afterEach(() => {
    vi.clearAllMocks()
    mockOnMessage = null
  })

  it('should connect to SSE', () => {
    const { result } = renderHook(() => useWordOfTheDayNotifications(), {
      wrapper: createWrapper(),
    })

    expect(result.current.isConnected).toBe(true)
    expect(useServerSentEventsHook.useServerSentEvents).toHaveBeenCalled()
  })

  it('should invalidate queries and show toast when new word notification arrives', () => {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false, gcTime: 0 },
        mutations: { retry: false },
      },
    })

    const invalidateQueriesSpy = vi.spyOn(queryClient, 'invalidateQueries')

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    )

    renderHook(() => useWordOfTheDayNotifications(), {
      wrapper,
    })

    if (mockOnMessage) {
      mockOnMessage({
        type: 'NEW_WORD_OF_THE_DAY',
        date: '2024-01-02',
      })
    }

    expect(invalidateQueriesSpy).toHaveBeenCalledWith({
      queryKey: ['word-of-the-day'],
    })

    expect(mockToastSuccess).toHaveBeenCalledWith(
      'A new word of the day is available!'
    )
  })

  it('should not invalidate queries if date is the same', () => {
    vi.spyOn(useWordOfTheDayHook, 'useWordOfTheDay').mockReturnValue({
      data: { date: '2024-01-01' },
      isLoading: false,
      isError: false,
      error: null,
      isPending: false,
      isSuccess: true,
      status: 'success',
      dataUpdatedAt: Date.now(),
      errorUpdatedAt: 0,
      failureCount: 0,
      failureReason: null,
      errorUpdateCount: 0,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isInitialLoading: false,
      isLoadingError: false,
      isPaused: false,
      isPlaceholderData: false,
      isRefetchError: false,
      isRefetching: false,
      isStale: false,
      refetch: vi.fn(),
      fetchStatus: 'idle',
      isEnabled: true,
      promise: Promise.resolve({ date: '2024-01-01' }),
    } as unknown as ReturnType<typeof useWordOfTheDayHook.useWordOfTheDay>)

    const queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false, gcTime: 0 },
        mutations: { retry: false },
      },
    })

    const invalidateQueriesSpy = vi.spyOn(queryClient, 'invalidateQueries')

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    )

    renderHook(() => useWordOfTheDayNotifications(), {
      wrapper,
    })

    if (mockOnMessage) {
      mockOnMessage({
        type: 'NEW_WORD_OF_THE_DAY',
        date: '2024-01-01', // Same date
      })
    }

    expect(invalidateQueriesSpy).not.toHaveBeenCalled()
    expect(mockToastSuccess).not.toHaveBeenCalled()
  })

  it('should ignore messages with different type', () => {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false, gcTime: 0 },
        mutations: { retry: false },
      },
    })

    const invalidateQueriesSpy = vi.spyOn(queryClient, 'invalidateQueries')

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    )

    renderHook(() => useWordOfTheDayNotifications(), {
      wrapper,
    })

    if (mockOnMessage) {
      mockOnMessage({
        type: 'OTHER_MESSAGE_TYPE',
        date: '2024-01-02',
      })
    }

    expect(invalidateQueriesSpy).not.toHaveBeenCalled()
    expect(mockToastSuccess).not.toHaveBeenCalled()
  })

  it('should handle missing date in notification', () => {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false, gcTime: 0 },
        mutations: { retry: false },
      },
    })

    const invalidateQueriesSpy = vi.spyOn(queryClient, 'invalidateQueries')

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    )

    renderHook(() => useWordOfTheDayNotifications(), {
      wrapper,
    })

    if (mockOnMessage) {
      mockOnMessage({
        type: 'NEW_WORD_OF_THE_DAY',
        // No date field
      })
    }

    expect(invalidateQueriesSpy).not.toHaveBeenCalled()
    expect(mockToastSuccess).not.toHaveBeenCalled()
  })
})
