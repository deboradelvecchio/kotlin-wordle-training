import { describe, it, expect } from 'vitest'
import { render, screen } from '@test/utils/testUtils'
import { GameResult } from '../GameResult'

describe('GameResult', () => {
  it('renders congratulations message when game is won', () => {
    render(<GameResult gameState="won" attemptsCount={3} />)

    expect(screen.getByText(/Congratulations/i)).toBeInTheDocument()
    expect(screen.getByText(/You solved it in 3 attempts/i)).toBeInTheDocument()
  })

  it('renders game over message when game is lost', () => {
    render(<GameResult gameState="lost" attemptsCount={6} />)

    expect(screen.getByText(/Game Over/i)).toBeInTheDocument()
    expect(screen.getByText(/Better luck next time/i)).toBeInTheDocument()
  })

  it('returns null when game is not finished', () => {
    const { container } = render(
      <GameResult gameState="in_progress" attemptsCount={2} />
    )

    expect(container.firstChild).toBeNull()
  })
})
