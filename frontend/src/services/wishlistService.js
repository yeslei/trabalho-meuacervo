import { api } from './api.js'

export const wishlistService = {
  minha: () => api.get('/wishlist/listar'),
  adicionar: (idDisco) => api.post('/wishlist/adicionar', { id_disco: idDisco }),
  remover: (idDisco) => api.del('/wishlist/adicionar', { id_disco: idDisco }),
}
