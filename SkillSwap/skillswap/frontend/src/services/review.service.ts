import api from './api'
import type { ApiResponse, Avaliacao } from '../types'

export const reviewService = {
  async listarAvaliacoes(idUsuario: number): Promise<ApiResponse<Avaliacao[]>> {
    const res = await api.get<ApiResponse<Avaliacao[]>>(`/usuarios/${idUsuario}/avaliacoes`)
    return res.data
  },

  async avaliar(idAvaliado: number, nota: number): Promise<ApiResponse<void>> {
    const res = await api.post<ApiResponse<void>>('/avaliacoes', { idAvaliado, nota })
    return res.data
  },

  async editar(idAvaliado: number, nota: number): Promise<ApiResponse<void>> {
    const res = await api.put<ApiResponse<void>>('/avaliacoes', { idAvaliado, nota })
    return res.data
  }
}
