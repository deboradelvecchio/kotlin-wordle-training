import { useEffect } from 'react'
import type { GameState } from '@api/models/WordOfTheDayResponse'

type KeyboardHandlers = {
  onLetterInput: (letter: string) => void
  onBackspace: () => void
  onEnter: () => void
}

const LETTER_REGEX = /^[A-Za-z]$/

export function useKeyboardEvents(
  gameState: GameState,
  handlers: KeyboardHandlers
) {
  const { onLetterInput, onBackspace, onEnter } = handlers

  useEffect(() => {
    if (gameState === 'won' || gameState === 'lost') {
      return
    }

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Enter') {
        event.preventDefault()
        onEnter()
      } else if (event.key === 'Backspace') {
        event.preventDefault()
        onBackspace()
      } else if (event.key.length === 1 && LETTER_REGEX.test(event.key)) {
        event.preventDefault()
        onLetterInput(event.key.toUpperCase())
      }
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [gameState, onLetterInput, onBackspace, onEnter])
}
