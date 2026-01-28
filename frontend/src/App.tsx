import { useEffect } from 'react'
import { Routes, Route, useSearchParams, useNavigate } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import toast from 'react-hot-toast'
import { Game } from '@pages/Game'
import { Leaderboard } from '@pages/Leaderboard'
import './App.css'

function App() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  useEffect(() => {
    const token = searchParams.get('token')
    const error = searchParams.get('error')

    if (token) {
      localStorage.setItem('auth_token', token)
      window.location.href = '/'
    } else if (error) {
      toast.error(`Login failed: ${error}`)
      navigate('/', { replace: true })
    }
  }, [searchParams, navigate])

  return (
    <div className="app">
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#363636',
            color: '#fff',
          },
          error: {
            duration: 5000,
            iconTheme: {
              primary: '#ff4444',
              secondary: '#fff',
            },
          },
        }}
      />
      <Routes>
        <Route path="/" element={<Game />} />
        <Route path="/leaderboard" element={<Leaderboard />} />
      </Routes>
    </div>
  )
}

export default App
