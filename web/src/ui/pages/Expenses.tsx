import { useEffect, useMemo, useState } from 'react'
import { motion } from 'framer-motion'
import AddTransactionForm from '../components/AddTransactionForm'

type Tx = { id: string; description: string; amount: number; date: string; merchant: string }

const category = (t: Tx) => {
  const s = `${t.description} ${t.merchant}`.toLowerCase()
  if (/(kroger|aldi|whole|grocery|supermarket)/.test(s)) return 'grocery'
  if (/(rent|landlord)/.test(s)) return 'housing'
  if (/(starbucks|cafe|restaurant|dining|bar)/.test(s)) return 'dining'
  if (/(uber|lyft|gas|metro|bus|train)/.test(s)) return 'transport'
  return 'other'
}

const badge: Record<string,string> = {
  grocery: 'bg-emerald-500/20 text-emerald-200 border-emerald-500/30',
  housing: 'bg-indigo-500/20 text-indigo-200 border-indigo-500/30',
  dining: 'bg-rose-500/20 text-rose-200 border-rose-500/30',
  transport: 'bg-amber-500/20 text-amber-200 border-amber-500/30',
  other: 'bg-slate-500/20 text-slate-200 border-slate-500/30'
}

export default function Expenses() {
  const [items, setItems] = useState<Tx[]>([])

  useEffect(() => {
    fetch('/api/finance/api/transactions?limit=10')
      .then(r => r.json())
      .then(setItems)
      .catch(() => setItems([]))
  }, [])

  const grouped = useMemo(() => {
    const map: Record<string, Tx[]> = {}
    for (const tx of items) {
      const d = new Date(tx.date).toDateString()
      map[d] = map[d] || []
      map[d].push(tx)
    }
    return Object.entries(map)
  }, [items])

  const refreshTransactions = () => {
    fetch('/api/finance/api/transactions?limit=10')
      .then(r => r.json())
      .then(setItems)
      .catch(() => setItems([]))
  }

  return (
    <>
      <div className="grid md:grid-cols-3 gap-4">
        <div className="md:col-span-2 card p-5">
          <div className="flex items-center justify-between mb-3">
            <div className="text-lg font-semibold">Timeline</div>
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => setShowAddForm(true)}
              className="px-4 py-2 bg-brand-500 hover:bg-brand-600 text-white rounded-lg transition"
            >
              Add Transaction
            </motion.button>
          </div>
        <div className="timeline">
          {grouped.map(([date, list]) => (
            <div key={date} className="timeline-item">
              <div className="text-sm text-slate-400 mb-2">{date}</div>
              <div className="space-y-2">
                {list.map(tx => (
                  <div key={tx.id} className="flex items-center justify-between bg-white/5 border border-white/10 rounded-lg p-3">
                    <div>
                      <div className="font-medium">{tx.description}</div>
                      <div className="text-xs text-slate-400">{tx.merchant}</div>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className={`text-xs px-2 py-1 rounded border ${badge[category(tx)]}`}>{category(tx)}</span>
                      <div className="text-right font-medium">${tx.amount.toFixed(2)}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="card p-5">
        <div className="text-lg font-semibold mb-3">Filters</div>
        <div className="grid gap-2 text-sm">
          <button className="px-3 py-2 bg-white/10 rounded">This month</button>
          <button className="px-3 py-2 bg-white/10 rounded">Dining</button>
          <button className="px-3 py-2 bg-white/10 rounded">Transport</button>
          <button className="px-3 py-2 bg-white/10 rounded">Shopping</button>
        </div>
      </div>
    </div>

    {showAddForm && (
      <AddTransactionForm
        onClose={() => setShowAddForm(false)}
        onSuccess={refreshTransactions}
      />
    )}
    </>
  )
}
