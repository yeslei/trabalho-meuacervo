import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.js'
import { discoService } from '../services/discoService.js'
import DiscoCard from '../components/DiscoCard.jsx'
import Loading from '../components/Loading.jsx'

export default function Home() {
  const navigate = useNavigate()
  const { usuario } = useAuth()
  const [discos, setDiscos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    discoService.destaques()
      .then((d) => setDiscos(d.discos || []))
      .catch((e) => setErro(e.mensagem || 'Erro ao carregar destaques.'))
      .finally(() => setCarregando(false))
  }, [])

  // Persiste o disco (gera id interno) e navega para os detalhes.
  const abrir = async (disco) => {
    try {
      const salvo = await discoService.abrir({
        discogsId: disco.discogsId,
        titulo: disco.titulo,
        artista: disco.artista,
        genero: disco.genero,
        formato: disco.formato,
        capa: disco.imagemCapa,
        ano: disco.anoLancamento,
      })
      navigate(`/disco/${salvo.idDisco ?? salvo.id_disco}`)
    } catch (e) {
      setErro(e.mensagem || 'Nao foi possivel abrir o disco.')
    }
  }

  if (carregando) return <Loading texto="Carregando destaques..." />

  const secoes = [
    ['Os discos e CDs mais vendidos desta semana', discos.slice(0, 6)],
    ['Os discos e CDs mais desejados desta semana', discos.slice(6, 12)],
    ['Os discos e CDs mais colecionados desta semana', discos.slice(12, 18)],
  ]

  return (
    <div>
      <h2 className="home-greeting">
        E ai, <span>{usuario?.nome || 'Visitante'}</span>! Confira as novidades que acabaram de sair
      </h2>

      {erro && <div className="alert alert-error">{erro}</div>}
      {discos.length === 0 && <p className="empty-state">Nenhum disco disponivel no momento. Tente buscar por artistas ou albuns.</p>}

      {secoes.map(([titulo, itens]) => (
        itens.length > 0 && (
          <section key={titulo}>
            <h3 className="section-title">{titulo}</h3>
            <div className="card-grid">
              {itens.map((d, i) => <DiscoCard key={d.discogsId || `${titulo}-${i}`} disco={d} aoClicar={abrir} />)}
            </div>
          </section>
        )
      ))}
    </div>
  )
}
