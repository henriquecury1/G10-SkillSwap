import api from './api'
import type { ApiResponse, Amizade, AmizadeDetalhada } from '../types'

export const friendshipService = {
  async listarAmizades(idUsuario: number): Promise<ApiResponse<Amizade[]>> {
    const res = await api.get<ApiResponse<Amizade[]>>(`/usuarios/${idUsuario}/amizades`)
    return res.data
  },

  async listarRecebidas(idUsuario: number): Promise<ApiResponse<Amizade[]>> {
    const res = await api.get<ApiResponse<Amizade[]>>(`/usuarios/${idUsuario}/amizades/recebidas`)
    return res.data
  },

  async listarEnviadas(idUsuario: number): Promise<ApiResponse<Amizade[]>> {
    const res = await api.get<ApiResponse<Amizade[]>>(`/usuarios/${idUsuario}/amizades/enviadas`)
    return res.data
  },

  async enviarSolicitacao(idDestinatario: number): Promise<ApiResponse<void>> {
    const res = await api.post<ApiResponse<void>>('/amizades/solicitacoes', { idDestinatario })
    return res.data
  },

  async aceitar(idAmizade: number): Promise<ApiResponse<void>> {
    const res = await api.put<ApiResponse<void>>(`/amizades/${idAmizade}/aceitar`)
    return res.data
  },

  async recusar(idAmizade: number): Promise<ApiResponse<void>> {
    const res = await api.put<ApiResponse<void>>(`/amizades/${idAmizade}/recusar`)
    return res.data
  },

  async remover(idAmizade: number): Promise<ApiResponse<void>> {
    const res = await api.delete<ApiResponse<void>>(`/amizades/${idAmizade}`)
    return res.data
  },

  async listarAmizadesDetalhadas(idUsuario: number): Promise<ApiResponse<AmizadeDetalhada[]>> {
    const res = await api.get<ApiResponse<AmizadeDetalhada[]>>(`/usuarios/${idUsuario}/amizades-detalhadas`)
    return res.data
  },

  async listarRecebidasDetalhadas(idUsuario: number): Promise<ApiResponse<AmizadeDetalhada[]>> {
    const res = await api.get<ApiResponse<AmizadeDetalhada[]>>(`/usuarios/${idUsuario}/amizades/recebidas-detalhadas`)
    return res.data
  },

  async listarEnviadasDetalhadas(idUsuario: number): Promise<ApiResponse<AmizadeDetalhada[]>> {
    const res = await api.get<ApiResponse<AmizadeDetalhada[]>>(`/usuarios/${idUsuario}/amizades/enviadas-detalhadas`)
    return res.data
  },

  async bloquear(idBloqueado: number): Promise<ApiResponse<void>> {
    const res = await api.post<ApiResponse<void>>('/amizades/bloqueios', { idBloqueado })
    return res.data
  },

  async desbloquear(idBloqueado: number): Promise<ApiResponse<void>> {
    const res = await api.delete<ApiResponse<void>>(`/amizades/bloqueios/${idBloqueado}`)
    return res.data
  }
}
