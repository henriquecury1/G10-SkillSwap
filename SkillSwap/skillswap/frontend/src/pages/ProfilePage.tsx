import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { UserPlus, MessageSquare, Star, Zap, Loader2, Check, X } from 'lucide-react'
import { useAuth } from '../contexts/AuthContext'
import AppLayout from '../components/layout/AppLayout'
import StarRating from '../components/ui/StarRating'
import { userService } from '../services/user.service'
import { reviewService } from '../services/review.service'
import { friendshipService } from '../services/friendship.service'
import { AmizadeStatus } from '../types'

export default function ProfilePage() {
  const { id } = useParams<{ id: string }>()
  const { user: me } = useAuth()
  const qc = useQueryClient()
  const idUsuario = Number(id)
  const isOwn = me?.idUsuario === idUsuario
  const [reviewNota, setReviewNota] = useState(0)
  const [reviewMsg, setReviewMsg] = useState('')

  const { data: perfilData, isLoading: loadingUser } = useQuery({
    queryKey: ['perfil', idUsuario],
    queryFn: () => userService.buscarPerfil(idUsuario)
  })

  const { data: avaliacoesData } = useQuery({
    queryKey: ['avaliacoes', idUsuario],
    queryFn: () => reviewService.listarAvaliacoes(idUsuario)
  })

  const { data: amizadesData } = useQuery({
    queryKey: ['amizades', me?.idUsuario],
    queryFn: () => friendshipService.listarAmizades(me!.idUsuario),
    enabled: !!me && !isOwn
  })

  const amizadeComEle = (amizadesData?.data ?? []).find(
    (a) => a.usuario1 === idUsuario || a.usuario2 === idUsuario
  )
  const jaAmigos = amizadeComEle?.status === AmizadeStatus.ACEITA
  const pendente = amizadeComEle?.status === AmizadeStatus.PENDENTE

  const solicitarMutation = useMutation({
    mutationFn: () => friendshipService.enviarSolicitacao(idUsuario),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['amizades'] })
  })

  const avaliarMutation = useMutation({
    mutationFn: () => reviewService.avaliar(idUsuario, reviewNota),
    onSuccess: () => {
      setReviewMsg('Avaliação enviada!')
      qc.invalidateQueries({ queryKey: ['avaliacoes', idUsuario] })
    },
    onError: () => setReviewMsg('Erro ao enviar avaliação.')
  })

  const usuario = perfilData?.data?.usuario
  const skills = perfilData?.data?.skills ?? []
  const avaliacoes = avaliacoesData?.data ?? []
  const notaMedia = avaliacoes.length > 0
    ? (avaliacoes.reduce((acc, a) => acc + a.nota, 0) / avaliacoes.length)
    : 0

  const initials = usuario?.nome
    ? usuario.nome.split(' ').map((n: string) => n[0]).slice(0, 2).join('').toUpperCase()
    : '??'

  if (loadingUser) {
    return (
      <AppLayout>
        <div className="flex justify-center py-20">
          <Loader2 size={24} className="animate-spin text-primary" />
        </div>
      </AppLayout>
    )
  }

  if (!usuario) {
    return (
      <AppLayout>
        <div className="text-center py-20">
          <p className="text-gray-500">Usuário não encontrado.</p>
        </div>
      </AppLayout>
    )
  }

  return (
    <AppLayout>
      <div className="space-y-6">
        {/* Header do perfil */}
        <div className="card p-6">
          <div className="flex flex-col sm:flex-row sm:items-start gap-4">
            <div className="w-16 h-16 rounded-full bg-primary flex items-center justify-center text-white text-2xl font-bold flex-shrink-0">
              {initials}
            </div>
            <div className="flex-1 min-w-0">
              <h1 className="text-2xl font-bold text-gray-900">{usuario.nome}</h1>
              <p className="text-gray-500 text-sm">{usuario.email}</p>
              {usuario.bio && (
                <p className="text-gray-600 text-sm mt-2 leading-relaxed">{usuario.bio}</p>
              )}
              {avaliacoes.length > 0 && (
                <div className="flex items-center gap-2 mt-2">
                  <StarRating value={notaMedia} size={14} />
                  <span className="text-sm text-gray-500">
                    {notaMedia.toFixed(1)} ({avaliacoes.length} avaliações)
                  </span>
                </div>
              )}
            </div>

            {!isOwn && me && (
              <div className="flex gap-2 flex-shrink-0">
                {jaAmigos && amizadeComEle && (
                  <Link
                    to={`/mensagens/${amizadeComEle.idAmizade}`}
                    className="btn-outline flex items-center gap-1.5 text-sm"
                  >
                    <MessageSquare size={14} />
                    Mensagem
                  </Link>
                )}
                {!jaAmigos && !pendente && (
                  <button
                    onClick={() => solicitarMutation.mutate()}
                    disabled={solicitarMutation.isPending}
                    className="btn-primary flex items-center gap-1.5 text-sm"
                  >
                    {solicitarMutation.isPending
                      ? <Loader2 size={14} className="animate-spin" />
                      : <UserPlus size={14} />}
                    Conectar
                  </button>
                )}
                {pendente && (
                  <span className="badge bg-yellow-100 text-warning px-3 py-1.5 text-xs">
                    Solicitação enviada
                  </span>
                )}
              </div>
            )}
            {isOwn && (
              <Link to="/configuracoes" className="btn-secondary text-sm flex-shrink-0">
                Editar perfil
              </Link>
            )}
          </div>
        </div>

        {/* Skills */}
        <div className="card p-5">
          <h2 className="font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <Zap size={16} className="text-primary" />
            Skills
          </h2>
          {skills.length === 0 ? (
            <p className="text-gray-400 text-sm">Nenhuma skill cadastrada.</p>
          ) : (
            <div className="flex flex-wrap gap-2">
              {skills.map((s) => (
                <span key={s.idSkill} className="badge bg-primary/10 text-primary">
                  {s.name}
                </span>
              ))}
            </div>
          )}
        </div>

        {/* Avaliações */}
        <div className="card p-5">
          <h2 className="font-semibold text-gray-900 mb-4 flex items-center gap-2">
            <Star size={16} className="text-warning" />
            Avaliações {avaliacoes.length > 0 && `(${avaliacoes.length})`}
          </h2>

          {/* Formulário de avaliação para outros usuários */}
          {!isOwn && me && jaAmigos && (
            <div className="bg-gray-50 rounded-lg p-4 mb-4">
              <p className="text-sm font-medium text-gray-700 mb-2">Avaliar este usuário</p>
              <StarRating value={reviewNota} interactive onChange={setReviewNota} size={22} />
              {reviewMsg && (
                <p className={`text-xs mt-2 ${reviewMsg.includes('Erro') ? 'text-error' : 'text-success'}`}>
                  {reviewMsg}
                </p>
              )}
              <button
                onClick={() => { setReviewMsg(''); avaliarMutation.mutate() }}
                disabled={reviewNota === 0 || avaliarMutation.isPending}
                className="btn-primary text-sm mt-3 flex items-center gap-1.5"
              >
                {avaliarMutation.isPending ? <Loader2 size={13} className="animate-spin" /> : <Check size={13} />}
                Enviar avaliação
              </button>
            </div>
          )}

          {avaliacoes.length === 0 ? (
            <p className="text-gray-400 text-sm">Nenhuma avaliação ainda.</p>
          ) : (
            <div className="space-y-3">
              {avaliacoes.map((a) => (
                <div key={a.idAvaliacao} className="flex items-center gap-3 py-2 border-b border-gray-50 last:border-0">
                  <div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center text-xs text-gray-500 font-bold flex-shrink-0">
                    {a.avaliador}
                  </div>
                  <div className="flex-1">
                    <StarRating value={a.nota} size={14} />
                    <p className="text-xs text-gray-400 mt-0.5">
                      {new Date(a.dataHora).toLocaleDateString('pt-BR')}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  )
}
