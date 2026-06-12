import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './contexts/AuthContext'
import { ToastProvider } from './components/ui/Toast'

// Pages
import LandingPage from './pages/LandingPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import ProfilePage from './pages/ProfilePage'
import SkillsPage from './pages/SkillsPage'
import FriendsPage from './pages/FriendsPage'
import MessagesPage from './pages/MessagesPage'
import ChatPage from './pages/ChatPage'
import ReviewsPage from './pages/ReviewsPage'
import SettingsPage from './pages/SettingsPage'
import DiscoverPage from './pages/DiscoverPage'
import MatchingPage from './pages/MatchingPage'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth()
  if (!isAuthenticated) return <Navigate to="/login" replace />
  return <>{children}</>
}

function PublicRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth()
  if (isAuthenticated) return <Navigate to="/dashboard" replace />
  return <>{children}</>
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
      <Route path="/cadastro" element={<PublicRoute><RegisterPage /></PublicRoute>} />
      <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
      <Route path="/perfil/:id" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
      <Route path="/skills" element={<ProtectedRoute><SkillsPage /></ProtectedRoute>} />
      <Route path="/amizades" element={<ProtectedRoute><FriendsPage /></ProtectedRoute>} />
      <Route path="/mensagens" element={<ProtectedRoute><MessagesPage /></ProtectedRoute>} />
      <Route path="/mensagens/:idAmizade" element={<ProtectedRoute><ChatPage /></ProtectedRoute>} />
      <Route path="/avaliacoes" element={<ProtectedRoute><ReviewsPage /></ProtectedRoute>} />
      <Route path="/descobrir" element={<ProtectedRoute><DiscoverPage /></ProtectedRoute>} />
      <Route path="/matching" element={<ProtectedRoute><MatchingPage /></ProtectedRoute>} />
      <Route path="/configuracoes" element={<ProtectedRoute><SettingsPage /></ProtectedRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
          <AppRoutes />
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  )
}
