import { useState, useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { MessageSquare, Loader2, Search } from 'lucide-react'
import { useAuth } from '../contexts/AuthContext'
import AppLayout from '../components/layout/AppLayout'
import { friendshipService } from '../services/friendship.service'
import { qualificationService } from '../services/qualification.service'
import { AmizadeStatus, type AmizadeDetalhada, type Skill } from '../types'

function ConversationItem({ amizade, skills }: { amizade: AmizadeDetalhada; skills?: Skill[] }) {
  const user = amizade.outroUsuario
  const initials = user?.nome
    ? user.nome.split(' ').map((n: string) => n[0]).slice(0, 2).join('').toUpperCase()
    : '??'

  return (
    <Link
      to={`/mensagens/${amizade.idAmizade}`}
      className="flex items-start gap-3 px-4 py-3.5 hover:bg-gray-50 transition-colors border-b border-gray-50 last:border-0"
    >
      <div className="w-10 h-10 rounded-full bg-primary flex items-center justify-center text-white text-sm font-bold flex-shrink-0 mt-0.5">
        {initials}
      </div>
      <div className="flex-1 min-w-0">
        <p className="font-medium text-gray-900 text-sm">{user.nome}</p>
        {skills && skills.length > 0 ? (
          <div className="flex flex-wrap gap-1 mt-1">
            {skills.slice(0, 3).map(s => (
              <span key={s.idSkill} className="inline-flex items-center px-2 py-0.5 rounded-full text-xs bg-primary/10 text-primary font-medium">
                {s.name}
              </span>
            ))}
            {skills.length > 3 && (
              <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs bg-gray-100 text-gray-500">
                +{skills.length - 3}
              </span>
            )}
          </div>
        ) : (
          user.bio && <p className="text-gray-400 text-xs mt-0.5 truncate">{user.bio}</p>
        )}
      </div>
      <MessageSquare size={16} className="text-gray-300 flex-shrink-0 mt-1" />
    </Link>
  )
}

export default function MessagesPage() {
  const { user } = useAuth()
  const [busca, setBusca] = useState('')

  const { data: amizadesData, isLoading } = useQuery({
    queryKey: ['amizades-detalhadas', user?.idUsuario],
    queryFn: () => friendshipService.listarAmizadesDetalhadas(user!.idUsuario),
    enabled: !!user
  })

  const amigos = (amizadesData?.data ?? []).filter(a => a.status === AmizadeStatus.ACEITA)
  const amigosIds = amigos.map(a => a.outroUsuario.idUsuario)

  const { data: skillsData } = useQuery({
    queryKey: ['skills-amigos', amigosIds],
    queryFn: async () => {
      const results = await Promise.all(
        amigosIds.map(id => qualificationService.listarSkillsDoUsuario(id).then(r => ({ id, skills: r.data ?? [] })))
      )
      return Object.fromEntries(results.map(r => [r.id, r.skills]))
    },
    enabled: amigosIds.length > 0
  })

  const skillsMap = skillsData ?? {}

  const amigosFiltrados = useMemo(() => {
    if (!busca.trim()) return amigos
    const termo = busca.toLowerCase()
    return amigos.filter(a => {
      const nomeMatch = a.outroUsuario.nome.toLowerCase().includes(termo)
      const skillMatch = (skillsMap[a.outroUsuario.idUsuario] ?? []).some((s: Skill) =>
        s.name.toLowerCase().includes(termo)
      )
      return nomeMatch || skillMatch
    })
  }, [amigos, busca, skillsMap])

  return (
    <AppLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Mensagens</h1>
          <p className="text-gray-500 text-sm mt-1">Converse com suas conexões.</p>
        </div>

        <div className="card overflow-hidden">
          {isLoading ? (
            <div className="flex justify-center py-12">
              <Loader2 size={20} className="animate-spin text-primary" />
            </div>
          ) : amigos.length === 0 ? (
            <div className="text-center py-12">
              <MessageSquare size={40} className="text-gray-200 mx-auto mb-3" />
              <p className="text-gray-500 font-medium">Nenhuma conversa</p>
              <p className="text-gray-400 text-sm mt-1">
                Conecte-se com outros usuários para começar a conversar.
              </p>
              <Link to="/amizades" className="btn-primary text-sm mt-4 inline-block">
                Encontrar conexões
              </Link>
            </div>
          ) : (
            <>
              <div className="px-4 pt-4 pb-2">
                <div className="relative">
                  <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                  <input
                    value={busca}
                    onChange={e => setBusca(e.target.value)}
                    className="input-field pl-9 text-sm"
                    placeholder="Buscar por nome ou skill..."
                  />
                </div>
              </div>
              {amigosFiltrados.length === 0 ? (
                <div className="text-center py-8">
                  <p className="text-gray-400 text-sm">Nenhuma conversa encontrada para "{busca}".</p>
                  <button onClick={() => setBusca('')} className="text-primary text-sm mt-1 hover:underline">
                    Limpar filtro
                  </button>
                </div>
              ) : (
                <div className="mt-2">
                  {amigosFiltrados.map((a) => (
                    <ConversationItem
                      key={a.idAmizade}
                      amizade={a}
                      skills={skillsMap[a.outroUsuario.idUsuario]}
                    />
                  ))}
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </AppLayout>
  )
}
