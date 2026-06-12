import { api } from './api.js'

export const perfilPublicoService = {
  buscarPorUsername: (username) => api.get(`/perfil/publico?username=${encodeURIComponent(username)}`),
}
