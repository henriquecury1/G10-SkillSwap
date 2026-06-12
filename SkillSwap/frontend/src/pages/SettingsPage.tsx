import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { Loader2, Check } from 'lucide-react'
import { useAuth } from '../contexts/AuthContext'
import AppLayout from '../components/layout/AppLayout'
import { userService } from '../services/user.service'

const profileSchema = z.object({
  nome: z.string().min(2, 'Nome deve ter ao menos 2 caracteres'),
  email: z.string().email('E-mail inválido'),
  bio: z.string().optional()
})

const passwordSchema = z.object({
  senhaAtual: z.string().min(1, 'Informe a senha atual'),
  novaSenha: z.string().min(6, 'Nova senha deve ter ao menos 6 caracteres'),
  confirmar: z.string()
}).refine(d => d.novaSenha === d.confirmar, {
  message: 'As senhas não coincidem',
  path: ['confirmar']
})

type ProfileForm = z.infer<typeof profileSchema>
type PasswordForm = z.infer<typeof passwordSchema>

export default function SettingsPage() {
  const { user, updateUser } = useAuth()
  const [profileMsg, setProfileMsg] = useState('')
  const [passwordMsg, setPasswordMsg] = useState('')

  const profileForm = useForm<ProfileForm>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      nome: user?.nome ?? '',
      email: user?.email ?? '',
      bio: user?.bio ?? ''
    }
  })

  const passwordForm = useForm<PasswordForm>({
    resolver: zodResolver(passwordSchema)
  })

  async function onSaveProfile(data: ProfileForm) {
    setProfileMsg('')
    try {
      const res = await userService.atualizarPerfil(user!.idUsuario, data)
      if (!res.success) throw new Error(res.message)
      updateUser({ ...user!, ...data })
      setProfileMsg('Perfil atualizado com sucesso!')
    } catch (e: unknown) {
      setProfileMsg(e instanceof Error ? e.message : 'Erro ao atualizar perfil')
    }
  }

  async function onChangePassword(data: PasswordForm) {
    setPasswordMsg('')
    try {
      const res = await userService.atualizarSenha(user!.idUsuario, data.senhaAtual, data.novaSenha)
      if (!res.success) throw new Error(res.message)
      setPasswordMsg('Senha alterada com sucesso!')
      passwordForm.reset()
    } catch (e: unknown) {
      setPasswordMsg(e instanceof Error ? e.message : 'Erro ao alterar senha')
    }
  }

  return (
    <AppLayout>
      <div className="space-y-6 max-w-xl">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Configurações</h1>
          <p className="text-gray-500 text-sm mt-1">Gerencie seus dados pessoais e preferências.</p>
        </div>

        {/* Dados pessoais */}
        <div className="card p-6">
          <h2 className="font-semibold text-gray-900 mb-4">Dados pessoais</h2>
          <form onSubmit={profileForm.handleSubmit(onSaveProfile)} className="space-y-4">
            {profileMsg && (
              <div className={`flex items-center gap-2 text-sm px-3 py-2 rounded-lg ${
                profileMsg.includes('sucesso')
                  ? 'bg-green-50 border border-green-200 text-success'
                  : 'bg-red-50 border border-red-200 text-error'
              }`}>
                {profileMsg.includes('sucesso') && <Check size={14} />}
                {profileMsg}
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nome</label>
              <input {...profileForm.register('nome')} className="input-field" />
              {profileForm.formState.errors.nome && (
                <p className="text-error text-xs mt-1">{profileForm.formState.errors.nome.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">E-mail</label>
              <input {...profileForm.register('email')} type="email" className="input-field" />
              {profileForm.formState.errors.email && (
                <p className="text-error text-xs mt-1">{profileForm.formState.errors.email.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Bio</label>
              <textarea
                {...profileForm.register('bio')}
                className="input-field resize-none"
                rows={3}
                placeholder="Conte um pouco sobre você..."
              />
            </div>

            <button
              type="submit"
              disabled={profileForm.formState.isSubmitting}
              className="btn-primary flex items-center gap-2"
            >
              {profileForm.formState.isSubmitting && <Loader2 size={14} className="animate-spin" />}
              Salvar alterações
            </button>
          </form>
        </div>

        {/* Alterar senha */}
        <div className="card p-6">
          <h2 className="font-semibold text-gray-900 mb-4">Alterar senha</h2>
          <form onSubmit={passwordForm.handleSubmit(onChangePassword)} className="space-y-4">
            {passwordMsg && (
              <div className={`flex items-center gap-2 text-sm px-3 py-2 rounded-lg ${
                passwordMsg.includes('sucesso')
                  ? 'bg-green-50 border border-green-200 text-success'
                  : 'bg-red-50 border border-red-200 text-error'
              }`}>
                {passwordMsg.includes('sucesso') && <Check size={14} />}
                {passwordMsg}
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Senha atual</label>
              <input {...passwordForm.register('senhaAtual')} type="password" className="input-field" placeholder="••••••••" />
              {passwordForm.formState.errors.senhaAtual && (
                <p className="text-error text-xs mt-1">{passwordForm.formState.errors.senhaAtual.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nova senha</label>
              <input {...passwordForm.register('novaSenha')} type="password" className="input-field" placeholder="Mínimo 6 caracteres" />
              {passwordForm.formState.errors.novaSenha && (
                <p className="text-error text-xs mt-1">{passwordForm.formState.errors.novaSenha.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Confirmar nova senha</label>
              <input {...passwordForm.register('confirmar')} type="password" className="input-field" placeholder="••••••••" />
              {passwordForm.formState.errors.confirmar && (
                <p className="text-error text-xs mt-1">{passwordForm.formState.errors.confirmar.message}</p>
              )}
            </div>

            <button
              type="submit"
              disabled={passwordForm.formState.isSubmitting}
              className="btn-primary flex items-center gap-2"
            >
              {passwordForm.formState.isSubmitting && <Loader2 size={14} className="animate-spin" />}
              Alterar senha
            </button>
          </form>
        </div>
      </div>
    </AppLayout>
  )
}
