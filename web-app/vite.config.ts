import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
// Use relative paths for Electron, absolute for web deployment
const isElectron = process.env.ELECTRON === 'true';

export default defineConfig({
  plugins: [react()],
  base: isElectron ? './' : '/fincalc/',
})
