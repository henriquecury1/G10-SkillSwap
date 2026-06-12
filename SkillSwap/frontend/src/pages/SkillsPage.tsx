import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Zap, Plus, Trash2, Loader2, Edit2, Check, X } from 'lucide-react'
import { useAuth } from '../contexts/AuthContext'
import AppLayout from '../components/layout/AppLayout'
import { skillService } from '../services/skill.service'
import { qualificationService } from '../services/qualification.service'

export default function SkillsPage() {
  const { user } = useAuth()
  const qc = useQueryClient()
  const [newSkillName, setNewSkillName] = useState('')
  const [editingId, setEditingId] = useState<number | null>(null)
  const [editingName, setEditingName] = useState('')
  const [addingToProfile, setAddingToProfile] = useState(false)

  const { data: allSkillsData, isLoading: loadingAll } = useQuery({
    queryKey: ['skills'],
    queryFn: skillService.listarTodas
  })

  const { data: mySkillsData } = useQuery({
    queryKey: ['skills-usuario', user?.idUsuario],
    queryFn: () => qualificationService.listarSkillsDoUsuario(user!.idUsuario),
    enabled: !!user
  })

  const createMutation = useMutation({
    mutationFn: skillService.cadastrar,
    onSuccess: () => {
      setNewSkillName('')
      qc.invalidateQueries({ queryKey: ['skills'] })
    }
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, name }: { id: number; name: string }) => skillService.atualizar(id, name),
    onSuccess: () => {
      setEditingId(null)
      qc.invalidateQueries({ queryKey: ['skills'] })
    }
  })

  const deleteMutation = useMutation({
    mutationFn: skillService.deletar,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['skills'] })
  })

  const addToProfileMutation = useMutation({
    mutationFn: (idSkill: number) => qualificationService.adicionarSkill(user!.idUsuario, idSkill),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['skills-usuario', user?.idUsuario] })
  })

  const removeFromProfileMutation = useMutation({
    mutationFn: (idSkill: number) => qualificationService.removerSkill(user!.idUsuario, idSkill),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['skills-usuario', user?.idUsuario] })
  })

  const allSkills = allSkillsData?.data ?? []
  const mySkillIds = new Set((mySkillsData?.data ?? []).map((s) => s.idSkill))

  return (
    <AppLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Skills</h1>
          <p className="text-gray-500 text-sm mt-1">Gerencie as habilidades disponíveis e adicione ao seu perfil.</p>
        </div>

        {/* Adicionar nova skill */}
        <div className="card p-5">
          <h2 className="font-semibold text-gray-900 mb-3">Cadastrar nova skill</h2>
          <div className="flex gap-2">
            <input
              value={newSkillName}
              onChange={(e) => setNewSkillName(e.target.value)}
              className="input-field"
              placeholder="Nome da skill (ex: Python, Design, Inglês...)"
              onKeyDown={(e) => e.key === 'Enter' && newSkillName.trim() && createMutation.mutate(newSkillName.trim())}
            />
            <button
              onClick={() => newSkillName.trim() && createMutation.mutate(newSkillName.trim())}
              disabled={!newSkillName.trim() || createMutation.isPending}
              className="btn-primary flex items-center gap-1.5 whitespace-nowrap"
            >
              {createMutation.isPending ? <Loader2 size={14} className="animate-spin" /> : <Plus size={14} />}
              Adicionar
            </button>
          </div>
        </div>

        {/* Lista de skills */}
        <div className="card p-5">
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-semibold text-gray-900">Todas as skills</h2>
            <button
              onClick={() => setAddingToProfile(!addingToProfile)}
              className={`text-sm font-medium ${addingToProfile ? 'text-error' : 'text-primary'}`}
            >
              {addingToProfile ? 'Concluir' : 'Gerenciar meu perfil'}
            </button>
          </div>

          {loadingAll ? (
            <div className="flex justify-center py-8">
              <Loader2 size={20} className="animate-spin text-primary" />
            </div>
          ) : allSkills.length === 0 ? (
            <div className="text-center py-8">
              <Zap size={32} className="text-gray-200 mx-auto mb-2" />
              <p className="text-gray-400 text-sm">Nenhuma skill cadastrada ainda.</p>
            </div>
          ) : (
            <div className="space-y-2">
              {allSkills.map((skill) => {
                const inProfile = mySkillIds.has(skill.idSkill)
                return (
                  <div
                    key={skill.idSkill}
                    className="flex items-center gap-3 py-2.5 px-3 rounded-lg hover:bg-gray-50 group"
                  >
                    <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center flex-shrink-0">
                      <Zap size={14} className="text-primary" />
                    </div>

                    {editingId === skill.idSkill ? (
                      <input
                        value={editingName}
                        onChange={(e) => setEditingName(e.target.value)}
                        className="input-field flex-1 py-1 text-sm"
                        autoFocus
                      />
                    ) : (
                      <span className="flex-1 text-gray-800 text-sm font-medium">{skill.name}</span>
                    )}

                    <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                      {editingId === skill.idSkill ? (
                        <>
                          <button
                            onClick={() => updateMutation.mutate({ id: skill.idSkill, name: editingName })}
                            className="p-1.5 rounded hover:bg-green-100 text-success"
                          >
                            <Check size={14} />
                          </button>
                          <button
                            onClick={() => setEditingId(null)}
                            className="p-1.5 rounded hover:bg-red-100 text-error"
                          >
                            <X size={14} />
                          </button>
                        </>
                      ) : (
                        <>
                          <button
                            onClick={() => { setEditingId(skill.idSkill); setEditingName(skill.name) }}
                            className="p-1.5 rounded hover:bg-gray-200 text-gray-500"
                          >
                            <Edit2 size={13} />
                          </button>
                          <button
                            onClick={() => deleteMutation.mutate(skill.idSkill)}
                            disabled={deleteMutation.isPending}
                            className="p-1.5 rounded hover:bg-red-100 text-error"
                          >
                            <Trash2 size={13} />
                          </button>
                        </>
                      )}
                    </div>

                    {addingToProfile && (
                      <button
                        onClick={() => inProfile
                          ? removeFromProfileMutation.mutate(skill.idSkill)
                          : addToProfileMutation.mutate(skill.idSkill)
                        }
                        className={`text-xs px-3 py-1 rounded-full font-medium transition-colors ${
                          inProfile
                            ? 'bg-primary/10 text-primary hover:bg-red-100 hover:text-error'
                            : 'bg-gray-100 text-gray-600 hover:bg-primary/10 hover:text-primary'
                        }`}
                      >
                        {inProfile ? 'Remover' : 'Adicionar ao perfil'}
                      </button>
                    )}

                    {!addingToProfile && inProfile && (
                      <span className="badge bg-primary/10 text-primary text-xs">no perfil</span>
                    )}
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
