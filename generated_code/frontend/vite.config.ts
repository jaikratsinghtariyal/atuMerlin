import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Dev server port is configurable via FRONTEND_PORT.
export default defineConfig(() => ({
  plugins: [react()],
  server: {
    port: Number(process.env.FRONTEND_PORT) || 5173,
    host: true,
  },
  preview: {
    port: Number(process.env.FRONTEND_PORT) || 4173,
    host: true,
  },
}));
