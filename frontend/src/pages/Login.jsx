import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.js'
import Input from '../components/Input.jsx'
import Button from '../components/Button.jsx'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [lembrar, setLembrar] = useState(false)
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  const enviar = async (e) => {
    e.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      await login(email, senha, lembrar)
      navigate('/')
    } catch (err) {
      setErro(err.mensagem || err.message || 'Falha no login.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="split-layout">
      <div className="split-left">
        <Link to="/" className="auth-brand">
          <i className="fa-solid fa-record-vinyl" /> MeuAcervo
        </Link>
        <h1>
          Seu acervo musical<br />
          <span>em um so lugar</span>
        </h1>
        <p>Entre para acompanhar sua colecao, favoritos e avaliacoes.</p>
      </div>

      <div className="split-right">
      <div className="auth-form-container">
        <p className="auth-eyebrow">Bem-vindo de volta</p>
        <h2>Entre na sua conta</h2>
        <p className="auth-copy">Acesse sua colecao e continue descobrindo discos.</p>
        {erro && <div className="alert alert-error">{erro}</div>}
        <form onSubmit={enviar}>
          <Input label="E-mail" id="email" type="email" value={email}
                 onChange={(e) => setEmail(e.target.value)} placeholder="seu@email.com" required />
          <Input label="Senha" id="senha" type="password" value={senha}
                 onChange={(e) => setSenha(e.target.value)} placeholder="••••••••" required />
          <label className="lembrar">
            <input type="checkbox" checked={lembrar} onChange={(e) => setLembrar(e.target.checked)} />
            Manter conectado
          </label>
          <Button type="submit" bloco disabled={enviando}>
            {enviando ? 'Entrando...' : 'Entrar'}
          </Button>
        </form>
        <p className="auth-footer">Nao tem conta? <Link to="/cadastro">Criar conta</Link></p>
      </div>
      </div>
    </div>
  )
}
