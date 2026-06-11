import axios from 'axios'

// No Codespaces, defina a variável de ambiente VITE_API_URL com o endereço do backend.
// Localmente, o proxy do Vite encaminha /api automaticamente para localhost:4567.
const BASE_URL = import.meta.env.VITE_API_URL ?? '/api'

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Injeta o token JWT automaticamente
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('skillswap_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Trata erros globais de autenticação
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('skillswap_token')
      localStorage.removeItem('skillswap_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
