import { useState, useEffect } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { discoService } from '../services/discoService.js'
import DiscoCard from '../components/DiscoCard.jsx'
import Button from '../components/Button.jsx'
import Loading from '../components/Loading.jsx'

export default function Busca() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const termo = searchParams.get('q') || ''

  const [resultado, setResultado] = useState(null)
  const [pagina, setPagina] = useState(1)
  const [carregando, setCarregando] = useState(false)
  const [erro, setErro] = useState('')

  useEffect(() => { setPagina(1) }, [termo])

  useEffect(() => {
    if (!termo) return
    setCarregando(true)
    setErro('')
    discoService.buscar(termo, pagina)
      .then(setResultado)
      .catch((e) => setErro(e.mensagem || 'Erro na busca.'))
      .finally(() => setCarregando(false))
  }, [termo, pagina])

  const abrir = async (disco) => {
    try {
      const salvo = await discoService.abrir({
        discogsId: disco.discogsId, titulo: disco.titulo, artista: disco.artista,
        genero: disco.genero, formato: disco.formato, capa: disco.imagemCapa, ano: disco.anoLancamento,
      })
      navigate(`/disco/${salvo.idDisco ?? salvo.id_disco}`)
    } catch (e) { setErro(e.mensagem || 'Nao foi possivel abrir o disco.') }
  }

  return (
    <div>
      <h2 className="section-heading" style={{ textAlign: 'left' }}>
        {termo ? `Resultados para "${termo}"` : 'Resultados da busca'}
      </h2>
      {erro && <div className="alert alert-error">{erro}</div>}
      {carregando && <Loading texto="Buscando..." />}

      {!carregando && resultado && (
        <>
          {(resultado.discos || []).length === 0
            ? <p className="empty-state">Nenhum disco encontrado para "{termo}".</p>
            : <div className="card-grid">
                {resultado.discos.map((d, i) => <DiscoCard key={d.discogsId || i} disco={d} aoClicar={abrir} />)}
              </div>}

          {(resultado.totalPaginas > 1) && (
            <div className="pagination">
              <span className="pagination-info">Pagina {resultado.paginaAtual} de {resultado.totalPaginas}</span>
              <div className="pagination-links">
              <Button variante="secundario" disabled={!resultado.temAnterior}
                      onClick={() => setPagina((p) => p - 1)}>Anterior</Button>
              <Button variante="secundario" disabled={!resultado.temProxima}
                      onClick={() => setPagina((p) => p + 1)}>Proxima</Button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  )
}
