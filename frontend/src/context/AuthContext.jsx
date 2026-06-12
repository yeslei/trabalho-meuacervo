import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { authService } from '../services/authService.js'

/**
 * AuthContext — UNICO estado global da aplicacao.
 *
 * Como a sessao vive no cookie HttpOnly (o JS nao le), no carregamento
 * chamamos /api/me para descobrir se ha sessao ativa e reidratar o usuario.
 * Isso mantem o login apos um F5 sem reexpor a senha.
 */
const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [carregando, setCarregando] = useState(true) // true ate sabermos quem esta logado

  // Ao montar o app: pergunta ao back "quem sou eu?"
  useEffect(() => {
    authService
      .eu()
      .then((u) => setUsuario(u))
      .catch(() => setUsuario(null)) // 401 => ninguem logado, normal
      .finally(() => setCarregando(false))
  }, [])

  const login = useCallback(async (email, senha, lembrar) => {
    const u = await authService.login(email, senha, lembrar)
    setUsuario(u)
    return u
  }, [])

  const cadastrar = useCallback(async (dados) => {
    const u = await authService.cadastrar(dados)
    setUsuario(u)
    return u
  }, [])

  const logout = useCallback(async () => {
    try { await authService.logout() } finally { setUsuario(null) }
  }, [])

  const atualizarPerfil = useCallback(async (dados) => {
    const u = await authService.atualizarPerfil(dados)
    setUsuario(u)
    return u
  }, [])

  const valor = { usuario, carregando, login, cadastrar, logout, atualizarPerfil, autenticado: !!usuario }
  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>
}

export { AuthContext }
