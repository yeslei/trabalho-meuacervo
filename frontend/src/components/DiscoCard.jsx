// Card de disco reutilizavel (home, busca, colecao, favoritos).
export default function DiscoCard({ disco, aoClicar }) {
  const capa = disco.imagemCapa || disco.imagem_capa

  return (
    <article className="album-card" onClick={() => aoClicar?.(disco)}>
      {capa
        ? <img className="album-cover" src={capa} alt={disco.titulo} loading="lazy" />
        : <div className="album-cover cover-placeholder"><i className="fa-solid fa-compact-disc" /></div>}

      <div className="album-title">{disco.titulo}</div>
      <div className="album-artist">{disco.artista || 'Desconhecido'}</div>
      <div className="album-meta">
        <span>{[disco.anoLancamento, disco.formato].filter(Boolean).join(' · ')}</span>
      </div>
    </article>
  )
}
