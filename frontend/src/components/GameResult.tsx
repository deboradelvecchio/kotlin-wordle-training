import type { GameState } from '@api/models/WordOfTheDayResponse'

type GameResultProps = {
  gameState: GameState
  attemptsCount: number
}

export function GameResult({ gameState, attemptsCount }: GameResultProps) {
  if (gameState === 'won') {
    return (
      <div className="game-result">
        <h2>Congratulations! 🎉</h2>
        <p>You solved it in {attemptsCount} attempts!</p>
      </div>
    )
  }

  if (gameState === 'lost') {
    return (
      <div className="game-result">
        <h2>Game Over</h2>
        <p>Better luck next time!</p>
      </div>
    )
  }

  return null
}
