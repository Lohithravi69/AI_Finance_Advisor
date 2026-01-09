import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    open: true,
    proxy: {
      '/api/finance': 'http://localhost:8080',
      '/api/ai': 'http://localhost:8080'
    }
  }
})
