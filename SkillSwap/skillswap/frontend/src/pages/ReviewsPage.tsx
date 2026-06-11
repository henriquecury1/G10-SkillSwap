import { useQuery } from '@tanstack/react-query'
import { Star, Loader2 } from 'lucide-react'
import { useAuth } from '../contexts/AuthContext'
import AppLayout from '../components/layout/AppLayout'
import StarRating from '../components/ui/StarRating'
import { reviewService } from '../services/review.service'

export default function ReviewsPage() {
  const { user } = useAuth()

  const { data, isLoading } = useQuery({
    queryKey: ['avaliacoes', user?.idUsuario],
    queryFn: () => reviewService.listarAvaliacoes(user!.idUsuario),
    enabled: !!user
  })

  const avaliacoes = data?.data ?? []
  const notaMedia = avaliacoes.length > 0
    ? avaliacoes.reduce((acc, a) => acc + a.nota, 0) / avaliacoes.length
    : 0

  return (
    <AppLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Avaliações</h1>
          <p className="text-gray-500 text-sm mt-1">Avaliações que você recebeu de outros usuários.</p>
        </div>

        {/* Resumo */}
        {avaliacoes.length > 0 && (
          <div className="card p-6 flex items-center gap-6">
            <div className="text-center">
              <p className="text-5xl font-bold text-gray-900">{notaMedia.toFixed(1)}</p>
              <StarRating value={notaMedia} size={20} />
              <p className="text-gray-400 text-sm mt-1">{avaliacoes.length} avaliações</p>
            </div>
            <div className="flex-1 space-y-2">
              {[5, 4, 3, 2, 1].map((nota) => {
                const count = avaliacoes.filter(a => Math.round(a.nota) === nota).length
                const pct = avaliacoes.length > 0 ? (count / avaliacoes.length) * 100 : 0
                return (
                  <div key={nota} className="flex items-center gap-2">
                    <span className="text-xs text-gray-500 w-3">{nota}</span>
                    <Star size={12} className="text-warning fill-warning flex-shrink-0" />
                    <div className="flex-1 h-1.5 bg-gray-100 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-warning rounded-full transition-all"
                        style={{ width: `${pct}%` }}
                      />
                    </div>
                    <span className="text-xs text-gray-400 w-4">{count}</span>
                  </div>
                )
              })}
            </div>
          </div>
        )}

        {/* Lista */}
        <div className="card p-5">
          <h2 className="font-semibold text-gray-900 mb-4">Histórico</h2>
          {isLoading ? (
            <div className="flex justify-center py-8">
              <Loader2 size={20} className="animate-spin text-primary" />
            </div>
          ) : avaliacoes.length === 0 ? (
            <div className="text-center py-12">
              <Star size={40} className="text-gray-200 mx-auto mb-3" />
              <p className="text-gray-500 font-medium">Nenhuma avaliação ainda</p>
              <p className="text-gray-400 text-sm mt-1">
                Conecte-se com outros usuários e troque conhecimentos para receber avaliações.
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {avaliacoes.map((a) => (
                <div key={a.idAvaliacao} className="flex items-center gap-4 py-3 border-b border-gray-50 last:border-0">
                  <div className="w-9 h-9 rounded-full bg-gray-100 flex items-center justify-center text-sm text-gray-600 font-bold flex-shrink-0">
                    {a.avaliador}
                  </div>
                  <div className="flex-1">
                    <StarRating value={a.nota} size={15} />
                    <p className="text-xs text-gray-400 mt-0.5">
                      {new Date(a.dataHora).toLocaleDateString('pt-BR', {
                        day: '2-digit', month: 'long', year: 'numeric'
                      })}
                    </p>
                  </div>
                  <span className="text-lg font-bold text-gray-900">{a.nota.toFixed(1)}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  )
}
