import { useState, useMemo } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { Search, UserPlus, Loader2, Users, Zap, Check } from 'lucide-react'
import { useAuth } from '../contexts/AuthContext'
import AppLayout from '../components/layout/AppLayout'
import { userService } from '../services/user.service'
import { skillService } from '../services/skill.service'
import { qualificationService } from '../services/qualification.service'
import { friendshipService } from '../services/friendship.service'
import { AmizadeStatus } from '../types'
import type { Usuario, Skill } from '../types'

interface UsuarioComSkills extends Usuario {
  skills: Skill[]
}

export default function DiscoverPage() {
  const { user: me } = useAuth()
  const qc = useQueryClient()
  const [skillSelecionada, setSkillSelecionada] = useState<number | null>(null)
  const [busca, setBusca] = useState('')
  const [solicitados, setSolicitados] = useState<Set<number>>(new Set())

  const { data: usuariosData, isLoading: loadingUsuarios } = useQuery({
    queryKey: ['todos-usuarios'],
    queryFn: userService.listarTodos
  })

  const { data: skillsData, isLoading: loadingSkills } = useQuery({
    queryKey: ['skills'],
    queryFn: skillService.listarTodas
  })

  const { data: amizadesData } = useQuery({
    queryKey: ['amizades-detalhadas', me?.idUsuario],
    queryFn: () => friendshipService.listarAmizadesDetalhadas(me!.idUsuario),
    enabled: !!me
  })

  const usuarios = (usuariosData?.data ?? []).filter(u => u.idUsuario !== me?.idUsuario)
  const todasSkills = skillsData?.data ?? []

  const { data: skillsPorUsuario, isLoading: loadingSkillsUsuarios } = useQuery({
    queryKey: ['skills-todos-usuarios', usuarios.map(u => u.idUsuario).join(',')],
    queryFn: async () => {
      const results = await Promise.all(
        usuarios.map(u =>
          userService.buscarPerfil(u.idUsuario)
            .then(r => ({ id: u.idUsuario, skills: r.data?.skills ?? [] }))
        )
      )
      return Object.fromEntries(results.map(r => [r.id, r.skills])) as Record<number, Skill[]>
    },
    enabled: usuarios.length > 0
  })

  const mapaSkills = skillsPorUsuario ?? {}

  const jaConectados = useMemo(() => {
    const set = new Set<number>()
    ;(amizadesData?.data ?? []).forEach(a => {
      const outroId = a.outroUsuario.idUsuario
      if (a.status === AmizadeStatus.ACEITA || a.status === AmizadeStatus.PENDENTE) {
        set.add(outroId)
      }
    })
    return set
  }, [amizadesData])

  const usuariosComSkills: UsuarioComSkills[] = useMemo(() => {
    return usuarios.map(u => ({
      ...u,
      skills: mapaSkills[u.idUsuario] ?? []
    }))
  }, [usuarios, mapaSkills])

  const resultados = useMemo(() => {
    let lista = usuariosComSkills

    if (skillSelecionada !== null) {
      lista = lista.filter(u => u.skills.some(s => s.idSkill === skillSelecionada))
    }

    if (busca.trim()) {
      const termo = busca.toLowerCase()
      lista = lista.filter(u =>
        u.nome.toLowerCase().includes(termo) ||
        u.skills.some(s => s.name.toLowerCase().includes(termo))
      )
    }

    return lista
  }, [usuariosComSkills, skillSelecionada, busca])

  const solicitarMutation = useMutation({
    mutationFn: (idDestinatario: number) => friendshipService.enviarSolicitacao(idDestinatario),
    onSuccess: (_, idDestinatario) => {
      setSolicitados(prev => new Set(prev).add(idDestinatario))
      qc.invalidateQueries({ queryKey: ['amizades-detalhadas'] })
    }
  })

  const isLoading = loadingUsuarios || loadingSkills || (usuarios.length > 0 && loadingSkillsUsuarios)

  return (
    <AppLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Descobrir</h1>
          <p className="text-gray-500 text-sm mt-1">Encontre estudantes pela skill que você quer aprender.</p>
        </div>

        <div className="card p-5 space-y-4">
          <div className="relative">
            <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              value={busca}
              onChange={e => setBusca(e.target.value)}
              className="input-field pl-9 text-sm"
              placeholder="Buscar por nome ou skill..."
            />
          </div>

          <div>
            <p className="text-xs font-medium text-gray-500 mb-2">Filtrar por skill</p>
            <div className="flex flex-wrap gap-2">
              <button
                onClick={() => setSkillSelecionada(null)}
                className={`text-xs px-3 py-1.5 rounded-full font-medium transition-colors ${
                  skillSelecionada === null
                    ? 'bg-primary text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-primary/10 hover:text-primary'
                }`}
              >
                Todas
              </button>
              {todasSkills.map(s => (
                <button
                  key={s.idSkill}
                  onClick={() => setSkillSelecionada(skillSelecionada === s.idSkill ? null : s.idSkill)}
                  className={`text-xs px-3 py-1.5 rounded-full font-medium transition-colors ${
                    skillSelecionada === s.idSkill
                      ? 'bg-primary text-white'
                      : 'bg-gray-100 text-gray-600 hover:bg-primary/10 hover:text-primary'
                  }`}
                >
                  {s.name}
                </button>
              ))}
            </div>
          </div>
        </div>

        <div className="card overflow-hidden">
          {isLoading ? (
            <div className="flex flex-col items-center justify-center py-12 gap-3">
              <Loader2 size={20} className="animate-spin text-primary" />
              <p className="text-gray-400 text-sm">Carregando usuários e skills...</p>
            </div>
          ) : resultados.length === 0 ? (
            <div className="text-center py-12">
              <Users size={40} className="text-gray-200 mx-auto mb-3" />
              <p className="text-gray-500 font-medium">Nenhum usuário encontrado</p>
              <p className="text-gray-400 text-sm mt-1">Tente selecionar outra skill.</p>
              {(skillSelecionada !== null || busca) && (
                <button
                  onClick={() => { setSkillSelecionada(null); setBusca('') }}
                  className="text-primary text-sm mt-2 hover:underline"
                >
                  Limpar filtros
                </button>
              )}
            </div>
          ) : (
            <div>
              <div className="px-4 py-3 border-b border-gray-50">
                <p className="text-xs text-gray-400">
                  {resultados.length} usuário{resultados.length !== 1 ? 's' : ''} encontrado{resultados.length !== 1 ? 's' : ''}
                </p>
              </div>
              {resultados.map(u => {
                const jaConectado = jaConectados.has(u.idUsuario)
                const solicitado = solicitados.has(u.idUsuario)
                const initials = u.nome.split(' ').map((n: string) => n[0]).slice(0, 2).join('').toUpperCase()

                return (
                  <div key={u.idUsuario} className="flex items-start gap-3 px-4 py-3.5 border-b border-gray-50 last:border-0 hover:bg-gray-50">
                    <Link to={`/perfil/${u.idUsuario}`} className="w-10 h-10 rounded-full bg-primary flex items-center justify-center text-white text-sm font-bold flex-shrink-0 mt-0.5">
                      {initials}
                    </Link>
                    <div className="flex-1 min-w-0">
                      <Link to={`/perfil/${u.idUsuario}`} className="font-medium text-gray-900 text-sm hover:text-primary block">
                        {u.nome}
                      </Link>
                      {u.bio && <p className="text-gray-400 text-xs mt-0.5 truncate">{u.bio}</p>}
                      {u.skills.length > 0 ? (
                        <div className="flex flex-wrap gap-1 mt-1.5">
                          {u.skills.map(s => (
                            <span
                              key={s.idSkill}
                              onClick={() => setSkillSelecionada(skillSelecionada === s.idSkill ? null : s.idSkill)}
                              className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium cursor-pointer transition-colors ${
                                skillSelecionada === s.idSkill
                                  ? 'bg-primary text-white'
                                  : 'bg-primary/10 text-primary hover:bg-primary/20'
                              }`}
                            >
                              <Zap size={10} />
                              {s.name}
                            </span>
                          ))}
                        </div>
                      ) : (
                        <p className="text-gray-300 text-xs mt-1">Nenhuma skill cadastrada</p>
                      )}
                    </div>
                    <div className="flex-shrink-0 mt-0.5">
                      {jaConectado ? (
                        <span className="text-xs text-gray-400 flex items-center gap-1">
                          <Check size={12} />
                          Conectado
                        </span>
                      ) : solicitado ? (
                        <span className="text-xs text-warning">Enviado</span>
                      ) : (
                        <button
                          onClick={() => solicitarMutation.mutate(u.idUsuario)}
                          disabled={solicitarMutation.isPending}
                          className="flex items-center gap-1.5 text-xs font-medium text-primary border border-primary/30 hover:bg-primary hover:text-white px-3 py-1.5 rounded-lg transition-colors"
                        >
                          {solicitarMutation.isPending
                            ? <Loader2 size={12} className="animate-spin" />
                            : <UserPlus size={12} />}
                          Conectar
                        </button>
                      )}
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  )
}