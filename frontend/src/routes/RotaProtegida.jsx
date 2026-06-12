import { Navigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.js'
import Loading from '../components/Loading.jsx'

// Protege rotas que exigem login. Enquanto o /api/me nao respondeu, mostra Loading.
export default function RotaProtegida({ children }) {
  const { autenticado, carregando } = useAuth()
  if (carregando) return <Loading texto="Carregando..." />
  if (!autenticado) return <Navigate to="/login" replace />
  return children
}
