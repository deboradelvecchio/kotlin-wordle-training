import type { Attempt, GameState } from '@api/models/types'
import { GAME_CONSTANTS, isGameActive } from '@utils/gameState'

type BoardProps = {
  attempts: Attempt[]
  currentWord: string
  gameState: GameState
}

export function Board({ attempts, currentWord, gameState }: BoardProps) {
  const isActive = isGameActive(gameState)
  const rowsToShow =
    GAME_CONSTANTS.MAX_ATTEMPTS - attempts.length - (isActive ? 1 : 0)

  const attemptRows = attempts.map((attempt, index) => (
    <Row
      key={`attempt-${index}`}
      word={attempt.word}
      feedback={attempt.feedback}
    />
  ))

  const currentRow = isActive ? (
    <Row key="current" word={currentWord} feedback={null} isCurrent />
  ) : null

  const emptyRows = Array.from({ length: rowsToShow }, (_, index) => (
    <Row key={`empty-${index}`} word="" feedback={null} />
  ))

  return (
    <div className="board">
      {attemptRows}
      {currentRow}
      {emptyRows}
    </div>
  )
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
  const cells = Array.from({ length: GAME_CONSTANTS.WORD_LENGTH }, (_, i) => {
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
