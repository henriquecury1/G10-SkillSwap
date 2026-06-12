import api from './api'
import type { ApiResponse, LoginResponseDTO, Usuario } from '../types'

export const authService = {
  async login(email: string, senha: string): Promise<ApiResponse<LoginResponseDTO>> {
    const res = await api.post<ApiResponse<LoginResponseDTO>>('/login', { email, senha })
    return res.data
  },

  async cadastrar(nome: string, email: string, senha: string, bio?: string): Promise<ApiResponse<void>> {
    const res = await api.post<ApiResponse<void>>('/usuarios', { nome, email, senha, bio })
    return res.data
  },

  saveSession(token: string, usuario: Usuario) {
    localStorage.setItem('skillswap_token', token)
    localStorage.setItem('skillswap_user', JSON.stringify(usuario))
  },

  getToken(): string | null {
    return localStorage.getItem('skillswap_token')
  },

  getUser(): Usuario | null {
    const raw = localStorage.getItem('skillswap_user')
    if (!raw) return null
    try {
      return JSON.parse(raw) as Usuario
    } catch {
      return null
    }
  },

  logout() {
    localStorage.removeItem('skillswap_token')
    localStorage.removeItem('skillswap_user')
  },

  isAuthenticated(): boolean {
    return !!this.getToken()
  }
}
