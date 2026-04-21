/// <reference types="vitest" />

import legacy from '@vitejs/plugin-legacy'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import { defineConfig } from 'vite'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    legacy()
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      // Spring Boot 백엔드로 프록시 (nginx 프록시 경로와 동일하게 유지)
      '/user':     { target: 'http://localhost:8080', changeOrigin: true },
      '/oauth2':   { target: 'http://localhost:8080', changeOrigin: true },
      '/login':    { target: 'http://localhost:8080', changeOrigin: true },
      '/post':     { target: 'http://localhost:8080', changeOrigin: true },
      '/place':    { target: 'http://localhost:8080', changeOrigin: true },
      '/bookmark': { target: 'http://localhost:8080', changeOrigin: true },
      '/content':  { target: 'http://localhost:8080', changeOrigin: true },
      '/trend':    { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
  test: {
    globals: true,
    environment: 'jsdom'
  }
})
