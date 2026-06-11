import { Routes, Route, Navigate } from 'react-router-dom'
import RotaProtegida from './RotaProtegida.jsx'
import Login from '../pages/Login.jsx'
import Cadastro from '../pages/Cadastro.jsx'
import Home from '../pages/Home.jsx'
import Busca from '../pages/Busca.jsx'
import DetalhesDisco from '../pages/DetalhesDisco.jsx'
import Perfil from '../pages/Perfil.jsx'

// Arvore central de rotas (React Router).
export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<Cadastro />} />

      <Route path="/" element={<RotaProtegida><Home /></RotaProtegida>} />
      <Route path="/busca" element={<RotaProtegida><Busca /></RotaProtegida>} />
      <Route path="/disco/:idDisco" element={<RotaProtegida><DetalhesDisco /></RotaProtegida>} />
      <Route path="/perfil" element={<RotaProtegida><Perfil /></RotaProtegida>} />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
