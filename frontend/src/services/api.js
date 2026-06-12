import { API_URL } from '../config.js'

const SESSION_STORAGE_KEY = 'meuacervo.sessionId'

function sessionIdAtual() {
  try {
    return window.localStorage.getItem(SESSION_STORAGE_KEY)
  } catch (e) {
    return null
  }
}

function salvarSessionId(sessionId) {
  try {
    if (sessionId) {
      window.localStorage.setItem(SESSION_STORAGE_KEY, sessionId)
    }
  } catch (e) {
    // localStorage pode estar indisponivel em navegadores mais restritos.
  }
}

function limparSessionId() {
  try {
    window.localStorage.removeItem(SESSION_STORAGE_KEY)
  } catch (e) {
    // Sem acao: o cookie de sessao ainda pode funcionar.
  }
}

function caminhoComSessao(caminho) {
  const sessionId = sessionIdAtual()
  if (!sessionId || caminho === '/login' || caminho === '/cadastro') {
    return caminho
  }

  const [pathname, query = ''] = caminho.split('?')
  const sufixo = query ? `?${query}` : ''
  return `;jsessionid=${encodeURIComponent(sessionId)}${pathname}${sufixo}`
}

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
  const baseUrl = API_URL.replace(/\/+$/, '')
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
    resposta = await fetch(`${baseUrl}${caminhoComSessao(caminho)}`, opcoes)
  } catch (e) {
    const erro = new Error('Nao foi possivel conectar a API. O servidor esta no ar?')
    erro.codigo = 'sem-conexao'
    throw erro
  }

  const texto = await resposta.text()
  const contentType = resposta.headers.get('content-type') || ''
  let dados = null

  if (texto && contentType.includes('application/json')) {
    try {
      dados = JSON.parse(texto)
    } catch (e) {
      const erro = new Error('A API retornou um JSON invalido.')
      erro.codigo = 'json-invalido'
      erro.status = resposta.status
      throw erro
    }
  } else if (texto) {
    const erro = new Error(
      resposta.ok
        ? 'A API retornou uma resposta inesperada.'
        : 'A API retornou HTML em vez de JSON. Confira se VITE_API_URL termina com /backend.'
    )
    erro.codigo = 'resposta-nao-json'
    erro.status = resposta.status
    throw erro
  }

  if (!resposta.ok) {
    if (resposta.status === 401) {
      limparSessionId()
    }
    const erro = new Error(dados?.mensagem || 'Erro na requisicao.')
    erro.codigo = dados?.erro || 'erro'
    erro.status = resposta.status
    throw erro
  }

  if (dados?.sessionId) {
    salvarSessionId(dados.sessionId)
  }
  return dados
}

export const api = {
  get: (caminho) => request(caminho, { method: 'GET' }),
  post: (caminho, body) => request(caminho, { method: 'POST', body }),
  put: (caminho, body) => request(caminho, { method: 'PUT', body }),
  del: (caminho, body) => request(caminho, { method: 'DELETE', body }),
  limparSessaoLocal: limparSessionId,
}
