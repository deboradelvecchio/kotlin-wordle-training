import { useNavigate } from 'react-router-dom'
import { wordleApi } from '@api/wordleApi'

type GameHeaderProps = {
  isAuthenticated: boolean
}

export function GameHeader({ isAuthenticated }: GameHeaderProps) {
  const navigate = useNavigate()

  const handleLogout = () => {
    wordleApi.logout()
  }

  const handleLogin = () => {
    wordleApi.login()
  }

  return (
    <header className="game-header">
      <h1>Wordle</h1>
      <nav>
        {isAuthenticated && (
          <button onClick={() => navigate('/leaderboard')}>Leaderboard</button>
        )}
        {isAuthenticated ? (
          <button onClick={handleLogout}>Logout</button>
        ) : (
          <button onClick={handleLogin}>Login</button>
        )}
      </nav>
    </header>
  )
}
