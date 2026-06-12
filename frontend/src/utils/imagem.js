export function imagemDoDisco(disco) {
  return disco?.imagemCapa || disco?.imagem_capa || disco?.capa || ''
}

export function capaDoDisco(disco) {
  return imagemDoDisco(disco)
}
