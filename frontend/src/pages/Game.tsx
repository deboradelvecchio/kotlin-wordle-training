import { Board } from '@components/Board'
import { Keyboard } from '@components/Keyboard'
import { GameHeader } from '@components/GameHeader'
import { GameResult } from '@components/GameResult'
import { useWordleGame } from '@hooks/ui/useWordleGame'
import { isGameFinished } from '@utils/gameState'

export function Game() {
  const {
    wordOfTheDay,
    attempts,
    currentWord,
    gameState,
    isLoading,
    handleLetterInput,
    handleBackspace,
    handleEnter,
    isAuthenticated,
  } = useWordleGame()

  const showKeyboard = !isGameFinished(gameState)
  const isLoadingGame = isLoading && !wordOfTheDay

  return (
    <div className="game-container">
      <GameHeader isAuthenticated={isAuthenticated} />

      {isLoadingGame ? (
        <div className="game-loading">Loading...</div>
      ) : (
        <>
          <Board
            attempts={attempts}
            currentWord={currentWord}
            gameState={gameState}
          />

          {showKeyboard && (
            <Keyboard
              onLetterClick={handleLetterInput}
              onBackspace={handleBackspace}
              onEnter={handleEnter}
              attempts={attempts}
            />
          )}

          <GameResult gameState={gameState} attemptsCount={attempts.length} />
        </>
      )}
    </div>
  )
}
