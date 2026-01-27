import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'

type Goal = {
  id: number
  name: string
  targetAmount: number
  currentAmount: number
  status: string
}

export default function Goals() {
  const [goals, setGoals] = useState<Goal[]>([])
  const [celebrate, setCelebrate] = useState<string | null>(null)

  useEffect(() => {
    fetch('/api/finance/api/goals')
      .then(r => r.json())
      .then(goals => setGoals(goals.map((g: any) => ({ ...g, name: g.title }))))
      .catch(() => setGoals([]))
  }, [])

  const updateGoal = (id: number, currentAmount: number) => {
    const goal = goals.find(g => g.id === id)
    if (!goal) return

    const newAmount = Math.max(0, currentAmount)
    const progress = (newAmount / goal.targetAmount) * 100

    fetch(`/api/finance/api/goals/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...goal, currentAmount: newAmount })
    })
      .then(r => r.json())
      .then(updated => {
        setGoals(gs => gs.map(g => g.id === id ? updated : g))
        if (progress >= 100 && goal.status === 'ACTIVE') {
          setCelebrate(goal.name)
          setTimeout(() => setCelebrate(null), 3000)
        }
      })
  }

  return (
    <div className="grid md:grid-cols-3 gap-4">
      <div className="card p-5 md:col-span-2">
        <div className="text-lg font-semibold mb-4">Your Goals</div>
        {goals.length === 0 ? (
          <div className="text-slate-400">No goals yet. Create one to get started!</div>
        ) : (
          <div className="space-y-4">
            {goals.map(goal => {
              const progress = (goal.currentAmount / goal.targetAmount) * 100
              return (
                <div key={goal.id} className="p-4 bg-white/5 border border-white/10 rounded-lg">
                  <div className="flex justify-between items-start mb-2">
                    <div>
                      <div className="font-medium">{goal.name}</div>
                      <div className="text-sm text-slate-400">
                        ${goal.currentAmount.toFixed(2)} of ${goal.targetAmount.toFixed(2)} target
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="text-sm text-slate-400">{progress.toFixed(1)}%</div>
                    </div>
                  </div>
                  <div className="h-3 bg-white/10 rounded-full overflow-hidden mb-3">
                    <div className="h-full bg-gradient-to-r from-brand-500 to-cyan-400" style={{width:`${Math.min(100, progress)}%`}} />
                  </div>
                  <div className="flex gap-2 text-sm">
                    <button className="px-3 py-1 bg-white/10 rounded" onClick={()=>updateGoal(goal.id, goal.currentAmount + 100)}>+100</button>
                    <button className="px-3 py-1 bg-white/10 rounded" onClick={()=>updateGoal(goal.id, goal.currentAmount + 500)}>+500</button>
                    <button className="px-3 py-1 bg-white/10 rounded" onClick={()=>updateGoal(goal.id, goal.currentAmount - 100)}>-100</button>
                  </div>
                </div>
              )
            })}
          </div>
        )}
        <AnimatePresence>
          {celebrate && (
            <motion.div initial={{opacity:0, y:10}} animate={{opacity:1,y:0}} exit={{opacity:0}} className="mt-4 p-4 rounded-lg bg-emerald-500/20 border border-emerald-500/30">
              🎉 Goal "{celebrate}" completed! Amazing work!
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      <div className="card p-5">
        <div className="text-lg font-semibold mb-2">Upcoming Milestones</div>
        <ul className="space-y-2 text-sm">
          {goals.filter(g => g.status === 'ACTIVE').map(goal => {
            const remaining = goal.targetAmount - goal.currentAmount
            return (
              <li key={goal.id} className="bg-white/5 border border-white/10 rounded p-3">
                Save ${remaining.toFixed(2)} more for {goal.name}
              </li>
            )
          })}
          {goals.length === 0 && (
            <>
              <li className="bg-white/5 border border-white/10 rounded p-3">Create your first savings goal</li>
              <li className="bg-white/5 border border-white/10 rounded p-3">Set a monthly savings target</li>
              <li className="bg-white/5 border border-white/10 rounded p-3">Track progress with milestones</li>
            </>
          )}
        </ul>
      </div>
    </div>
  )
}
