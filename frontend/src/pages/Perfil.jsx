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
  const { usuario, atualizarPerfil } = useAuth()
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
  const [sucesso, setSucesso] = useState('')
  const [itemEditando, setItemEditando] = useState(null)
  const [detalhesItem, setDetalhesItem] = useState({ estado: '', observacao: '' })
  const [salvandoDetalhes, setSalvandoDetalhes] = useState(false)
  const [editandoBio, setEditandoBio] = useState(false)
  const [bio, setBio] = useState(usuario?.bio || '')
  const [salvandoBio, setSalvandoBio] = useState(false)

  const capaDoDisco = (disco) => disco.imagemCapa || disco.imagem_capa

  const carregarPerfil = () => {
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
  }

  useEffect(() => {
    carregarPerfil()
  }, [])

  useEffect(() => {
    setBio(usuario?.bio || '')
  }, [usuario?.bio])

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
  const editarDetalhes = (disco) => {
    setItemEditando(disco.idDisco)
    setDetalhesItem({
      estado: disco.estadoConservacao || '',
      observacao: disco.observacao || '',
    })
  }
  const cancelarDetalhes = () => {
    setItemEditando(null)
    setDetalhesItem({ estado: '', observacao: '' })
  }
  const salvarDetalhes = async (idDisco) => {
    setErro('')
    setSucesso('')
    setSalvandoDetalhes(true)

    try {
      await colecaoService.atualizarDetalhes(idDisco, detalhesItem.estado, detalhesItem.observacao)
      setColecao((atual) => atual.map((d) => (
        d.idDisco === idDisco
          ? { ...d, estadoConservacao: detalhesItem.estado.trim(), observacao: detalhesItem.observacao.trim() }
          : d
      )))
      setItemEditando(null)
      setSucesso('Detalhes da colecao atualizados.')
    } catch (e) {
      setErro(e.message || e.mensagem || 'Nao foi possivel atualizar os detalhes.')
    } finally {
      setSalvandoDetalhes(false)
    }
  }
  const salvarBio = async (e) => {
    e.preventDefault()
    setErro('')
    setSucesso('')
    setSalvandoBio(true)

    try {
      await atualizarPerfil({ bio })
      setEditandoBio(false)
      setSucesso('Bio atualizada com sucesso.')
    } catch (e) {
      setErro(e.message || 'Nao foi possivel atualizar a bio.')
    } finally {
      setSalvandoBio(false)
    }
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
      {sucesso && <div className="alert alert-success">{sucesso}</div>}

      <section className="perfil-header">
        <div className="perfil-avatar">{iniciais}</div>
        <div className="perfil-info">
          <h1>{usuario?.nome}</h1>
          <div className="perfil-meta">
            <span>@{usuario?.username || usuario?.email}</span>
            <span>•</span>
            <span>Membro</span>
          </div>
          {editandoBio ? (
            <form className="perfil-bio-form" onSubmit={salvarBio}>
              <textarea
                value={bio}
                maxLength={280}
                onChange={(e) => setBio(e.target.value)}
                placeholder="Conte um pouco sobre voce e seu acervo..."
              />
              <div className="perfil-bio-footer">
                <span>{bio.length}/280</span>
                <div className="perfil-bio-actions">
                  <button
                    className="btn-secondary"
                    type="button"
                    onClick={() => {
                      setBio(usuario?.bio || '')
                      setEditandoBio(false)
                    }}
                  >
                    Cancelar
                  </button>
                  <button className="btn-primary" type="submit" disabled={salvandoBio}>
                    {salvandoBio ? 'Salvando...' : 'Salvar bio'}
                  </button>
                </div>
              </div>
            </form>
          ) : (
            <>
              <p className="perfil-bio">
                {usuario?.bio || 'Colecionador apaixonado por musica. Bem-vindo ao meu acervo!'}
              </p>
              <button className="btn-secondary perfil-edit-button" type="button" onClick={() => setEditandoBio(true)}>
                Editar bio
              </button>
            </>
          )}
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
            : (
              <div className="card-grid">
                {colecao.map((d) => (
                  <article className="album-card album-card-detalhado" key={d.idDisco} onClick={() => irParaDisco(d)}>
                    {capaDoDisco(d)
                      ? <img className="album-cover" src={capaDoDisco(d)} alt={d.titulo} loading="lazy" />
                      : <div className="album-cover cover-placeholder"><i className="fa-solid fa-compact-disc" /></div>}

                    <div className="album-title">{d.titulo}</div>
                    <div className="album-artist">{d.artista || 'Desconhecido'}</div>
                    <div className="album-meta">
                      <span>{[d.anoLancamento, d.formato].filter(Boolean).join(' - ')}</span>
                    </div>

                    {itemEditando === d.idDisco ? (
                      <div className="colecao-detalhes-form" onClick={(e) => e.stopPropagation()}>
                        <label>
                          Estado
                          <input
                            value={detalhesItem.estado}
                            maxLength={80}
                            onChange={(e) => setDetalhesItem((atual) => ({ ...atual, estado: e.target.value }))}
                            placeholder="Ex.: Novo, Bom, Com riscos"
                          />
                        </label>
                        <label>
                          Nota privada
                          <textarea
                            value={detalhesItem.observacao}
                            maxLength={500}
                            onChange={(e) => setDetalhesItem((atual) => ({ ...atual, observacao: e.target.value }))}
                            placeholder="Ex.: comprado na feira, edicao nacional..."
                          />
                        </label>
                        <div className="colecao-detalhes-acoes">
                          <button className="btn-secondary" type="button" onClick={cancelarDetalhes}>Cancelar</button>
                          <button
                            className="btn-primary"
                            type="button"
                            disabled={salvandoDetalhes}
                            onClick={() => salvarDetalhes(d.idDisco)}
                          >
                            {salvandoDetalhes ? 'Salvando...' : 'Salvar'}
                          </button>
                        </div>
                      </div>
                    ) : (
                      <div className="colecao-detalhes" onClick={(e) => e.stopPropagation()}>
                        {d.estadoConservacao && <span className="colecao-tag">{d.estadoConservacao}</span>}
                        {d.observacao && <p>{d.observacao}</p>}
                        <button className="colecao-editar" type="button" onClick={() => editarDetalhes(d)}>
                          <i className="fa-regular fa-pen-to-square" /> Editar detalhes
                        </button>
                      </div>
                    )}
                  </article>
                ))}
              </div>
            )}
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
