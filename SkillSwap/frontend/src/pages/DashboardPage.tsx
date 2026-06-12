import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Users, MessageSquare, Star, Zap, ArrowRight, UserPlus } from 'lucide-react'
import { useAuth } from '../contexts/AuthContext'
import AppLayout from '../components/layout/AppLayout'
import { friendshipService } from '../services/friendship.service'
import { qualificationService } from '../services/qualification.service'
import { reviewService } from '../services/review.service'
import StarRating from '../components/ui/StarRating'
import { AmizadeStatus } from '../types'
import { userService } from '../services/user.service'

export default function DashboardPage() {
  const { user } = useAuth()

  const { data: amizadesData } = useQuery({
    queryKey: ['amizades', user?.idUsuario],
    queryFn: () => friendshipService.listarAmizades(user!.idUsuario),
    enabled: !!user
  })

  const { data: solicitacoesData } = useQuery({
    queryKey: ['solicitacoes-recebidas', user?.idUsuario],
    queryFn: () => friendshipService.listarRecebidas(user!.idUsuario),
    enabled: !!user
  })

  const { data: perfilData } = useQuery({
    queryKey: ['perfil', user?.idUsuario],
    queryFn: () => userService.buscarPerfil(user!.idUsuario),
    enabled: !!user
  })

  const { data: avaliacoesData } = useQuery({
    queryKey: ['avaliacoes', user?.idUsuario],
    queryFn: () => reviewService.listarAvaliacoes(user!.idUsuario),
    enabled: !!user
  })

  const amizadesAceitas = (amizadesData?.data ?? []).filter(a => a.status === AmizadeStatus.ACEITA)
  const pendentes = (solicitacoesData?.data ?? []).filter(a => a.status === AmizadeStatus.PENDENTE)
  const skills = perfilData?.data?.skills ?? []
  const avaliacoes = avaliacoesData?.data ?? []
  const notaMedia = avaliacoes.length > 0
    ? (avaliacoes.reduce((acc, a) => acc + a.nota, 0) / avaliacoes.length)
    : 0

  const initials = user?.nome
    ? user.nome.split(' ').map((n) => n[0]).slice(0, 2).join('').toUpperCase()
    : '??'

  return (
    <AppLayout>
      <div className="space-y-6">
        {/* Boas vindas */}
        <div className="card p-6 flex items-center gap-4">
          <div className="w-14 h-14 rounded-full bg-primary flex items-center justify-center text-white text-xl font-bold flex-shrink-0">
            {initials}
          </div>
          <div className="flex-1 min-w-0">
            <h1 className="text-xl font-bold text-gray-900">
              Olá, {user?.nome?.split(' ')[0]}!
            </h1>
            <p className="text-gray-500 text-sm mt-0.5 truncate">{user?.email}</p>
            {user?.bio && <p className="text-gray-600 text-sm mt-1 line-clamp-1">{user.bio}</p>}
          </div>
          <Link
            to={`/perfil/${user?.idUsuario}`}
            className="btn-outline text-sm hidden sm:flex items-center gap-1"
          >
            Ver perfil
            <ArrowRight size={14} />
          </Link>
        </div>

        {/* Alerta de solicitações pendentes */}
        {pendentes.length > 0 && (
          <Link
            to="/amizades"
            className="flex items-center gap-3 bg-blue-50 border border-blue-200 rounded-xl px-4 py-3 hover:bg-blue-100 transition-colors"
          >
            <UserPlus size={18} className="text-primary" />
            <span className="text-primary text-sm font-medium">
              {pendentes.length} nova{pendentes.length > 1 ? 's' : ''} solicitação{pendentes.length > 1 ? 'ões' : ''} de conexão
            </span>
            <ArrowRight size={14} className="text-primary ml-auto" />
          </Link>
        )}

        {/* Cards de resumo */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          {[
            {
              icon: Users,
              label: 'Conexões',
              value: amizadesAceitas.length,
              to: '/amizades',
              color: 'text-primary bg-blue-50'
            },
            {
              icon: Zap,
              label: 'Skills',
              value: skills.length,
              to: '/skills',
              color: 'text-purple-600 bg-purple-50'
            },
            {
              icon: Star,
              label: 'Avaliações',
              value: avaliacoes.length,
              to: '/avaliacoes',
              color: 'text-warning bg-yellow-50'
            },
            {
              icon: MessageSquare,
              label: 'Mensagens',
              value: amizadesAceitas.length,
              to: '/mensagens',
              color: 'text-success bg-green-50'
            }
          ].map(({ icon: Icon, label, value, to, color }) => (
            <Link key={label} to={to} className="card p-4 hover:shadow-md transition-shadow">
              <div className={`w-9 h-9 rounded-lg ${color} flex items-center justify-center mb-3`}>
                <Icon size={18} />
              </div>
              <p className="text-2xl font-bold text-gray-900">{value}</p>
              <p className="text-gray-500 text-xs mt-0.5">{label}</p>
            </Link>
          ))}
        </div>

        {/* Nota média */}
        {avaliacoes.length > 0 && (
          <div className="card p-5">
            <div className="flex items-center justify-between mb-3">
              <h2 className="font-semibold text-gray-900">Sua reputação</h2>
              <Link to="/avaliacoes" className="text-primary text-sm hover:underline">Ver todas</Link>
            </div>
            <div className="flex items-center gap-3">
              <span className="text-3xl font-bold text-gray-900">{notaMedia.toFixed(1)}</span>
              <div>
                <StarRating value={notaMedia} size={18} />
                <p className="text-gray-400 text-xs mt-0.5">{avaliacoes.length} avaliação{avaliacoes.length !== 1 ? 'ões' : ''}</p>
              </div>
            </div>
          </div>
        )}

        {/* Skills */}
        <div className="card p-5">
          <div className="flex items-center justify-between mb-3">
            <h2 className="font-semibold text-gray-900">Suas skills</h2>
            <Link to="/skills" className="text-primary text-sm hover:underline">Gerenciar</Link>
          </div>
          {skills.length === 0 ? (
            <div className="text-center py-6">
              <Zap size={32} className="text-gray-200 mx-auto mb-2" />
              <p className="text-gray-400 text-sm">Nenhuma skill cadastrada ainda.</p>
              <Link to="/skills" className="text-primary text-sm font-medium mt-1 inline-block">
                Adicionar skills
              </Link>
            </div>
          ) : (
            <div className="flex flex-wrap gap-2">
              {skills.map((skill) => (
                <span
                  key={skill.idSkill}
                  className="badge bg-primary/10 text-primary"
                >
                  {skill.name}
                </span>
              ))}
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  )
}
