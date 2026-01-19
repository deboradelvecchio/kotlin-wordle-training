import { describe, it, expect } from 'vitest'
import { render, screen } from '@test/utils/testUtils'
import { Board } from '../Board'
import type { Attempt } from '@api/models/WordOfTheDayResponse'

describe('Board', () => {
  const mockAttempts: Attempt[] = [
    {
      word: 'HELLO',
      feedback: [
        { letter: 'H', status: 'correct' },
        { letter: 'E', status: 'correct' },
        { letter: 'L', status: 'correct' },
        { letter: 'L', status: 'correct' },
        { letter: 'O', status: 'correct' },
      ],
      attemptNumber: 1,
    },
  ]

  it('renders the board with attempts', () => {
    render(<Board attempts={mockAttempts} currentWord="" gameState="won" />)

    expect(screen.getByText('H')).toBeInTheDocument()
    expect(screen.getByText('E')).toBeInTheDocument()
    expect(screen.getAllByText('L').length).toBeGreaterThan(0)
    expect(screen.getByText('O')).toBeInTheDocument()
  })

  it('renders current word when game is active', () => {
    render(<Board attempts={[]} currentWord="WORLD" gameState="in_progress" />)

    expect(screen.getByText('W')).toBeInTheDocument()
    expect(screen.getByText('O')).toBeInTheDocument()
    expect(screen.getByText('R')).toBeInTheDocument()
    expect(screen.getByText('L')).toBeInTheDocument()
    expect(screen.getByText('D')).toBeInTheDocument()
  })

  it('renders empty cells for remaining attempts', () => {
    render(
      <Board attempts={mockAttempts} currentWord="" gameState="in_progress" />
    )

    const cells = screen
      .getAllByRole('generic')
      .filter(el => el.className.includes('cell'))
    expect(cells.length).toBeGreaterThan(5)
  })
})
