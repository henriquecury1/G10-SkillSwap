import api from './api'
import type { ApiResponse, Mensagem } from '../types'

export const messageService = {
  async listarMensagens(idAmizade: number): Promise<ApiResponse<Mensagem[]>> {
    const res = await api.get<ApiResponse<Mensagem[]>>(`/amizades/${idAmizade}/mensagens`)
    return res.data
  },

  async enviar(idAmizade: number, conteudo: string): Promise<ApiResponse<void>> {
    const res = await api.post<ApiResponse<void>>(`/amizades/${idAmizade}/mensagens`, { conteudo })
    return res.data
  },

  async deletar(idMensagem: number): Promise<ApiResponse<void>> {
    const res = await api.delete<ApiResponse<void>>(`/mensagens/${idMensagem}`)
    return res.data
  }
}
