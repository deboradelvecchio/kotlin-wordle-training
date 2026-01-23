import type { Attempt } from '@api/models/types'
import { calculateLetterStatuses } from '@utils/keyboard'
import type { LetterStatus } from '@utils/keyboard'

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
] as const

const LAST_ROW_INDEX = 2

function getKeyClassName(status: LetterStatus | undefined): string {
  const baseClass = 'keyboard-key'
  return status ? `${baseClass} key-${status}` : baseClass
}

type KeyboardKeyProps = {
  letter: string
  status: LetterStatus | undefined
  onClick: () => void
}

function KeyboardKey({ letter, status, onClick }: KeyboardKeyProps) {
  return (
    <button
      className={getKeyClassName(status)}
      onClick={onClick}
      aria-label={`Key ${letter}`}
    >
      {letter}
    </button>
  )
}

export function Keyboard({
  onLetterClick,
  onBackspace,
  onEnter,
  attempts,
}: KeyboardProps) {
  const letterStatuses = calculateLetterStatuses(attempts)

  return (
    <div className="keyboard">
      {KEYBOARD_ROWS.map((row, rowIndex) => (
        <div key={rowIndex} className="keyboard-row">
          {rowIndex === LAST_ROW_INDEX && (
            <button className="keyboard-key key-enter" onClick={onEnter}>
              Enter
            </button>
          )}
          {row.map(letter => (
            <KeyboardKey
              key={letter}
              letter={letter}
              status={letterStatuses.get(letter)}
              onClick={() => onLetterClick(letter)}
            />
          ))}
          {rowIndex === LAST_ROW_INDEX && (
            <button
              className="keyboard-key key-backspace"
              onClick={onBackspace}
              aria-label="Backspace"
            >
              ⌫
            </button>
          )}
        </div>
      ))}
    </div>
  )
}
