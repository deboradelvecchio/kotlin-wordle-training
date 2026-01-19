import type { Attempt } from '@api/models/WordOfTheDayResponse'

type KeyboardProps = {
  onLetterClick: (letter: string) => void
  onBackspace: () => void
  onEnter: () => void
  attempts: Attempt[]
}

const KEYBOARD_ROWS = [
  ['Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'],
  ['A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L'],
  ['Z', 'X', 'C', 'V', 'B', 'N', 'M'],
]

export function Keyboard({
  onLetterClick,
  onBackspace,
  onEnter,
  attempts,
}: KeyboardProps) {
  const letterStatuses = new Map<string, 'correct' | 'present' | 'absent'>()

  attempts.forEach(attempt => {
    attempt.feedback.forEach(feedback => {
      const letter = feedback.letter.toUpperCase()
      const currentStatus = letterStatuses.get(letter)

      if (!currentStatus || currentStatus === 'absent') {
        letterStatuses.set(letter, feedback.status)
      } else if (currentStatus === 'present' && feedback.status === 'correct') {
        letterStatuses.set(letter, 'correct')
      }
    })
  })

  return (
    <div className="keyboard">
      {KEYBOARD_ROWS.map((row, rowIndex) => (
        <div key={rowIndex} className="keyboard-row">
          {rowIndex === 2 && (
            <button className="keyboard-key key-enter" onClick={onEnter}>
              Enter
            </button>
          )}
          {row.map(letter => {
            const status = letterStatuses.get(letter)
            let className = 'keyboard-key'
            if (status) {
              className += ` key-${status}`
            }
            return (
              <button
                key={letter}
                className={className}
                onClick={() => onLetterClick(letter)}
              >
                {letter}
              </button>
            )
          })}
          {rowIndex === 2 && (
            <button
              className="keyboard-key key-backspace"
              onClick={onBackspace}
            >
              ⌫
            </button>
          )}
        </div>
      ))}
    </div>
  )
}
