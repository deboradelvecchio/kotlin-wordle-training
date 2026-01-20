/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useMemo } from 'react'
import type { ReactNode } from 'react'
import { useWordOfTheDay } from '@hooks/queries/useWordOfTheDay'

type AuthContextType = {
  isAuthenticated: boolean
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const { data: wordOfTheDayData, isLoading } = useWordOfTheDay()

  const isAuthenticated = useMemo(
    () =>
      wordOfTheDayData?.attempts !== undefined ||
      wordOfTheDayData?.gameState !== undefined,
    [wordOfTheDayData]
  )

  const value = useMemo(
    () => ({ isAuthenticated, isLoading }),
    [isAuthenticated, isLoading]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
