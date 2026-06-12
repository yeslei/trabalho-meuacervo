import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// base = nome do repositorio, necessario para o GitHub Pages (project site)
// servir os assets a partir de /trabalho-meuacervo/.
export default defineConfig({
  plugins: [react()],
  base: '/trabalho-meuacervo/',
})
