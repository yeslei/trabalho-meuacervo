import { api } from './api.js'

export const authService = {
  login: (email, senha, lembrar) => api.post('/login', { email, senha, lembrar }),
  cadastrar: (dados) => api.post('/cadastro', dados),
  logout: () => api.post('/logout'),
  eu: () => api.get('/api/me'),
  atualizarPerfil: (dados) => api.put('/api/me', dados),
}
