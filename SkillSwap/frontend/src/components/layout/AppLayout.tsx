import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import {
  LayoutDashboard,
  Zap,
  Users,
  MessageSquare,
  Star,
  Settings,
  LogOut,
  Menu,
  X,
  BookOpen,
  Compass,
  Sparkles
} from 'lucide-react'
import { useAuth } from '../../contexts/AuthContext'

const navItems = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/skills', icon: Zap, label: 'Skills' },
  { to: '/descobrir', icon: Compass, label: 'Descobrir' },
  { to: '/amizades', icon: Users, label: 'Conexões' },
  { to: '/mensagens', icon: MessageSquare, label: 'Mensagens' },
  { to: '/avaliacoes', icon: Star, label: 'Avaliações' },
  { to: '/matching', icon: Sparkles, label: 'Matching IA', highlight: true },
  { to: '/configuracoes', icon: Settings, label: 'Configurações' },
]

export default function AppLayout({ children }: { children: React.ReactNode }) {
  const location = useLocation()
  const navigate = useNavigate()
  const { user, logout } = useAuth()
  const [mobileOpen, setMobileOpen] = useState(false)

  function handleLogout() {
    logout()
    navigate('/')
  }

  const initials = user?.nome
    ? user.nome.split(' ').map((n) => n[0]).slice(0, 2).join('').toUpperCase()
    : '??'

  return (
    <div className="min-h-screen bg-background flex">
      {/* Sidebar desktop */}
      <aside className="hidden md:flex flex-col w-64 bg-primary min-h-screen fixed left-0 top-0 z-30">
        <div className="px-6 py-5 border-b border-primary-700/50">
          <Link to="/dashboard" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-secondary rounded-lg flex items-center justify-center">
              <BookOpen size={16} className="text-white" />
            </div>
            <span className="text-white font-bold text-lg">SkillSwap</span>
          </Link>
        </div>

        <nav className="flex-1 px-3 py-4 space-y-1">
          {navItems.map(({ to, icon: Icon, label, highlight }) => {
            const active = location.pathname === to || location.pathname.startsWith(to + '/')
            return (
              <Link
                key={to}
                to={to}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  active
                    ? 'bg-white/15 text-white'
                    : highlight
                    ? 'text-teal-300 hover:bg-white/10 hover:text-white'
                    : 'text-blue-200 hover:bg-white/10 hover:text-white'
                }`}
              >
                <Icon size={18} />
                {label}
                {highlight && !active && (
                  <span className="ml-auto text-[10px] bg-teal-500/30 text-teal-200 px-1.5 py-0.5 rounded-full font-semibold">
                    IA
                  </span>
                )}
              </Link>
            )
          })}
        </nav>

        <div className="p-3 border-t border-primary-700/50">
          <Link
            to={`/perfil/${user?.idUsuario}`}
            className="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-white/10 transition-colors mb-1"
          >
            <div className="w-8 h-8 rounded-full bg-secondary flex items-center justify-center text-white text-xs font-bold">
              {initials}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-white text-sm font-medium truncate">{user?.nome}</p>
              <p className="text-blue-300 text-xs truncate">{user?.email}</p>
            </div>
          </Link>
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-blue-200 hover:bg-white/10 hover:text-white transition-colors text-sm"
          >
            <LogOut size={16} />
            Sair
          </button>
        </div>
      </aside>

      {/* Mobile header */}
      <div className="md:hidden fixed top-0 left-0 right-0 z-40 bg-primary px-4 py-3 flex items-center justify-between">
        <Link to="/dashboard" className="flex items-center gap-2">
          <BookOpen size={20} className="text-white" />
          <span className="text-white font-bold">SkillSwap</span>
        </Link>
        <div className="flex items-center gap-2">
          <Link to="/matching" className="p-1 text-teal-300">
            <Sparkles size={20} />
          </Link>
          <button onClick={() => setMobileOpen(!mobileOpen)} className="text-white p-1">
            {mobileOpen ? <X size={22} /> : <Menu size={22} />}
          </button>
        </div>
      </div>

      {/* Mobile nav */}
      {mobileOpen && (
        <div className="md:hidden fixed inset-0 z-30 bg-primary pt-14">
          <nav className="px-3 py-4 space-y-1">
            {navItems.map(({ to, icon: Icon, label, highlight }) => (
              <Link
                key={to}
                to={to}
                onClick={() => setMobileOpen(false)}
                className={`flex items-center gap-3 px-3 py-3 rounded-lg text-sm font-medium ${
                  highlight ? 'text-teal-300' : 'text-blue-200'
                } hover:bg-white/10 hover:text-white`}
              >
                <Icon size={18} />
                {label}
                {highlight && (
                  <span className="ml-auto text-[10px] bg-teal-500/30 text-teal-200 px-1.5 py-0.5 rounded-full">IA</span>
                )}
              </Link>
            ))}
            <button
              onClick={handleLogout}
              className="w-full flex items-center gap-3 px-3 py-3 rounded-lg text-blue-200 hover:bg-white/10 text-sm font-medium"
            >
              <LogOut size={18} />
              Sair
            </button>
          </nav>
        </div>
      )}

      {/* Main content */}
      <main className="flex-1 md:ml-64 pt-14 md:pt-0 min-h-screen">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 py-6">
          {children}
        </div>
      </main>
    </div>
  )
}
