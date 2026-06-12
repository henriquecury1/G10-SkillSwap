import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Loader2, Sparkles, Users, Zap, Clock, ChevronRight, RefreshCw, UserPlus, Check } from 'lucide-react'
import AppLayout from '../components/layout/AppLayout'
import { useAuth } from '../contexts/AuthContext'
import { qualificationService } from '../services/qualification.service'
import { skillService } from '../services/skill.service'
import { friendshipService } from '../services/friendship.service'
import { userService } from '../services/user.service'
import api from '../services/api'
import type { Skill, Usuario } from '../types'

// ─── tipos internos ───────────────────────────────────────────────────────────
interface MatchResult {
  usuario: Usuario
  skills: Skill[]
  score: number
  reason: string
  compatibleSkills: string[]
  scheduleOverlap: string[]
}

interface MatchResponse {
  analysis: string
  matches: MatchResult[]
}

// ─── horários disponíveis ─────────────────────────────────────────────────────
const HORARIOS = [
  'Seg. Manhã','Seg. Tarde','Seg. Noite',
  'Ter. Manhã','Ter. Tarde','Ter. Noite',
  'Qua. Manhã','Qua. Tarde','Qua. Noite',
  'Qui. Manhã','Qui. Tarde','Qui. Noite',
  'Sex. Manhã','Sex. Tarde','Sex. Noite',
  'Sáb. Manhã','Sáb. Tarde',
  'Dom. Manhã',
]

// ─── cores de avatar ──────────────────────────────────────────────────────────
const COLORS = ['#534AB7','#D85A30','#993556','#0F6E56','#185FA5','#3B6D11','#854F0B','#A32D2D','#6B3FA0','#1D5FA0']
function avatarColor(id: number) { return COLORS[id % COLORS.length] }
function initials(nome: string) {
  return nome.split(' ').slice(0, 2).map(n => n[0]).join('').toUpperCase()
}

// ─── score badge ──────────────────────────────────────────────────────────────
function ScoreBadge({ score }: { score: number }) {
  const cls = score >= 80 ? 'bg-emerald-600' : score >= 60 ? 'bg-teal-600' : 'bg-gray-500'
  return (
    <span className={`inline-flex items-center gap-1 ${cls} text-white text-xs font-bold px-3 py-1 rounded-full`}>
      <Sparkles size={10} />
      {score}% match
    </span>
  )
}

// ─────────────────────────────────────────────────────────────────────────────
export default function MatchingPage() {
  const { user } = useAuth()

  // ── step: 'select' | 'loading' | 'results'
  const [step, setStep] = useState<'select' | 'loading' | 'results'>('select')
  const [horariosSel, setHorariosSel] = useState<string[]>([])
  const [loadingMsg, setLoadingMsg] = useState('Analisando perfis...')
  const [matchData, setMatchData] = useState<MatchResponse | null>(null)
  const [solicitados, setSolicitados] = useState<Set<number>>(new Set())
  const [filterScore, setFilterScore] = useState<'all' | 'high' | 'mid'>('all')

  // ── dados do usuário logado
  const { data: mySkillsData } = useQuery({
    queryKey: ['skills-usuario', user?.idUsuario],
    queryFn: () => qualificationService.listarSkillsDoUsuario(user!.idUsuario),
    enabled: !!user,
  })
  const mySkills = mySkillsData?.data ?? []

  // ── todos os usuários + skills (para mostrar nos cards)
  // Não temos um endpoint "listar todos usuários" — o matching é feito no backend
  // Vamos apenas chamar /matching e receber os dados prontos

  function toggleHorario(h: string) {
    setHorariosSel(prev =>
      prev.includes(h) ? prev.filter(x => x !== h) : [...prev, h]
    )
  }

  // ── Dispara a análise de matching no backend
  async function iniciarMatching() {
    if (horariosSel.length === 0) return

    setStep('loading')

    const msgs = [
      'Analisando suas habilidades...',
      'Cruzando perfis da plataforma...',
      'Calculando compatibilidade de horários...',
      'Gerando recomendações personalizadas...',
    ]
    let mi = 0
    const interval = setInterval(() => {
      mi = (mi + 1) % msgs.length
      setLoadingMsg(msgs[mi])
    }, 1400)

    try {
      const { data } = await api.post('/matching', {
        horarios: horariosSel,
      })
      clearInterval(interval)
      setMatchData(data.data)
      setStep('results')
    } catch {
      clearInterval(interval)
      setStep('select')
    }
  }

  async function solicitarConexao(idUsuario: number) {
    try {
      await friendshipService.enviarSolicitacao(idUsuario)
      setSolicitados(prev => new Set([...prev, idUsuario]))
    } catch {
      // já solicitado ou erro
      setSolicitados(prev => new Set([...prev, idUsuario]))
    }
  }

  // ─── STEP: selecionar horários ───────────────────────────────────────────
  if (step === 'select') {
    return (
      <AppLayout>
        <div className="max-w-2xl mx-auto">

          {/* header */}
          <div className="mb-6">
            <div className="flex items-center gap-2 mb-2">
              <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-violet-500 to-teal-500 flex items-center justify-center">
                <Sparkles size={16} className="text-white" />
              </div>
              <h1 className="text-2xl font-bold text-gray-900">Matching Inteligente</h1>
            </div>
            <p className="text-gray-500 text-sm">
              Nossa IA cruza suas skills com todos os usuários da plataforma e sugere as conexões mais compatíveis.
            </p>
          </div>

          {/* como funciona */}
          <div className="grid grid-cols-3 gap-3 mb-6">
            {[
              { n: '01', title: 'Suas skills', desc: 'Já cadastradas no seu perfil' },
              { n: '02', title: 'IA analisa', desc: 'Cruza skills, nível e horários' },
              { n: '03', title: 'Match perfeito', desc: 'Conexões ranqueadas por compatibilidade' },
            ].map(({ n, title, desc }) => (
              <div key={n} className="card p-4 text-center">
                <div className="text-xs text-teal-600 font-bold tracking-widest mb-1">{n}</div>
                <div className="font-semibold text-sm text-gray-800">{title}</div>
                <div className="text-xs text-gray-400 mt-1">{desc}</div>
              </div>
            ))}
          </div>

          {/* suas skills atuais */}
          {mySkills.length > 0 && (
            <div className="card p-4 mb-6">
              <div className="flex items-center gap-2 mb-3">
                <Zap size={14} className="text-primary" />
                <span className="text-sm font-semibold text-gray-700">Suas {mySkills.length} skills cadastradas</span>
              </div>
              <div className="flex flex-wrap gap-2">
                {mySkills.map(s => (
                  <span key={s.idSkill} className="text-xs bg-primary/10 text-primary px-3 py-1 rounded-full font-medium">
                    {s.name}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* selecionar horários */}
          <div className="card p-5">
            <div className="flex items-center gap-2 mb-1">
              <Clock size={14} className="text-primary" />
              <span className="font-semibold text-gray-800">Quando você está disponível?</span>
            </div>
            <p className="text-xs text-gray-400 mb-4">Selecione os períodos para encontrar quem tem horário compatível</p>

            <div className="grid grid-cols-3 gap-2">
              {HORARIOS.map(h => (
                <button
                  key={h}
                  onClick={() => toggleHorario(h)}
                  className={`py-2 px-3 rounded-lg text-xs font-medium border transition-all ${
                    horariosSel.includes(h)
                      ? 'bg-teal-50 border-teal-500 text-teal-700'
                      : 'bg-white border-gray-200 text-gray-600 hover:border-teal-300'
                  }`}
                >
                  {h}
                </button>
              ))}
            </div>

            <button
              onClick={iniciarMatching}
              disabled={horariosSel.length === 0}
              className="w-full mt-5 py-3 rounded-xl bg-gradient-to-r from-violet-600 to-teal-600 text-white font-semibold text-sm flex items-center justify-center gap-2 disabled:opacity-40 disabled:cursor-not-allowed hover:opacity-95 transition-opacity"
            >
              <Sparkles size={16} />
              Analisar com IA
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      </AppLayout>
    )
  }

  // ─── STEP: loading ───────────────────────────────────────────────────────
  if (step === 'loading') {
    return (
      <AppLayout>
        <div className="max-w-sm mx-auto flex flex-col items-center justify-center min-h-[60vh] text-center">
          <div className="w-16 h-16 rounded-2xl bg-gradient-to-br from-violet-500 to-teal-500 flex items-center justify-center mb-6 shadow-lg">
            <Loader2 size={28} className="text-white animate-spin" />
          </div>
          <h2 className="text-xl font-bold text-gray-900 mb-2">IA analisando perfis</h2>
          <p className="text-sm text-gray-400 mb-6">{loadingMsg}</p>
          <div className="flex gap-1.5">
            {[0, 1, 2].map(i => (
              <div
                key={i}
                className="w-2 h-2 rounded-full bg-teal-500 animate-bounce"
                style={{ animationDelay: `${i * 0.15}s` }}
              />
            ))}
          </div>
        </div>
      </AppLayout>
    )
  }

  // ─── STEP: results ───────────────────────────────────────────────────────
  const matches = matchData?.matches ?? []
  const filtered = matches.filter(m => {
    if (filterScore === 'high') return m.score >= 80
    if (filterScore === 'mid') return m.score >= 60 && m.score < 80
    return true
  })

  return (
    <AppLayout>
      <div className="max-w-2xl mx-auto">

        {/* header */}
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Seus Matches</h1>
            <p className="text-sm text-gray-400">{matches.length} parceiros encontrados pela IA</p>
          </div>
          <button
            onClick={() => { setStep('select'); setHorariosSel([]); setMatchData(null) }}
            className="flex items-center gap-1.5 text-xs text-gray-500 hover:text-gray-700 border border-gray-200 rounded-lg px-3 py-2 transition-colors"
          >
            <RefreshCw size={13} />
            Refazer
          </button>
        </div>

        {/* banner da análise da IA */}
        {matchData?.analysis && (
          <div className="bg-gradient-to-r from-slate-800 to-slate-700 rounded-xl p-4 mb-5 flex gap-3">
            <div className="text-2xl flex-shrink-0">🤖</div>
            <div>
              <div className="text-teal-400 text-xs font-semibold mb-1 flex items-center gap-1">
                <Sparkles size={10} /> ANÁLISE DA IA
              </div>
              <p className="text-slate-300 text-sm leading-relaxed">{matchData.analysis}</p>
            </div>
          </div>
        )}

        {/* filtros */}
        <div className="flex items-center gap-2 mb-5 flex-wrap">
          <span className="text-xs text-gray-500 font-medium">Filtrar:</span>
          {([['all', 'Todos'], ['high', '80%+ match'], ['mid', '60–79%']] as const).map(([val, label]) => (
            <button
              key={val}
              onClick={() => setFilterScore(val)}
              className={`text-xs px-3 py-1.5 rounded-full border transition-all font-medium ${
                filterScore === val
                  ? 'bg-teal-600 text-white border-teal-600'
                  : 'bg-white text-gray-600 border-gray-200 hover:border-teal-400'
              }`}
            >
              {label}
            </button>
          ))}
          <span className="ml-auto text-xs text-gray-400">{filtered.length} resultado{filtered.length !== 1 ? 's' : ''}</span>
        </div>

        {/* cards */}
        <div className="space-y-4">
          {filtered.map((m, i) => {
            const u = m.usuario
            const jaConectado = solicitados.has(u.idUsuario)
            return (
              <div
                key={u.idUsuario}
                className="card p-5 hover:border-teal-400 transition-all"
                style={{ animationDelay: `${i * 0.05}s` }}
              >
                {/* topo: avatar + nome + score */}
                <div className="flex items-start gap-3">
                  <div
                    className="w-11 h-11 rounded-full flex items-center justify-center text-white text-sm font-bold flex-shrink-0"
                    style={{ background: avatarColor(u.idUsuario) }}
                  >
                    {initials(u.nome)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="font-semibold text-gray-900">{u.nome}</div>
                    {u.bio && <div className="text-xs text-gray-400 truncate mt-0.5">{u.bio}</div>}
                  </div>
                  <ScoreBadge score={m.score} />
                </div>

                {/* skills do match */}
                {m.skills.length > 0 && (
                  <div className="mt-4">
                    <div className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">Skills</div>
                    <div className="flex flex-wrap gap-1.5">
                      {m.skills.map(s => {
                        const isCompat = m.compatibleSkills.some(c =>
                          c.toLowerCase().includes(s.name.toLowerCase()) ||
                          s.name.toLowerCase().includes(c.toLowerCase())
                        )
                        return (
                          <span
                            key={s.idSkill}
                            className={`text-xs px-2.5 py-1 rounded-full font-medium ${
                              isCompat
                                ? 'bg-teal-50 text-teal-700 border border-teal-200'
                                : 'bg-gray-50 text-gray-600 border border-gray-100'
                            }`}
                          >
                            {isCompat && <span className="mr-1">✦</span>}
                            {s.name}
                          </span>
                        )
                      })}
                    </div>
                  </div>
                )}

                {/* análise da IA */}
                {m.reason && (
                  <div className="mt-3 bg-gradient-to-r from-slate-50 to-teal-50 border border-teal-100 rounded-lg p-3">
                    <div className="text-xs font-bold text-teal-700 mb-1 flex items-center gap-1">
                      <Sparkles size={10} /> ANÁLISE DA IA
                    </div>
                    <p className="text-xs text-gray-600 leading-relaxed">{m.reason}</p>
                    {m.scheduleOverlap.length > 0 && (
                      <div className="flex flex-wrap gap-1.5 mt-2">
                        <span className="text-xs text-gray-400">⏰ Horários em comum:</span>
                        {m.scheduleOverlap.map(h => (
                          <span key={h} className="text-xs bg-teal-100 text-teal-700 px-2 py-0.5 rounded-full">{h}</span>
                        ))}
                      </div>
                    )}
                  </div>
                )}

                {/* rodapé */}
                <div className="flex items-center justify-between mt-4 pt-3 border-t border-gray-100">
                  <div className="flex items-center gap-1.5 text-xs text-gray-400">
                    <Users size={12} />
                    {u.numAvaliacoes ? `${u.nota?.toFixed(1)} ★ · ${u.numAvaliacoes} avaliações` : 'Sem avaliações ainda'}
                  </div>
                  <button
                    onClick={() => solicitarConexao(u.idUsuario)}
                    disabled={jaConectado}
                    className={`flex items-center gap-1.5 text-xs font-semibold px-4 py-2 rounded-lg transition-all ${
                      jaConectado
                        ? 'bg-green-50 text-green-600 border border-green-200 cursor-default'
                        : 'bg-primary text-white hover:bg-primary/90'
                    }`}
                  >
                    {jaConectado ? (
                      <><Check size={12} /> Solicitado</>
                    ) : (
                      <><UserPlus size={12} /> Conectar</>
                    )}
                  </button>
                </div>
              </div>
            )
          })}

          {filtered.length === 0 && (
            <div className="text-center py-12 text-gray-400">
              <div className="text-4xl mb-3">🔍</div>
              <p className="text-sm">Nenhum match com esses filtros.</p>
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  )
}
