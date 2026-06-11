import { Link } from 'react-router-dom'
import { BookOpen, Users, MessageSquare, Star, ArrowRight, CheckCircle } from 'lucide-react'

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-white">
      {/* Nav */}
      <nav className="border-b border-gray-100 sticky top-0 bg-white z-10">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
              <BookOpen size={16} className="text-white" />
            </div>
            <span className="font-bold text-primary text-xl">SkillSwap</span>
          </div>
          <div className="flex items-center gap-3">
            <Link to="/login" className="text-gray-600 hover:text-primary font-medium text-sm transition-colors">
              Entrar
            </Link>
            <Link to="/cadastro" className="btn-primary text-sm">
              Criar conta
            </Link>
          </div>
        </div>
      </nav>

      {/* Hero */}
      <section className="bg-gradient-to-br from-primary to-secondary py-20 px-4">
        <div className="max-w-3xl mx-auto text-center">
          <div className="inline-flex items-center gap-2 bg-white/10 text-blue-100 text-sm px-4 py-1.5 rounded-full mb-6">
            <Users size={14} />
            Plataforma universitária de troca de conhecimentos
          </div>
          <h1 className="text-4xl sm:text-5xl font-bold text-white mb-5 leading-tight">
            Aprenda ensinando.<br />Cresça colaborando.
          </h1>
          <p className="text-blue-200 text-lg mb-8 max-w-xl mx-auto">
            O SkillSwap conecta estudantes que querem aprender com quem já domina o assunto,
            criando uma rede de troca de conhecimentos dentro da universidade.
          </p>
          <div className="flex flex-col sm:flex-row gap-3 justify-center">
            <Link to="/cadastro" className="btn-primary flex items-center justify-center gap-2 py-3 px-6">
              Começar agora
              <ArrowRight size={16} />
            </Link>
            <Link to="/login" className="bg-white/10 hover:bg-white/20 text-white font-medium py-3 px-6 rounded-lg transition-colors text-center">
              Já tenho conta
            </Link>
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="py-20 px-4 bg-background">
        <div className="max-w-5xl mx-auto">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-3">Como funciona</h2>
            <p className="text-gray-500 max-w-xl mx-auto">
              Três passos simples para começar a trocar conhecimentos com outros estudantes.
            </p>
          </div>
          <div className="grid md:grid-cols-3 gap-6">
            {[
              {
                step: '1',
                icon: BookOpen,
                title: 'Cadastre suas skills',
                desc: 'Registre o que você sabe ensinar e o que quer aprender.'
              },
              {
                step: '2',
                icon: Users,
                title: 'Conecte-se',
                desc: 'Encontre colegas com as habilidades que você precisa e crie sua rede.'
              },
              {
                step: '3',
                icon: MessageSquare,
                title: 'Troque conhecimentos',
                desc: 'Converse diretamente e combine sessões de aprendizado.'
              }
            ].map(({ step, icon: Icon, title, desc }) => (
              <div key={step} className="card p-6">
                <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center mb-4">
                  <Icon size={20} className="text-primary" />
                </div>
                <h3 className="font-semibold text-gray-900 mb-2">{title}</h3>
                <p className="text-gray-500 text-sm leading-relaxed">{desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Benefits */}
      <section className="py-20 px-4">
        <div className="max-w-5xl mx-auto">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-3xl font-bold text-gray-900 mb-4">
                Construa seu networking acadêmico
              </h2>
              <p className="text-gray-500 mb-6 leading-relaxed">
                O SkillSwap vai além da sala de aula. Conecte-se com estudantes de outros cursos,
                compartilhe experiências e construa relações que duram além da universidade.
              </p>
              <div className="space-y-3">
                {[
                  'Perfil completo com suas habilidades',
                  'Sistema de avaliações por pares',
                  'Mensagens diretas entre conexões',
                  'Comunidade ativa de estudantes'
                ].map((item) => (
                  <div key={item} className="flex items-center gap-3 text-gray-700 text-sm">
                    <CheckCircle size={16} className="text-success flex-shrink-0" />
                    {item}
                  </div>
                ))}
              </div>
              <Link to="/cadastro" className="btn-primary inline-flex mt-6">
                Criar minha conta
              </Link>
            </div>
            <div className="grid grid-cols-2 gap-4">
              {[
                { icon: Users, label: 'Conexões', color: 'bg-blue-50 text-primary' },
                { icon: Star, label: 'Avaliações', color: 'bg-yellow-50 text-warning' },
                { icon: MessageSquare, label: 'Mensagens', color: 'bg-green-50 text-success' },
                { icon: BookOpen, label: 'Skills', color: 'bg-purple-50 text-purple-600' }
              ].map(({ icon: Icon, label, color }) => (
                <div key={label} className="card p-5 flex flex-col items-center gap-3">
                  <div className={`w-12 h-12 rounded-xl ${color} flex items-center justify-center`}>
                    <Icon size={22} />
                  </div>
                  <span className="text-sm font-medium text-gray-700">{label}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16 px-4 bg-primary">
        <div className="max-w-2xl mx-auto text-center">
          <h2 className="text-3xl font-bold text-white mb-4">Pronto para começar?</h2>
          <p className="text-blue-200 mb-6">
            Crie sua conta gratuita e comece a trocar conhecimentos hoje.
          </p>
          <Link to="/cadastro" className="bg-white text-primary hover:bg-blue-50 font-medium py-3 px-8 rounded-lg inline-block transition-colors">
            Criar conta gratuita
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-gray-100 py-8 px-4">
        <div className="max-w-5xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <BookOpen size={16} className="text-primary" />
            <span className="font-semibold text-primary">SkillSwap</span>
          </div>
          <p className="text-gray-400 text-sm">
            Plataforma universitária de troca de conhecimentos.
          </p>
        </div>
      </footer>
    </div>
  )
}
