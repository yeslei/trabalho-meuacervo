import { useState, useEffect } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.js'
import { colecaoService } from '../services/colecaoService.js'
import { wishlistService } from '../services/wishlistService.js'
import { avaliacaoService } from '../services/avaliacaoService.js'
import DiscoCard from '../components/DiscoCard.jsx'
import StarRating from '../components/StarRating.jsx'
import Loading from '../components/Loading.jsx'

export default function Perfil() {
  const { usuario } = useAuth()
  const navigate = useNavigate()
  const [searchParams, setSearchParams] = useSearchParams()
  const abaAtual = searchParams.get('aba')
  const [aba, setAba] = useState(['colecao', 'reviews', 'favoritos'].includes(abaAtual) ? abaAtual : 'colecao')
  const [colecao, setColecao] = useState([])
  const [favoritos, setFavoritos] = useState([])
  const [reviews, setReviews] = useState([])
  const [stats, setStats] = useState({ totalDiscos: 0, totalReviews: 0, totalFavoritos: 0 })
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    setCarregando(true)
    Promise.all([colecaoService.minha(), wishlistService.minha(), avaliacaoService.meusReviews()])
      .then(([col, wish, revs]) => {
        setColecao(col.colecao || [])
        setFavoritos(wish.favoritos || [])
        setReviews(revs.reviews || [])
        setStats({
          totalDiscos: col.totalDiscos ?? 0,
          totalFavoritos: wish.totalFavoritos ?? 0,
          totalReviews: revs.totalReviews ?? 0,
        })
      })
      .catch((e) => setErro(e.mensagem || 'Erro ao carregar o perfil.'))
      .finally(() => setCarregando(false))
  }, [])

  useEffect(() => {
    if (['colecao', 'reviews', 'favoritos'].includes(abaAtual)) {
      setAba(abaAtual)
    }
  }, [abaAtual])

  const irParaDisco = (d) => navigate(`/disco/${d.idDisco}`)
  const trocarAba = (proximaAba) => {
    setAba(proximaAba)
    setSearchParams({ aba: proximaAba })
  }
  const iniciais = (usuario?.nome || 'Usuario')
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0]?.toUpperCase())
    .join('')

  if (carregando) return <Loading texto="Carregando perfil..." />

  return (
    <div>
      {erro && <div className="alert alert-error">{erro}</div>}

      <section className="perfil-header">
        <div className="perfil-avatar">{iniciais}</div>
        <div className="perfil-info">
          <h1>{usuario?.nome}</h1>
          <div className="perfil-meta">
            <span>@{usuario?.username || usuario?.email}</span>
            <span>•</span>
            <span>Membro</span>
          </div>
          <p className="perfil-bio">Colecionador apaixonado por musica. Bem-vindo ao meu acervo!</p>
          {usuario?.username && (
            <Link className="btn-secondary perfil-edit-button" to={`/perfil/${usuario.username}`}>
              Ver perfil publico
            </Link>
          )}
        </div>
      </section>

      <section className="stats-grid">
        <button className="stat-card" type="button" onClick={() => trocarAba('colecao')}>
          <div className="stat-icon"><i className="fa-solid fa-compact-disc" /></div>
          <div className="stat-numero">{stats.totalDiscos}</div>
          <div className="stat-label">Discos na Colecao</div>
        </button>
        <button className="stat-card" type="button" onClick={() => trocarAba('reviews')}>
          <div className="stat-icon"><i className="fa-solid fa-pen" /></div>
          <div className="stat-numero">{stats.totalReviews}</div>
          <div className="stat-label">Reviews Escritos</div>
        </button>
        <button className="stat-card" type="button" onClick={() => trocarAba('favoritos')}>
          <div className="stat-icon"><i className="fa-solid fa-heart" /></div>
          <div className="stat-numero">{stats.totalFavoritos}</div>
          <div className="stat-label">Favoritos</div>
        </button>
      </section>

      <nav className="tabs">
        <button className={`tab-link ${aba === 'colecao' ? 'active' : ''}`} onClick={() => trocarAba('colecao')}>Colecao</button>
        <button className={`tab-link ${aba === 'reviews' ? 'active' : ''}`} onClick={() => trocarAba('reviews')}>Reviews</button>
        <button className={`tab-link ${aba === 'favoritos' ? 'active' : ''}`} onClick={() => trocarAba('favoritos')}>Favoritos</button>
      </nav>

      {aba === 'colecao' && (
        <>
          <h2 className="section-heading">Colecao de {usuario?.nome}<span className="count">{stats.totalDiscos} discos</span></h2>
          {colecao.length === 0
            ? <p className="empty-state">Voce ainda nao tem discos na colecao. Comece adicionando o primeiro!</p>
            : <div className="card-grid">{colecao.map((d) => <DiscoCard key={d.idDisco} disco={d} aoClicar={irParaDisco} />)}</div>}
        </>
      )}

      {aba === 'reviews' && (
        <>
          <h2 className="section-heading">Confira as opinioes de {usuario?.nome}<span className="count">{stats.totalReviews} reviews</span></h2>
          {reviews.length === 0
            ? <p className="empty-state">Voce ainda nao escreveu nenhum review. Que tal avaliar o primeiro disco?</p>
            : reviews.map((r, i) => (
              <article
                className="review-card"
                key={i}
                onClick={() => r.disco?.idDisco && navigate(`/disco/${r.disco.idDisco}`)}
              >
                {r.disco?.imagemCapa
                  ? <img className="review-cover" src={r.disco.imagemCapa} alt={r.disco.titulo} />
                  : <div className="review-cover cover-placeholder"><i className="fa-solid fa-compact-disc" /></div>}
                <div className="review-body">
                  <div className="review-header">
                    <div>
                      <span className="review-disco-titulo">{r.disco?.titulo || 'Disco'}</span>
                      {r.disco?.anoLancamento && <span className="review-ano">{r.disco.anoLancamento}</span>}
                      <div className="review-artista">{r.disco?.artista}</div>
                    </div>
                    <div className="review-stars"><StarRating valor={r.nota} /></div>
                  </div>
                  <div className="review-meta">
                    <i className="fa-regular fa-user" />
                    <span>@{r.username || usuario?.username || 'usuario'}</span>
                  </div>
                  {r.comentario && <p className="review-comentario">{r.comentario}</p>}
                </div>
              </article>
            ))}
        </>
      )}

      {aba === 'favoritos' && (
        <>
          <h2 className="section-heading">Os favoritos de {usuario?.nome}<span className="count">{stats.totalFavoritos} favoritos</span></h2>
          {favoritos.length === 0
            ? <p className="empty-state">Voce ainda nao favoritou nenhum disco.</p>
            : <div className="card-grid">{favoritos.map((d) => <DiscoCard key={d.idDisco} disco={d} aoClicar={irParaDisco} />)}</div>}
        </>
      )}
    </div>
  )
}
