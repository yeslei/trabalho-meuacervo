// Campo de formulario reutilizavel (label + input/textarea).
export default function Input({ label, id, textarea = false, ...props }) {
  return (
    <div className="form-group">
      {label && <label htmlFor={id}>{label}</label>}
      {textarea
        ? <textarea id={id} {...props} />
        : <input id={id} {...props} />}
    </div>
  )
}
