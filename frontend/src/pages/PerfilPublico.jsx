import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { perfilPublicoService } from '../services/perfilPublicoService.js'
import DiscoCard from '../components/DiscoCard.jsx'
import StarRating from '../components/StarRating.jsx'
import Loading from '../components/Loading.jsx'
import { capaDoDisco } from '../utils/imagem.js'

export default function PerfilPublico() {
  const { username } = useParams()
  const navigate = useNavigate()
  const [perfil, setPerfil] = useState(null)
  const [aba, setAba] = useState('colecao')
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    setCarregando(true)
    setErro('')
    perfilPublicoService
      .buscarPorUsername(username)
      .then(setPerfil)
      .catch((e) => setErro(e.message || 'Nao foi possivel carregar o perfil.'))
      .finally(() => setCarregando(false))
  }, [username])

  if (carregando) return <Loading texto="Carregando perfil publico..." />

  if (erro) {
    return (
      <div>
        <div className="alert alert-error">{erro}</div>
        <Link className="btn-secondary" to="/">Voltar para a home</Link>
      </div>
    )
  }

  const usuario = perfil.usuario
  const stats = perfil.stats
  const iniciais = (usuario?.nome || username || 'Usuario')
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0]?.toUpperCase())
    .join('')

  const irParaDisco = (d) => navigate(`/disco/${d.idDisco}`)

  return (
    <div>
      <section className="perfil-header">
        <div className="perfil-avatar">{iniciais}</div>
        <div className="perfil-info">
          <div className="perfil-publico-label">Perfil publico</div>
          <h1>{usuario.nome}</h1>
          <div className="perfil-meta">
            <span>@{usuario.username}</span>
            <span>•</span>
            <span>Membro do MeuAcervo</span>
          </div>
          <p className="perfil-bio">Colecao, reviews e favoritos compartilhados por {usuario.nome}.</p>
        </div>
      </section>

      <section className="stats-grid">
        <button className="stat-card" type="button" onClick={() => setAba('colecao')}>
          <div className="stat-icon"><i className="fa-solid fa-compact-disc" /></div>
          <div className="stat-numero">{stats.totalDiscos}</div>
          <div className="stat-label">Discos na Colecao</div>
        </button>
        <button className="stat-card" type="button" onClick={() => setAba('reviews')}>
          <div className="stat-icon"><i className="fa-solid fa-pen" /></div>
          <div className="stat-numero">{stats.totalReviews}</div>
          <div className="stat-label">Reviews Escritos</div>
        </button>
        <button className="stat-card" type="button" onClick={() => setAba('favoritos')}>
          <div className="stat-icon"><i className="fa-solid fa-heart" /></div>
          <div className="stat-numero">{stats.totalFavoritos}</div>
          <div className="stat-label">Favoritos</div>
        </button>
      </section>

      <nav className="tabs">
        <button className={`tab-link ${aba === 'colecao' ? 'active' : ''}`} onClick={() => setAba('colecao')}>Colecao</button>
        <button className={`tab-link ${aba === 'reviews' ? 'active' : ''}`} onClick={() => setAba('reviews')}>Reviews</button>
        <button className={`tab-link ${aba === 'favoritos' ? 'active' : ''}`} onClick={() => setAba('favoritos')}>Favoritos</button>
      </nav>

      {aba === 'colecao' && (
        <>
          <h2 className="section-heading">Colecao de {usuario.nome}<span className="count">{stats.totalDiscos} discos</span></h2>
          {perfil.colecao.length === 0
            ? <p className="empty-state">Este usuario ainda nao adicionou discos a colecao.</p>
            : <div className="card-grid">{perfil.colecao.map((d) => <DiscoCard key={d.idDisco} disco={{ ...d, imagemCapa: capaDoDisco(d) }} aoClicar={irParaDisco} />)}</div>}
        </>
      )}

      {aba === 'reviews' && (
        <>
          <h2 className="section-heading">Reviews de {usuario.nome}<span className="count">{stats.totalReviews} reviews</span></h2>
          {perfil.reviews.length === 0
            ? <p className="empty-state">Este usuario ainda nao escreveu nenhum review.</p>
            : perfil.reviews.map((r, i) => (
              <article
                className="review-card"
                key={i}
                onClick={() => r.disco?.idDisco && navigate(`/disco/${r.disco.idDisco}`)}
              >
                {capaDoDisco(r.disco || {})
                  ? <img className="review-cover" src={capaDoDisco(r.disco || {})} alt={r.disco.titulo} />
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
                    <span>@{r.username || usuario.username}</span>
                  </div>
                  {r.comentario && <p className="review-comentario">{r.comentario}</p>}
                </div>
              </article>
            ))}
        </>
      )}

      {aba === 'favoritos' && (
        <>
          <h2 className="section-heading">Favoritos de {usuario.nome}<span className="count">{stats.totalFavoritos} favoritos</span></h2>
          {perfil.favoritos.length === 0
            ? <p className="empty-state">Este usuario ainda nao favoritou nenhum disco.</p>
            : <div className="card-grid">{perfil.favoritos.map((d) => <DiscoCard key={d.idDisco} disco={{ ...d, imagemCapa: capaDoDisco(d) }} aoClicar={irParaDisco} />)}</div>}
        </>
      )}
    </div>
  )
}
