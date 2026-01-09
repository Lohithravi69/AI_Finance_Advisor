import { NavLink, Outlet } from 'react-router-dom'
import ThemeToggle from './components/ThemeToggle'

export default function App() {
  const link = 'px-3 py-2 rounded-lg hover:bg-white/5 transition'
  const active = 'bg-white/10'
  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-900 via-slate-950 to-black">
      <header className="sticky top-0 z-10 backdrop-blur-md border-b border-white/10 bg-slate-950/70">
        <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
          <div className="font-semibold text-lg">AI Finance Advisor</div>
          <nav className="flex items-center gap-2 text-sm">
            <NavLink to="/" end className={({isActive}) => `${link} ${isActive?active:''}`}>Dashboard</NavLink>
            <NavLink to="/expenses" className={({isActive}) => `${link} ${isActive?active:''}`}>Expenses</NavLink>
            <NavLink to="/goals" className={({isActive}) => `${link} ${isActive?active:''}`}>Goals</NavLink>
            <NavLink to="/chat" className={({isActive}) => `${link} ${isActive?active:''}`}>AI Advisor</NavLink>
            <NavLink to="/onboarding" className={({isActive}) => `${link} ${isActive?active:''}`}>Onboarding</NavLink>
            <ThemeToggle />
          </nav>
        </div>
      </header>
      <main className="max-w-6xl mx-auto px-4 py-6">
        <Outlet />
      </main>
    </div>
  )
}
