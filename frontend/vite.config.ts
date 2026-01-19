import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0', // Allow access from outside container
    proxy: {
      '/kotlin-wordle-training': {
        // Use BACKEND_URL if set (for Docker), otherwise localhost (for local dev)
        target: process.env.BACKEND_URL || 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
