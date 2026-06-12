// Indicador de carregamento reutilizavel.
export default function Loading({ texto = 'Carregando...' }) {
  return (
    <div className="carregando">
      <div className="spinner" />
      <span>{texto}</span>
    </div>
  )
}
