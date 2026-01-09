import { motion } from 'framer-motion'
import { PieChart, Pie, Cell, ResponsiveContainer } from 'recharts'

const data = [
  { name: 'Housing', value: 1200 },
  { name: 'Food', value: 450 },
  { name: 'Transport', value: 150 },
  { name: 'Shopping', value: 200 },
]
const COLORS = ['#2ac0ff', '#8fe2ff', '#57d3ff', '#0f628b']

export default function Dashboard() {
  return (
    <div className="grid md:grid-cols-3 gap-4">
      <motion.div className="card p-5 col-span-2" initial={{opacity:0, y:10}} animate={{opacity:1,y:0}}>
        <div className="flex items-start justify-between">
          <div>
            <div className="text-sm text-slate-400">Monthly Balance</div>
            <div className="text-4xl font-semibold tracking-tight">$2,200</div>
            <div className="mt-4 text-sm text-slate-400">Savings Progress</div>
            <div className="h-3 bg-white/10 rounded-full overflow-hidden">
              <div className="h-full bg-gradient-to-r from-brand-500 to-cyan-400" style={{width: '44%'}} />
            </div>
          </div>
          <div className="text-right">
            <div className="text-sm text-slate-400">Income</div>
            <div className="text-xl">$5,000</div>
            <div className="text-sm text-slate-400 mt-2">Expenses</div>
            <div className="text-xl">$2,800</div>
          </div>
        </div>
      </motion.div>

      <motion.div className="card p-5" initial={{opacity:0, y:10}} animate={{opacity:1,y:0}}>
        <div className="text-sm text-slate-400 mb-2">Spending Breakdown</div>
        <div className="h-48">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie data={data} dataKey="value" nameKey="name" outerRadius={70} innerRadius={40}>
                {data.map((_, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
            </PieChart>
          </ResponsiveContainer>
        </div>
      </motion.div>

      <motion.div className="card p-5 md:col-span-3" initial={{opacity:0, y:10}} animate={{opacity:1,y:0}}>
        <div className="text-sm text-slate-400 mb-2">AI Insights</div>
        <div className="grid md:grid-cols-3 gap-3">
          <div className="p-4 rounded-lg bg-white/5 border border-white/10">You overspent on dining by 18% this month.</div>
          <div className="p-4 rounded-lg bg-white/5 border border-white/10">Automate a $200 transfer to reach your goal sooner.</div>
          <div className="p-4 rounded-lg bg-white/5 border border-white/10">Your transport costs are 9% lower than last month.</div>
        </div>
      </motion.div>
    </div>
  )
}
