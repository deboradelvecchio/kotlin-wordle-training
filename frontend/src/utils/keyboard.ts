import type { Attempt } from '@api/models/types'

export type LetterStatus = 'correct' | 'present' | 'absent'

export function calculateLetterStatuses(
  attempts: Attempt[]
): Map<string, LetterStatus> {
  const letterStatuses = new Map<string, LetterStatus>()

  for (const attempt of attempts) {
    for (const feedback of attempt.feedback) {
      const letter = feedback.letter.toUpperCase()
      const currentStatus = letterStatuses.get(letter)

      if (!currentStatus || currentStatus === 'absent') {
        letterStatuses.set(letter, feedback.status)
      } else if (currentStatus === 'present' && feedback.status === 'correct') {
        letterStatuses.set(letter, 'correct')
      }
    }
  }

  return letterStatuses
}
