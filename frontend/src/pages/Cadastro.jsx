import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.js'
import Input from '../components/Input.jsx'
import Button from '../components/Button.jsx'

export default function Cadastro() {
  const { cadastrar } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ nome: '', username: '', email: '', senha: '' })
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  const mudar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

  const enviar = async (e) => {
    e.preventDefault()
    setErro('')
    if (form.senha.length < 8) { setErro('A senha deve ter ao menos 8 caracteres.'); return }
    setEnviando(true)
    try {
      await cadastrar(form)
      navigate('/')
    } catch (err) {
      setErro(err.mensagem || err.message || 'Falha no cadastro.')
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
          Comece a montar<br />
          <span>seu acervo</span>
        </h1>
        <p>Registre seus discos, avalie albuns e acompanhe sua evolucao musical.</p>
      </div>

      <div className="split-right">
      <div className="auth-form-container">
        <p className="auth-eyebrow">Bem-vindo</p>
        <h2>Crie sua conta</h2>
        <p className="auth-copy">E rapido. Em menos de um minuto voce estara pronto para montar seu acervo.</p>
        {erro && <div className="alert alert-error">{erro}</div>}
        <form onSubmit={enviar}>
          <Input label="Nome completo" id="nome" value={form.nome}
                 onChange={mudar('nome')} placeholder="Seu nome" required />
          <Input label="Nome de usuario" id="username" value={form.username}
                 onChange={mudar('username')} placeholder="@usuario" />
          <Input label="E-mail" id="email" type="email" value={form.email}
                 onChange={mudar('email')} placeholder="seu@email.com" required />
          <Input label="Senha" id="senha" type="password" value={form.senha}
                 onChange={mudar('senha')} placeholder="Minimo 8 caracteres" required />
          <Button type="submit" bloco disabled={enviando}>
            {enviando ? 'Criando...' : 'Cadastrar'}
          </Button>
        </form>
        <p className="auth-footer">Ja tem conta? <Link to="/login">Entrar</Link></p>
      </div>
      </div>
    </div>
  )
}
