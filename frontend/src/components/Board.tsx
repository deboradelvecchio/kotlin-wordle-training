import type { Attempt, GameState } from '@api/models/WordOfTheDayResponse'

type BoardProps = {
  attempts: Attempt[]
  currentWord: string
  gameState: GameState
}

const MAX_ATTEMPTS = 6
const WORD_LENGTH = 5

export function Board({ attempts, currentWord, gameState }: BoardProps) {
  const isActive = gameState === 'in_progress' || gameState === 'not_started'
  const rowsToShow = MAX_ATTEMPTS - attempts.length - (isActive ? 1 : 0)

  const rows = [
    ...attempts.map((attempt, i) => (
      <Row
        key={`attempt-${i}`}
        word={attempt.word}
        feedback={attempt.feedback}
      />
    )),
    ...(isActive
      ? [<Row key="current" word={currentWord} feedback={null} isCurrent />]
      : []),
    ...Array.from({ length: rowsToShow }, (_, i) => (
      <Row key={`empty-${i}`} word="" feedback={null} />
    )),
  ]

  return <div className="board">{rows}</div>
}

type RowProps = {
  word: string
  feedback: Array<{
    letter: string
    status: 'correct' | 'present' | 'absent'
  }> | null
  isCurrent?: boolean
}

function Row({ word, feedback, isCurrent = false }: RowProps) {
  const cells = Array.from({ length: WORD_LENGTH }, (_, i) => {
    const letter = word[i] || ''
    const status = feedback?.[i]?.status || null
    return (
      <Cell key={i} letter={letter} status={status} isCurrent={isCurrent} />
    )
  })

  return <div className="row">{cells}</div>
}

type CellProps = {
  letter: string
  status: 'correct' | 'present' | 'absent' | null
  isCurrent?: boolean
}

function Cell({ letter, status, isCurrent = false }: CellProps) {
  let className = 'cell'
  if (status) {
    className += ` cell-${status}`
  } else if (letter && isCurrent) {
    className += ' cell-typing'
  }

  return <div className={className}>{letter.toUpperCase()}</div>
}
