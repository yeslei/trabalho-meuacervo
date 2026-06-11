import { useState, useEffect, useCallback } from 'react'
import { useParams } from 'react-router-dom'
import { discoService } from '../services/discoService.js'
import { avaliacaoService } from '../services/avaliacaoService.js'
import { colecaoService } from '../services/colecaoService.js'
import { wishlistService } from '../services/wishlistService.js'
import StarRating from '../components/StarRating.jsx'
import Button from '../components/Button.jsx'
import Input from '../components/Input.jsx'
import Loading from '../components/Loading.jsx'

export default function DetalhesDisco() {
  const { idDisco } = useParams()
  const [dados, setDados] = useState(null)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [aviso, setAviso] = useState('')
  const [nota, setNota] = useState(0)
  const [comentario, setComentario] = useState('')

  const carregar = useCallback(() => {
    setCarregando(true)
    discoService.detalhes(idDisco)
      .then((d) => {
        setDados(d)
        setNota(d.notaUsuario || 0)
      })
      .catch((e) => setErro(e.mensagem || 'Erro ao carregar o disco.'))
      .finally(() => setCarregando(false))
  }, [idDisco])

  useEffect(() => { carregar() }, [carregar])

  const enviarAvaliacao = async () => {
    if (!nota) { setErro('Escolha uma nota.'); return }
    try {
      setErro('')
      await avaliacaoService.atualizar(Number(idDisco), nota, comentario)
      setComentario('')
      setAviso('Avaliacao salva!')
      carregar()
    } catch (e) { setErro(e.mensagem || 'Erro ao avaliar.') }
  }

  const alternarColecao = async () => {
    try {
      if (dados.estaNaColecao) await colecaoService.remover(Number(idDisco))
      else await colecaoService.adicionar(Number(idDisco))
      carregar()
    } catch (e) { setErro(e.mensagem || 'Erro na colecao.') }
  }

  const alternarWishlist = async () => {
    try {
      if (dados.estaNaWishlist) await wishlistService.remover(Number(idDisco))
      else await wishlistService.adicionar(Number(idDisco))
      carregar()
    } catch (e) { setErro(e.mensagem || 'Erro nos favoritos.') }
  }

  if (carregando) return <Loading texto="Carregando disco..." />
  if (!dados) return <div className="alert alert-error">{erro || 'Disco nao encontrado.'}</div>

  const { disco, estatistica, reviews = [], faixas = [] } = dados
  const media = Number(estatistica?.mediaNotas || estatistica?.media || 0)
  const totalAvaliacoes = estatistica?.totalAvaliacoes || estatistica?.total || 0

  return (
    <div>
      {erro && <div className="alert alert-error">{erro}</div>}
      {aviso && <div className="alert alert-success">{aviso}</div>}

      <section className="detalhes-container">
        <div>
          {disco.imagemCapa
            ? <img className="detalhes-cover" src={disco.imagemCapa} alt={disco.titulo} />
            : <div className="detalhes-cover cover-placeholder"><i className="fa-solid fa-compact-disc" /></div>}
        </div>

        <div className="detalhes-info">
          <h1>{disco.titulo}</h1>
          <div className="detalhes-artista">{disco.artista}</div>

          <div className="detalhes-meta">
            {disco.anoLancamento && <span><i className="fa-regular fa-calendar" /> {disco.anoLancamento}</span>}
            {disco.genero && <span><i className="fa-solid fa-music" /> {disco.genero}</span>}
            {disco.formato && <span><i className="fa-solid fa-record-vinyl" /> {disco.formato}</span>}
          </div>

          {totalAvaliacoes > 0 && (
            <div className="detalhes-rating">
              <span className="stars">
                {[1, 2, 3, 4, 5].map((n) => (
                  <span key={n} className={n <= Math.round(media) ? '' : 'star-empty'}>★</span>
                ))}
              </span>
              <span className="rating-numero">{media.toFixed(1)}</span>
              <span className="rating-total">({totalAvaliacoes} avaliacoes)</span>
            </div>
          )}

          <div className="detalhes-acoes">
            <Button variante={dados.estaNaColecao ? 'perigo' : 'primario'} onClick={alternarColecao}>
              <i className="fa-solid fa-compact-disc" /> {dados.estaNaColecao ? 'Remover da colecao' : 'Adicionar a colecao'}
            </Button>
            <Button variante={dados.estaNaWishlist ? 'perigo' : 'secundario'} onClick={alternarWishlist}>
              <i className={dados.estaNaWishlist ? 'fa-solid fa-heart' : 'fa-regular fa-heart'} /> {dados.estaNaWishlist ? 'Remover dos favoritos' : 'Favoritar'}
            </Button>
          </div>
        </div>
      </section>

      <section className="form-avaliar">
        <h3>Sua avaliacao</h3>
        <StarRating valor={nota} aoSelecionar={(valor) => { setNota(valor); setErro('') }} />
        <Input
          textarea
          id="comentario"
          placeholder="O que voce achou deste disco?"
          value={comentario}
          onChange={(e) => setComentario(e.target.value)}
        />
        <Button onClick={enviarAvaliacao}>{dados.notaUsuario ? 'Atualizar avaliacao' : 'Avaliar'}</Button>
      </section>

      {faixas.length > 0 && (
        <section className="tracklist">
          <h3>Faixas</h3>
          {faixas.map((f, i) => (
            <div className="track-row" key={i}>
              <span className="track-num">{f.posicao || i + 1}</span>
              <span className="track-titulo">{f.titulo}</span>
              <span className="track-duracao">{f.duracao}</span>
            </div>
          ))}
        </section>
      )}

      <section className="comments">
        <h3>Avaliacoes da comunidade</h3>
        {reviews.length === 0
          ? <p className="empty">Ainda nao ha avaliacoes. Seja o primeiro!</p>
          : reviews.map((r, i) => (
              <div className="review" key={i}>
                <div className="autor">{r.username || r.nomeUsuario || 'Usuario'} <StarRating valor={r.nota} /></div>
                {r.comentario && <div className="comentario">{r.comentario}</div>}
              </div>
            ))}
      </section>
    </div>
  )
}
