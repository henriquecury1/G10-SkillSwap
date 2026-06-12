import { useState, useEffect, useRef } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { ArrowLeft, Send, Loader2, Trash2 } from 'lucide-react'
import { useAuth } from '../contexts/AuthContext'
import AppLayout from '../components/layout/AppLayout'
import { messageService } from '../services/message.service'
import { friendshipService } from '../services/friendship.service'
import { userService } from '../services/user.service'
import { AmizadeStatus } from '../types'

export default function ChatPage() {
  const { idAmizade } = useParams<{ idAmizade: string }>()
  const { user } = useAuth()
  const qc = useQueryClient()
  const [text, setText] = useState('')
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const idAmizadeNum = Number(idAmizade)

  const { data: amizadesData } = useQuery({
    queryKey: ['amizades', user?.idUsuario],
    queryFn: () => friendshipService.listarAmizades(user!.idUsuario),
    enabled: !!user
  })

  const amizade = (amizadesData?.data ?? []).find(
    a => a.idAmizade === idAmizadeNum && a.status === AmizadeStatus.ACEITA
  )
  const otherId = amizade ? (amizade.usuario1 === user?.idUsuario ? amizade.usuario2 : amizade.usuario1) : null

  const { data: otherUserData } = useQuery({
    queryKey: ['usuario', otherId],
    queryFn: () => userService.buscarPorId(otherId!),
    enabled: !!otherId
  })

  const { data: mensagensData, isLoading } = useQuery({
    queryKey: ['mensagens', idAmizadeNum],
    queryFn: () => messageService.listarMensagens(idAmizadeNum),
    refetchInterval: 3000
  })

  const sendMutation = useMutation({
    mutationFn: (conteudo: string) => messageService.enviar(idAmizadeNum, conteudo),
    onSuccess: () => {
      setText('')
      qc.invalidateQueries({ queryKey: ['mensagens', idAmizadeNum] })
    }
  })

  const deleteMutation = useMutation({
    mutationFn: messageService.deletar,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['mensagens', idAmizadeNum] })
  })

  const mensagens = mensagensData?.data ?? []
  const otherUser = otherUserData?.data

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [mensagens])

  function handleSend() {
    if (!text.trim()) return
    sendMutation.mutate(text.trim())
  }

  const otherInitials = otherUser?.nome
    ? otherUser.nome.split(' ').map((n: string) => n[0]).slice(0, 2).join('').toUpperCase()
    : String(otherId)

  return (
    <AppLayout>
      <div className="flex flex-col h-[calc(100vh-8rem)]">
        {/* Header */}
        <div className="card p-4 flex items-center gap-3 mb-4">
          <Link to="/mensagens" className="p-1.5 rounded-lg hover:bg-gray-100 text-gray-500">
            <ArrowLeft size={18} />
          </Link>
          <div className="w-9 h-9 rounded-full bg-primary flex items-center justify-center text-white text-sm font-bold">
            {otherInitials}
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-semibold text-gray-900 text-sm">{otherUser?.nome ?? `Usuário #${otherId}`}</p>
            {otherUser?.bio && <p className="text-gray-400 text-xs truncate">{otherUser.bio}</p>}
          </div>
          {otherId && (
            <Link to={`/perfil/${otherId}`} className="text-primary text-xs hover:underline">
              Ver perfil
            </Link>
          )}
        </div>

        {/* Mensagens */}
        <div className="card flex-1 flex flex-col overflow-hidden">
          <div className="flex-1 overflow-y-auto p-4 space-y-3">
            {isLoading ? (
              <div className="flex justify-center py-8">
                <Loader2 size={20} className="animate-spin text-primary" />
              </div>
            ) : mensagens.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-400 text-sm">Nenhuma mensagem ainda. Diga olá!</p>
              </div>
            ) : (
              mensagens.map((m) => {
                const isMe = m.remetente === user?.idUsuario
                return (
                  <div
                    key={m.idMensagem}
                    className={`flex items-end gap-2 ${isMe ? 'justify-end' : 'justify-start'}`}
                  >
                    {!isMe && (
                      <div className="w-7 h-7 rounded-full bg-gray-200 flex items-center justify-center text-xs text-gray-500 font-bold flex-shrink-0">
                        {otherInitials?.charAt(0)}
                      </div>
                    )}
                    <div className={`max-w-xs sm:max-w-sm group relative`}>
                      <div
                        className={`px-3.5 py-2 rounded-2xl text-sm leading-relaxed ${
                          isMe
                            ? 'bg-primary text-white rounded-br-sm'
                            : 'bg-gray-100 text-gray-800 rounded-bl-sm'
                        }`}
                      >
                        {m.conteudo}
                      </div>
                      <div className={`flex items-center gap-1 mt-0.5 ${isMe ? 'justify-end' : 'justify-start'}`}>
                        <span className="text-xs text-gray-400">
                          {new Date(m.dataHora).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}
                        </span>
                        {isMe && (
                          <button
                            onClick={() => deleteMutation.mutate(m.idMensagem)}
                            className="opacity-0 group-hover:opacity-100 transition-opacity p-0.5 text-gray-300 hover:text-error"
                          >
                            <Trash2 size={11} />
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                )
              })
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Input */}
          <div className="border-t border-gray-100 p-3 flex gap-2">
            <input
              value={text}
              onChange={(e) => setText(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && !e.shiftKey && handleSend()}
              className="input-field flex-1 text-sm"
              placeholder="Digite uma mensagem..."
            />
            <button
              onClick={handleSend}
              disabled={!text.trim() || sendMutation.isPending}
              className="btn-primary p-2.5 flex items-center justify-center"
            >
              {sendMutation.isPending
                ? <Loader2 size={16} className="animate-spin" />
                : <Send size={16} />}
            </button>
          </div>
        </div>
      </div>
    </AppLayout>
  )
}
