import { useLeaderboard } from '@hooks/queries/useLeaderboard'
import { useNavigate } from 'react-router-dom'

export function Leaderboard() {
  const navigate = useNavigate()
  const { data, isLoading } = useLeaderboard()

  return (
    <div className="leaderboard-container">
      <header className="leaderboard-header">
        <h1>Leaderboard</h1>
        <button onClick={() => navigate('/')}>Back to Game</button>
      </header>

      {isLoading ? (
        <div className="leaderboard-loading">Loading...</div>
      ) : !data ? (
        <div className="leaderboard-empty">
          <p>No leaderboard data available. You may need to login.</p>
        </div>
      ) : (
        <>
          {data.currentUserRank && (
            <div className="user-rank">
              <p>Your rank: #{data.currentUserRank}</p>
            </div>
          )}

          {data.entries.length === 0 ? (
            <div className="leaderboard-empty">
              <p>No entries yet. Be the first to play!</p>
            </div>
          ) : (
            <table className="leaderboard-table">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Username</th>
                  <th>Attempts</th>
                  <th>Time</th>
                </tr>
              </thead>
              <tbody>
                {data.entries.map(entry => (
                  <tr key={entry.rank}>
                    <td>{entry.rank}</td>
                    <td>{entry.username}</td>
                    <td>{entry.attempts}</td>
                    <td>{entry.solveTimeSeconds}s</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </>
      )}
    </div>
  )
}
