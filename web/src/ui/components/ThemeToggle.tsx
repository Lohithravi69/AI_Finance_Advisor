import { useEffect, useState } from 'react'

export default function ThemeToggle() {
  const [dark, setDark] = useState<boolean>(() => {
    const saved = localStorage.getItem('theme')
    if (saved) return saved === 'dark'
    return true
  })

  useEffect(() => {
    const root = document.documentElement
    if (dark) {
      root.classList.add('dark')
      localStorage.setItem('theme', 'dark')
    } else {
      root.classList.remove('dark')
      localStorage.setItem('theme', 'light')
    }
  }, [dark])

  return (
    <button
      aria-label="Toggle theme"
      onClick={() => setDark(v => !v)}
      className="px-3 py-2 rounded-lg bg-white/10 hover:bg-white/20 transition text-sm"
      title="Toggle theme"
    >
      {dark ? '🌙' : '☀️'}
    </button>
  )
}
