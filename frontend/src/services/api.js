import { API_URL } from '../config.js'

/**
 * Cliente HTTP central da aplicacao.
 *
 * TODA chamada a API passa por aqui. Nenhuma pagina faz fetch diretamente.
 * Responsabilidades centralizadas neste ponto unico:
 *  - prefixar a URL base da API (config.js / .env)
 *  - enviar 'credentials: include' -> manda o cookie de sessao (auth por sessao)
 *  - serializar o corpo como JSON e setar o Content-Type
 *  - desserializar a resposta e tratar erros de forma uniforme
 *
 * Em erro, lanca um Error com .codigo e .status para as paginas tratarem.
 */
async function request(caminho, { method = 'GET', body, ...resto } = {}) {
  const opcoes = {
    method,
    credentials: 'include', // ESSENCIAL: envia/recebe o cookie de sessao (JSESSIONID)
    headers: { Accept: 'application/json' },
    ...resto,
  }

  if (body !== undefined) {
    opcoes.headers['Content-Type'] = 'application/json'
    opcoes.body = JSON.stringify(body)
  }

  let resposta
  try {
    resposta = await fetch(`${API_URL}${caminho}`, opcoes)
  } catch (e) {
    const erro = new Error('Nao foi possivel conectar a API. O servidor esta no ar?')
    erro.codigo = 'sem-conexao'
    throw erro
  }

  const texto = await resposta.text()
  const dados = texto ? JSON.parse(texto) : null

  if (!resposta.ok) {
    const erro = new Error(dados?.mensagem || 'Erro na requisicao.')
    erro.codigo = dados?.erro || 'erro'
    erro.status = resposta.status
    throw erro
  }
  return dados
}

export const api = {
  get: (caminho) => request(caminho, { method: 'GET' }),
  post: (caminho, body) => request(caminho, { method: 'POST', body }),
  put: (caminho, body) => request(caminho, { method: 'PUT', body }),
  del: (caminho, body) => request(caminho, { method: 'DELETE', body }),
}
