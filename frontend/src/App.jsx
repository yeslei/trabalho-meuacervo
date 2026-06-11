import { useLocation } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import AppRoutes from './routes/AppRoutes.jsx'

export default function App() {
  const location = useLocation()
  // Login e cadastro nao exibem a Navbar
  const rotasSemNavbar = ['/login', '/cadastro']
  const mostrarNavbar = !rotasSemNavbar.includes(location.pathname)

  return (
    <>
      {mostrarNavbar && <Navbar />}
      <main className={mostrarNavbar ? 'container' : ''}>
        <AppRoutes />
      </main>
    </>
  )
}
