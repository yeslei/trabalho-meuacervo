import { api } from './api.js'

export const discoService = {
  destaques: () => api.get('/home'),
  buscar: (termo, page = 1) =>
    api.get(`/buscar-discos?q=${encodeURIComponent(termo)}&page=${page}`),
  abrir: (disco) => api.post('/disco/abrir', disco),
  detalhes: (idDisco) => api.get(`/avaliar-disco?id_disco=${idDisco}`),
  tracklist: (discogsId) => api.get(`/ver-tracklist?id=${discogsId}`),
  metricas: (idDisco) => api.get(`/disco/metricas?id_disco=${idDisco}`),
}
