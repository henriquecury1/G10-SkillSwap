import api from './api'
import type { ApiResponse, Usuario, PerfilUsuario } from '../types'

export const userService = {
  async buscarPorId(id: number): Promise<ApiResponse<Usuario>> {
    const res = await api.get<ApiResponse<Usuario>>(`/usuarios/${id}`)
    return res.data
  },

  async buscarPerfil(id: number): Promise<ApiResponse<PerfilUsuario>> {
    const res = await api.get<ApiResponse<PerfilUsuario>>(`/usuarios/${id}/perfil`)
    return res.data
  },

  async listarTodos(): Promise<ApiResponse<Usuario[]>> {
    const res = await api.get<ApiResponse<Usuario[]>>('/usuarios')
    return res.data
  },

  async atualizarPerfil(id: number, dados: { nome?: string; email?: string; bio?: string }): Promise<ApiResponse<void>> {
    const res = await api.put<ApiResponse<void>>(`/usuarios/${id}`, dados)
    return res.data
  },

  async atualizarSenha(id: number, senhaAtual: string, novaSenha: string): Promise<ApiResponse<void>> {
    const res = await api.put<ApiResponse<void>>(`/usuarios/${id}/senha`, { senhaAtual, novaSenha })
    return res.data
  }
}