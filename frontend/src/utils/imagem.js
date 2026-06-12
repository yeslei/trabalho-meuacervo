import { API_URL } from '../config.js'

const HOSTS_DISCOGS = new Set([
  'api-img.discogs.com',
  'i.discogs.com',
  'img.discogs.com',
  'st.discogs.com',
])

export function imagemDoDisco(disco) {
  return disco?.imagemCapa || disco?.imagem_capa || disco?.capa || ''
}

export function imagemProxy(url) {
  if (!url) return ''

  try {
    const parsed = new URL(url)
    if (parsed.protocol !== 'https:' || !HOSTS_DISCOGS.has(parsed.hostname.toLowerCase())) {
      return url
    }

    const baseUrl = API_URL.replace(/\/+$/, '')
    return `${baseUrl}/imagem?url=${encodeURIComponent(url)}`
  } catch (e) {
    return url
  }
}

export function capaDoDisco(disco) {
  return imagemProxy(imagemDoDisco(disco))
}
