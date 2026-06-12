import api from './api'
import type { ApiResponse, Skill, Usuario } from '../types'

export const qualificationService = {
  async listarSkillsDoUsuario(idUsuario: number): Promise<ApiResponse<Skill[]>> {
    const res = await api.get<ApiResponse<Skill[]>>(`/usuarios/${idUsuario}/skills`)
    return res.data
  },

  async adicionarSkill(idUsuario: number, idSkill: number): Promise<ApiResponse<void>> {
    const res = await api.post<ApiResponse<void>>(`/usuarios/${idUsuario}/skills`, { idSkill })
    return res.data
  },

  async removerSkill(idUsuario: number, idSkill: number): Promise<ApiResponse<void>> {
    const res = await api.delete<ApiResponse<void>>(`/usuarios/${idUsuario}/skills/${idSkill}`)
    return res.data
  },

  async listarUsuariosPorSkill(idSkill: number): Promise<ApiResponse<Usuario[]>> {
    const res = await api.get<ApiResponse<Usuario[]>>(`/skills/${idSkill}/usuarios`)
    return res.data
  }
}
