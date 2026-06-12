import { api } from './api.js'

export const avaliacaoService = {
  criar: (idDisco, nota, comentario) =>
    api.post('/avaliar-disco', { id_disco: idDisco, nota, comentario }),
  atualizar: (idDisco, nota, comentario) =>
    api.put('/avaliar-disco', { id_disco: idDisco, nota, comentario }),
  meusReviews: () => api.get('/perfil/reviews'),
}
