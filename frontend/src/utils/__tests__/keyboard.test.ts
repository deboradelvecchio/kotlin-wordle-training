import { describe, it, expect } from 'vitest'
import { calculateLetterStatuses } from '../keyboard'
import type { Attempt } from '@api/models/types'

describe('calculateLetterStatuses', () => {
  it('returns empty map for no attempts', () => {
    const statuses = calculateLetterStatuses([])
    expect(statuses.size).toBe(0)
  })

  it('calculates correct status for letters', () => {
    const attempts: Attempt[] = [
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

    const statuses = calculateLetterStatuses(attempts)
    expect(statuses.get('H')).toBe('correct')
    expect(statuses.get('E')).toBe('correct')
    expect(statuses.get('L')).toBe('correct')
    expect(statuses.get('O')).toBe('correct')
  })

  it('upgrades present to correct when letter becomes correct', () => {
    const attempts: Attempt[] = [
      {
        word: 'WORLD',
        feedback: [
          { letter: 'O', status: 'present' },
          { letter: 'R', status: 'absent' },
          { letter: 'L', status: 'present' },
          { letter: 'D', status: 'absent' },
          { letter: 'W', status: 'absent' },
        ],
        attemptNumber: 1,
      },
      {
        word: 'HELLO',
        feedback: [
          { letter: 'H', status: 'correct' },
          { letter: 'E', status: 'correct' },
          { letter: 'L', status: 'correct' },
          { letter: 'L', status: 'correct' },
          { letter: 'O', status: 'correct' },
        ],
        attemptNumber: 2,
      },
    ]

    const statuses = calculateLetterStatuses(attempts)
    // O and L should be upgraded from present to correct
    expect(statuses.get('O')).toBe('correct')
    expect(statuses.get('L')).toBe('correct')
  })

  it('keeps absent status when letter is only absent', () => {
    const attempts: Attempt[] = [
      {
        word: 'WORLD',
        feedback: [
          { letter: 'W', status: 'absent' },
          { letter: 'O', status: 'present' },
          { letter: 'R', status: 'absent' },
          { letter: 'L', status: 'present' },
          { letter: 'D', status: 'absent' },
        ],
        attemptNumber: 1,
      },
    ]

    const statuses = calculateLetterStatuses(attempts)
    expect(statuses.get('W')).toBe('absent')
    expect(statuses.get('R')).toBe('absent')
    expect(statuses.get('D')).toBe('absent')
  })
})
