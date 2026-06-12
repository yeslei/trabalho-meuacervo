import { useEffect } from 'react'

// Modal reutilizavel. Fecha no ESC e no clique fora.
export default function Modal({ titulo, aberto, aoFechar, children }) {
  useEffect(() => {
    if (!aberto) return
    const onKey = (e) => { if (e.key === 'Escape') aoFechar() }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [aberto, aoFechar])

  if (!aberto) return null
  return (
    <div className="modal-fundo" onClick={aoFechar}>
      <div className="modal-caixa" onClick={(e) => e.stopPropagation()}>
        {titulo && <h3>{titulo}</h3>}
        {children}
      </div>
    </div>
  )
}
