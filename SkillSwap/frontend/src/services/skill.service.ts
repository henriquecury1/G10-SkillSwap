import api from './api'
import type { ApiResponse, Skill } from '../types'

export const skillService = {
  async listarTodas(): Promise<ApiResponse<Skill[]>> {
    const res = await api.get<ApiResponse<Skill[]>>('/skills')
    return res.data
  },

  async buscarPorId(id: number): Promise<ApiResponse<Skill>> {
    const res = await api.get<ApiResponse<Skill>>(`/skills/${id}`)
    return res.data
  },

  async cadastrar(name: string): Promise<ApiResponse<void>> {
    const res = await api.post<ApiResponse<void>>('/skills', { name })
    return res.data
  },

  async atualizar(id: number, name: string): Promise<ApiResponse<void>> {
    const res = await api.put<ApiResponse<void>>(`/skills/${id}`, { name })
    return res.data
  },

  async deletar(id: number): Promise<ApiResponse<void>> {
    const res = await api.delete<ApiResponse<void>>(`/skills/${id}`)
    return res.data
  }
}
