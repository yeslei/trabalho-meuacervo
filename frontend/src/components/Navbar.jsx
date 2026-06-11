import { useState } from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.js'

// Cabecalho reutilizavel da aplicacao React.
export default function Navbar() {
  const { usuario, logout } = useAuth()
  const navigate = useNavigate()
  const [termo, setTermo] = useState('')

  const buscar = (e) => {
    e.preventDefault()
    if (termo.trim()) navigate(`/busca?q=${encodeURIComponent(termo.trim())}`)
  }

  const sair = async () => {
    await logout()
    navigate('/login')
  }

  return (
    <header className="top-header">
      <NavLink to="/" className="logo-container">
        <i className="fa-solid fa-record-vinyl logo-icon" /> MeuAcervo
      </NavLink>

      <div className="search-bar">
        <form onSubmit={buscar}>
          <i className="fa-solid fa-magnifying-glass search-icon" />
          <input
            type="search"
            placeholder="Procure por artistas, albuns e mais..."
            value={termo}
            onChange={(e) => setTermo(e.target.value)}
          />
        </form>
      </div>

      <nav className="nav-icons">
        <NavLink to="/" title="Home"><i className="fa-solid fa-house" /></NavLink>
        <NavLink to="/perfil?aba=colecao" title="Minha Colecao"><i className="fa-solid fa-compact-disc" /></NavLink>
        <NavLink to="/perfil?aba=reviews" title="Meus Reviews"><i className="fa-solid fa-pen" /></NavLink>
        <NavLink to="/perfil?aba=favoritos" title="Favoritos"><i className="fa-regular fa-heart" /></NavLink>
        <NavLink to="/perfil" title={usuario?.nome || 'Perfil'}><i className="fa-regular fa-user" /></NavLink>
        <button type="button" className="nav-logout" title="Sair" onClick={sair}>
          <i className="fa-solid fa-right-from-bracket" />
        </button>
      </nav>
    </header>
  )
}
