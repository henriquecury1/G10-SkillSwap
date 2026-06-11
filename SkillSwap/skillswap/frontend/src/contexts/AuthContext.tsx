import React, { createContext, useContext, useState, useCallback } from 'react'
import type { Usuario } from '../types'
import { authService } from '../services/auth.service'

interface AuthContextData {
  user: Usuario | null
  isAuthenticated: boolean
  login: (email: string, senha: string) => Promise<void>
  logout: () => void
  updateUser: (user: Usuario) => void
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<Usuario | null>(() => authService.getUser())

  const login = useCallback(async (email: string, senha: string) => {
    const res = await authService.login(email, senha)
    if (!res.success || !res.data) {
      throw new Error(res.message || 'Credenciais inválidas')
    }
    authService.saveSession(res.data.token, res.data.usuario)
    setUser(res.data.usuario)
  }, [])

  const logout = useCallback(() => {
    authService.logout()
    setUser(null)
  }, [])

  const updateUser = useCallback((updated: Usuario) => {
    localStorage.setItem('skillswap_user', JSON.stringify(updated))
    setUser(updated)
  }, [])

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        login,
        logout,
        updateUser
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
