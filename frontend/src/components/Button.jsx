// Botao reutilizavel. variante: primario | secundario | perigo.
export default function Button({ variante = 'primario', bloco = false, children, ...props }) {
  const classes = {
    primario: 'btn-primary',
    secundario: 'btn-secondary',
    perigo: 'btn-secondary btn-danger',
  }
  const classe = `${classes[variante] || classes.primario}${bloco ? ' btn-bloco' : ''}`
  return <button className={classe} {...props}>{children}</button>
}
