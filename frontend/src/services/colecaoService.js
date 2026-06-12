import { api } from './api.js'

export const colecaoService = {
  minha: () => api.get('/colecao/ver'),
  adicionar: (idDisco, estado, observacao) =>
    api.post('/colecao/adicionar', { id_disco: idDisco, estado, observacao }),
  atualizarDetalhes: (idDisco, estado, observacao) =>
    api.put('/colecao/adicionar', { id_disco: idDisco, estado, observacao }),
  remover: (idDisco) => api.del('/colecao/adicionar', { id_disco: idDisco }),
}
