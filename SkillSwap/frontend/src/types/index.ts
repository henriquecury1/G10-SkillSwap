export interface Usuario {
  idUsuario: number
  nome: string
  email: string
  bio?: string
  nota?: number
  numAvaliacoes?: number
}

export interface Skill {
  idSkill: number
  name: string
}

export interface Amizade {
  idAmizade: number
  status: number // 0=pendente, 1=aceita, 2=recusada, 3=bloqueada
  usuario1: number
  usuario2: number
}

export interface Avaliacao {
  idAvaliacao: string
  avaliado: number
  avaliador: number
  nota: number
  dataHora: string
}

export interface Mensagem {
  idMensagem: number
  tipo: number
  conteudo: string
  dataHora: string
  remetente: number
  amizade: number
}

export interface AmizadeDetalhada {
  idAmizade: number
  status: number
  usuario1: number
  usuario2: number
  outroUsuario: Usuario
}

export interface PerfilUsuario {
  usuario: Usuario
  skills: Skill[]
}

export interface LoginResponseDTO {
  token: string
  usuario: Usuario
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data?: T
}

// Status de amizade
export const AmizadeStatus = {
  PENDENTE: 0,
  ACEITA: 1,
  RECUSADA: 2,
  BLOQUEADA: 3
} as const
