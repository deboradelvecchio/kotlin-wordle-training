import { Board } from '@components/Board'
import { Keyboard } from '@components/Keyboard'
import { useWordleGame } from '@hooks/ui/useWordleGame'
import { wordleApi } from '@api/wordleApi'
import { useNavigate } from 'react-router-dom'

export function Game() {
  const navigate = useNavigate()
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

  return (
    <div className="game-container">
      <header className="game-header">
        <h1>Wordle</h1>
        <nav>
          {isAuthenticated && (
            <button onClick={() => navigate('/leaderboard')}>
              Leaderboard
            </button>
          )}
          {isAuthenticated ? (
            <button
              onClick={() =>
                (window.location.href =
                  '/kotlin-wordle-training/api/auth/logout')
              }
            >
              Logout
            </button>
          ) : (
            <button onClick={() => wordleApi.login()}>Login</button>
          )}
        </nav>
      </header>

      {isLoading && !wordOfTheDay ? (
        <div className="game-loading">Loading...</div>
      ) : (
        <>
          <Board
            attempts={attempts}
            currentWord={currentWord}
            gameState={gameState}
          />

          {gameState !== 'won' && gameState !== 'lost' && (
            <Keyboard
              onLetterClick={handleLetterInput}
              onBackspace={handleBackspace}
              onEnter={handleEnter}
              attempts={attempts}
            />
          )}

          {gameState === 'won' && (
            <div className="game-result">
              <h2>Congratulations! 🎉</h2>
              <p>You solved it in {attempts.length} attempts!</p>
            </div>
          )}

          {gameState === 'lost' && wordOfTheDay && (
            <div className="game-result">
              <h2>Game Over</h2>
              <p>The word was: {wordOfTheDay.word}</p>
            </div>
          )}
        </>
      )}
    </div>
  )
}
