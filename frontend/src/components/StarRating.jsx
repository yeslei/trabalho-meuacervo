import { useState } from 'react'

// Estrelas reutilizaveis. Se 'aoSelecionar' for passado, vira interativo.
export default function StarRating({ valor = 0, aoSelecionar }) {
  const clicavel = typeof aoSelecionar === 'function'
  const [preview, setPreview] = useState(null)
  const valorAtual = preview ?? Number(valor || 0)

  return (
    <span
      className={`rating-stars${clicavel ? ' star-buttons' : ''}`}
      onMouseLeave={clicavel ? () => setPreview(null) : undefined}
    >
      {[1, 2, 3, 4, 5].map((n) => {
        const ativa = n <= valorAtual
        return (
          <button
            key={n}
            type="button"
            className={ativa ? 'star-filled' : 'star-empty'}
            onMouseEnter={clicavel ? () => setPreview(n) : undefined}
            onFocus={clicavel ? () => setPreview(n) : undefined}
            onBlur={clicavel ? () => setPreview(null) : undefined}
            onClick={clicavel ? () => aoSelecionar(n) : undefined}
            aria-label={`${n} estrela${n > 1 ? 's' : ''}`}
            aria-pressed={clicavel ? n === Number(valor || 0) : undefined}
            disabled={!clicavel}
          >
            &#9733;
          </button>
        )
      })}
    </span>
  )
}
