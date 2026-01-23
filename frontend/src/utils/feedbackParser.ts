/**
 * Converts backend feedback string format to frontend array format
 *
 * Backend format: "CCPAA" where C=correct, P=present, A=absent
 * Frontend format: Array of {letter, status} objects
 */

export type LetterStatus = 'correct' | 'present' | 'absent'

export type LetterFeedback = {
  letter: string
  status: LetterStatus
}

const statusMap: Record<string, LetterStatus> = {
  C: 'correct',
  P: 'present',
  A: 'absent',
}

/**
 * Parse backend feedback string into array of LetterFeedback
 * @param guess - The guessed word (e.g., "HELLO")
 * @param feedback - Backend feedback string (e.g., "CCPAA")
 * @returns Array of LetterFeedback objects
 */
export function parseFeedback(
  guess: string,
  feedback: string
): LetterFeedback[] {
  return Array.from(guess.toUpperCase()).map((letter, index) => ({
    letter,
    status: statusMap[feedback[index]] || 'absent',
  }))
}

/**
 * Check if feedback indicates a correct answer (all C's)
 */
export function isCorrectAnswer(feedback: string): boolean {
  return feedback === 'CCCCC'
}
