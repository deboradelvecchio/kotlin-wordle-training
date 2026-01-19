import { QueryClient, QueryCache, MutationCache } from '@tanstack/react-query'
import toast from 'react-hot-toast'

function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    if (
      error.message.includes('401') ||
      error.message.includes('Unauthorized')
    ) {
      return 'Authentication required. Please login.'
    }
    if (error.message.includes('403') || error.message.includes('Forbidden')) {
      return 'You do not have permission to perform this action.'
    }
    if (error.message.includes('404') || error.message.includes('Not Found')) {
      return 'Resource not found.'
    }
    if (
      error.message.includes('500') ||
      error.message.includes('Internal Server Error')
    ) {
      return 'Server error. Please try again later.'
    }
    if (error.message.includes('Network') || error.message.includes('fetch')) {
      return 'Network error. Please check your connection.'
    }
    return error.message
  }
  return 'An error occurred. Please try again.'
}

const queryCache = new QueryCache({
  onError: error => {
    toast.error(getErrorMessage(error))
  },
})

const mutationCache = new MutationCache({
  onError: error => {
    toast.error(getErrorMessage(error))
  },
})

/**
 * React Query client configuration
 */
export const queryClient = new QueryClient({
  queryCache,
  mutationCache,
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
})
