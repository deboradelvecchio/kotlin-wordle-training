import './App.css'
import { useHealth } from './hooks/useHealth'

function App() {
  const { data, isLoading, error } = useHealth()

  if (isLoading) return <div>Loading...</div>
  if (error) return <div>Error: {error.message}</div>

  if (data) {
    return (
      <div className="app">
        <h1>Wordle Training</h1>
      </div>
    )
  }

  return (
    <div className="app">
      <h1>Wordle Training</h1>

      <div className="card">
        <p>Ready to build Wordle! 🎮</p>
        <p>React Query is configured and ready to use.</p>
      </div>
    </div>
  )
}

export default App
