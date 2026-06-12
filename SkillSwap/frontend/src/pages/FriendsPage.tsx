import { useState, useMemo } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { Users, Check, X, MessageSquare, Loader2, UserX, Search } from 'lucide-react'
import { useAuth } from '../contexts/AuthContext'
import AppLayout from '../components/layout/AppLayout'
import { friendshipService } from '../services/friendship.service'
import { qualificationService } from '../services/qualification.service'
import { AmizadeStatus, type AmizadeDetalhada, type Skill } from '../types'

function UserCard({ amizade, skills, onAccept, onReject, onRemove }: {
  amizade: AmizadeDetalhada
  skills?: Skill[]
  onAccept?: () => void
  onReject?: () => void
  onRemove?: () => void
}) {
  const user = amizade.outroUsuario
  const initials = user?.nome
    ? user.nome.split(' ').map((n: string) => n[0]).slice(0, 2).join('').toUpperCase()
    : '??'

  return (
    <div className="flex items-start gap-3 py-3 px-2 hover:bg-gray-50 rounded-lg">
      <Link to={`/perfil/${user.idUsuario}`} className="w-10 h-10 rounded-full bg-primary flex items-center justify-center text-white text-sm font-bold flex-shrink-0 mt-0.5">
        {initials}
      </Link>
      <div className="flex-1 min-w-0">
        <Link to={`/perfil/${user.idUsuario}`} className="font-medium text-gray-900 text-sm hover:text-primary truncate block">
          {user.nome}
        </Link>
        {user.bio && <p className="text-gray-400 text-xs truncate mt-0.5">{user.bio}</p>}
        {skills && skills.length > 0 && (
          <div className="flex flex-wrap gap-1 mt-1.5">
            {skills.slice(0, 4).map(s => (
              <span key={s.idSkill} className="inline-flex items-center px-2 py-0.5 rounded-full text-xs bg-primary/10 text-primary font-medium">
                {s.name}
              </span>
            ))}
            {skills.length > 4 && (
              <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs bg-gray-100 text-gray-500">
                +{skills.length - 4}
              </span>
            )}
          </div>
        )}
      </div>
      <div className="flex items-center gap-1.5 flex-shrink-0 mt-0.5">
        {onAccept && (
          <button onClick={onAccept} className="p-2 rounded-lg bg-green-50 text-success hover:bg-green-100" title="Aceitar">
            <Check size={14} />
          </button>
        )}
        {onReject && (
          <button onClick={onReject} className="p-2 rounded-lg bg-red-50 text-error hover:bg-red-100" title="Recusar">
            <X size={14} />
          </button>
        )}
        {amizade.status === AmizadeStatus.ACEITA && (
          <Link to={`/mensagens/${amizade.idAmizade}`} className="p-2 rounded-lg bg-blue-50 text-primary hover:bg-blue-100">
            <MessageSquare size={14} />
          </Link>
        )}
        {onRemove && (
          <button onClick={onRemove} className="p-2 rounded-lg hover:bg-gray-200 text-gray-400" title="Remover">
            <UserX size={14} />
          </button>
        )}
      </div>
    </div>
  )
}

export default function FriendsPage() {
  const { user } = useAuth()
  const qc = useQueryClient()
  const [tab, setTab] = useState<'amigos' | 'recebidas' | 'enviadas'>('amigos')
  const [busca, setBusca] = useState('')

  const { data: amizadesData, isLoading: la } = useQuery({
    queryKey: ['amizades-detalhadas', user?.idUsuario],
    queryFn: () => friendshipService.listarAmizadesDetalhadas(user!.idUsuario),
    enabled: !!user
  })

  const { data: recebidasData, isLoading: lr } = useQuery({
    queryKey: ['solicitacoes-recebidas-detalhadas', user?.idUsuario],
    queryFn: () => friendshipService.listarRecebidasDetalhadas(user!.idUsuario),
    enabled: !!user
  })

  const { data: enviadasData } = useQuery({
    queryKey: ['solicitacoes-enviadas-detalhadas', user?.idUsuario],
    queryFn: () => friendshipService.listarEnviadasDetalhadas(user!.idUsuario),
    enabled: !!user
  })

  const amigos = (amizadesData?.data ?? []).filter(a => a.status === AmizadeStatus.ACEITA)
  const recebidas = (recebidasData?.data ?? []).filter(a => a.status === AmizadeStatus.PENDENTE)
  const enviadas = (enviadasData?.data ?? []).filter(a => a.status === AmizadeStatus.PENDENTE)

  // Busca skills de cada amigo
  const amigosIds = amigos.map(a => a.outroUsuario.idUsuario)
  const skillsQueries = useQuery({
    queryKey: ['skills-amigos', amigosIds],
    queryFn: async () => {
      const results = await Promise.all(
        amigosIds.map(id => qualificationService.listarSkillsDoUsuario(id).then(r => ({ id, skills: r.data ?? [] })))
      )
      return Object.fromEntries(results.map(r => [r.id, r.skills]))
    },
    enabled: amigosIds.length > 0
  })

  const skillsMap = skillsQueries.data ?? {}

  // Todas as skills únicas dos amigos para o filtro
  const todasSkills = useMemo(() => {
    const set = new Map<number, Skill>()
    Object.values(skillsMap).flat().forEach(s => set.set(s.idSkill, s))
    return Array.from(set.values()).sort((a, b) => a.name.localeCompare(b.name))
  }, [skillsMap])

  // Filtro: busca por nome ou skill
  const amigosFiltrados = useMemo(() => {
    if (!busca.trim()) return amigos
    const termo = busca.toLowerCase()
    return amigos.filter(a => {
      const nomeMatch = a.outroUsuario.nome.toLowerCase().includes(termo)
      const skillMatch = (skillsMap[a.outroUsuario.idUsuario] ?? []).some(s =>
        s.name.toLowerCase().includes(termo)
      )
      return nomeMatch || skillMatch
    })
  }, [amigos, busca, skillsMap])

  const aceitarMutation = useMutation({
    mutationFn: friendshipService.aceitar,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['amizades-detalhadas'] })
      qc.invalidateQueries({ queryKey: ['solicitacoes-recebidas-detalhadas'] })
    }
  })

  const recusarMutation = useMutation({
    mutationFn: friendshipService.recusar,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['solicitacoes-recebidas-detalhadas'] })
  })

  const removerMutation = useMutation({
    mutationFn: friendshipService.remover,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['amizades-detalhadas'] })
  })

  const tabs = [
    { key: 'amigos', label: `Conexões (${amigos.length})` },
    { key: 'recebidas', label: `Recebidas${recebidas.length > 0 ? ` (${recebidas.length})` : ''}` },
    { key: 'enviadas', label: 'Enviadas' }
  ]

  return (
    <AppLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Conexões</h1>
          <p className="text-gray-500 text-sm mt-1">Gerencie suas conexões e solicitações.</p>
        </div>

        <div className="card overflow-hidden">
          <div className="flex border-b border-gray-100">
            {tabs.map(({ key, label }) => (
              <button
                key={key}
                onClick={() => setTab(key as typeof tab)}
                className={`flex-1 py-3 text-sm font-medium transition-colors ${
                  tab === key
                    ? 'text-primary border-b-2 border-primary'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                {label}
              </button>
            ))}
          </div>

          {/* Filtro por nome ou skill — só na aba amigos */}
          {tab === 'amigos' && amigos.length > 0 && (
            <div className="px-4 pt-4 space-y-3">
              <div className="relative">
                <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                <input
                  value={busca}
                  onChange={e => setBusca(e.target.value)}
                  className="input-field pl-9 text-sm"
                  placeholder="Buscar por nome ou skill..."
                />
              </div>
              {todasSkills.length > 0 && (
                <div className="flex flex-wrap gap-1.5 pb-1">
                  {todasSkills.map(s => (
                    <button
                      key={s.idSkill}
                      onClick={() => setBusca(busca === s.name ? '' : s.name)}
                      className={`text-xs px-2.5 py-1 rounded-full font-medium transition-colors ${
                        busca === s.name
                          ? 'bg-primary text-white'
                          : 'bg-gray-100 text-gray-600 hover:bg-primary/10 hover:text-primary'
                      }`}
                    >
                      {s.name}
                    </button>
                  ))}
                </div>
              )}
            </div>
          )}

          <div className="p-4">
            {(la || lr) ? (
              <div className="flex justify-center py-8">
                <Loader2 size={20} className="animate-spin text-primary" />
              </div>
            ) : (
              <>
                {tab === 'amigos' && (
                  amigos.length === 0 ? (
                    <div className="text-center py-10">
                      <Users size={32} className="text-gray-200 mx-auto mb-2" />
                      <p className="text-gray-400 text-sm">Nenhuma conexão ainda.</p>
                    </div>
                  ) : amigosFiltrados.length === 0 ? (
                    <div className="text-center py-8">
                      <p className="text-gray-400 text-sm">Nenhuma conexão encontrada para "{busca}".</p>
                      <button onClick={() => setBusca('')} className="text-primary text-sm mt-1 hover:underline">
                        Limpar filtro
                      </button>
                    </div>
                  ) : (
                    <div>
                      {amigosFiltrados.map((a) => (
                        <UserCard
                          key={a.idAmizade}
                          amizade={a}
                          skills={skillsMap[a.outroUsuario.idUsuario]}
                          onRemove={() => removerMutation.mutate(a.idAmizade)}
                        />
                      ))}
                    </div>
                  )
                )}

                {tab === 'recebidas' && (
                  recebidas.length === 0 ? (
                    <div className="text-center py-10">
                      <p className="text-gray-400 text-sm">Nenhuma solicitação recebida.</p>
                    </div>
                  ) : (
                    <div>
                      {recebidas.map((a) => (
                        <UserCard
                          key={a.idAmizade}
                          amizade={a}
                          onAccept={() => aceitarMutation.mutate(a.idAmizade)}
                          onReject={() => recusarMutation.mutate(a.idAmizade)}
                        />
                      ))}
                    </div>
                  )
                )}

                {tab === 'enviadas' && (
                  enviadas.length === 0 ? (
                    <div className="text-center py-10">
                      <p className="text-gray-400 text-sm">Nenhuma solicitação enviada pendente.</p>
                    </div>
                  ) : (
                    <div>
                      {enviadas.map((a) => (
                        <UserCard key={a.idAmizade} amizade={a} />
                      ))}
                    </div>
                  )
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </AppLayout>
  )
}
