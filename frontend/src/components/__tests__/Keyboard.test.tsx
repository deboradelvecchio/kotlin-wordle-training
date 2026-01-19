import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@test/utils/testUtils'
import userEvent from '@testing-library/user-event'
import { Keyboard } from '../Keyboard'
import type { Attempt } from '@api/models/WordOfTheDayResponse'

describe('Keyboard', () => {
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

  it('renders all keyboard keys', () => {
    const onLetterClick = vi.fn()
    const onBackspace = vi.fn()
    const onEnter = vi.fn()

    render(
      <Keyboard
        onLetterClick={onLetterClick}
        onBackspace={onBackspace}
        onEnter={onEnter}
        attempts={[]}
      />
    )

    // Check some letters are present
    expect(screen.getByText('Q')).toBeInTheDocument()
    expect(screen.getByText('A')).toBeInTheDocument()
    expect(screen.getByText('Z')).toBeInTheDocument()
    expect(screen.getByText('Enter')).toBeInTheDocument()
  })

  it('calls onLetterClick when a letter is clicked', async () => {
    const user = userEvent.setup()
    const onLetterClick = vi.fn()
    const onBackspace = vi.fn()
    const onEnter = vi.fn()

    render(
      <Keyboard
        onLetterClick={onLetterClick}
        onBackspace={onBackspace}
        onEnter={onEnter}
        attempts={[]}
      />
    )

    const qKey = screen.getByText('Q')
    await user.click(qKey)

    expect(onLetterClick).toHaveBeenCalledWith('Q')
    expect(onLetterClick).toHaveBeenCalledTimes(1)
  })

  it('calls onEnter when Enter button is clicked', async () => {
    const user = userEvent.setup()
    const onLetterClick = vi.fn()
    const onBackspace = vi.fn()
    const onEnter = vi.fn()

    render(
      <Keyboard
        onLetterClick={onLetterClick}
        onBackspace={onBackspace}
        onEnter={onEnter}
        attempts={[]}
      />
    )

    const enterKey = screen.getByText('Enter')
    await user.click(enterKey)

    expect(onEnter).toHaveBeenCalledTimes(1)
  })

  it('applies correct status classes to keys based on attempts', () => {
    const onLetterClick = vi.fn()
    const onBackspace = vi.fn()
    const onEnter = vi.fn()

    render(
      <Keyboard
        onLetterClick={onLetterClick}
        onBackspace={onBackspace}
        onEnter={onEnter}
        attempts={mockAttempts}
      />
    )

    // Keys with correct status should have the key-correct class
    const hKey = screen.getByText('H').closest('button')
    expect(hKey).toHaveClass('key-correct')
  })
})
