import { useState } from 'react'

const suggestions = [
  'What budget should I use?',
  'How much can I save monthly?',
  'Why did dining spike this month?'
]

export default function Chat() {
  const [message, setMessage] = useState('')
  const [history, setHistory] = useState<Array<{role:'user'|'ai', text:string}>>([])

  const send = async (override?: string) => {
    const text = (override ?? message).trim()
    if (!text) return
    setMessage('')
    setHistory(h => [...h, { role: 'user', text }])
    try {
      const res = await fetch('/api/ai/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: text })
      })
      const data = await res.json()
      setHistory(h => [...h, { role: 'ai', text: data.reply || '...'}])
    } catch {
      setHistory(h => [...h, { role: 'ai', text: 'Connection issue. Is the AI service running on 8001?' }])
    }
  }

  return (
    <div className="max-w-2xl mx-auto grid gap-4">
      <div className="card p-5">
        <div className="text-lg font-semibold mb-2">AI Advisor</div>
        <div className="text-slate-400 mb-4">Ask about budgeting, savings, and expenses.</div>

        <div className="flex flex-wrap gap-2 mb-3 text-sm">
          {suggestions.map(s => (
            <button key={s} onClick={()=>send(s)} className="px-3 py-1 rounded-full bg-white/10 hover:bg-white/20 border border-white/10">{s}</button>
          ))}
        </div>

        <div className="space-y-2 max-h-[50vh] overflow-y-auto">
          {history.map((m, i) => (
            <div key={i} className={`flex ${m.role==='user' ? 'justify-end' : 'justify-start'}`}>
              <div className={m.role==='user' ? 'bubble-user' : 'bubble-ai'}>
                {m.text}
              </div>
            </div>
          ))}
        </div>

        <div className="mt-4 flex gap-2">
          <input value={message} onChange={e=>setMessage(e.target.value)} placeholder="e.g., How do I budget?" className="flex-1 px-3 py-2 rounded bg-white/10 border border-white/10" />
          <button onClick={()=>send()} className="px-4 py-2 bg-brand-500 rounded">Send</button>
        </div>
      </div>
    </div>
  )
}
