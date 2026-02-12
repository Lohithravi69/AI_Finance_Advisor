import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { PieChart, Pie, Cell, ResponsiveContainer } from 'recharts'
import BudgetAlerts, { generateBudgetAlerts } from '../components/BudgetAlerts'

const COLORS = ['#2ac0ff', '#8fe2ff', '#57d3ff', '#0f628b', '#ff6b6b', '#4ecdc4', '#ffe66d', '#dda0dd']

interface DashboardData {
  balance: number
  income: number
  expenses: number
  savingsProgress: number
  spendingBreakdown: Array<{ name: string; value: number }>
  insights: Array<{ type: string; message: string }>
  cashFlowForecast?: Array<{ date: string; predicted_balance: number; risk_level: string }>
  riskScore?: number
  goalPrediction?: { predicted_completion_date: string; confidence_score: number; recommendations: string[] }
}

export default function Dashboard() {
  const [data, setData] = useState<DashboardData>({
    balance: 0,
    income: 0,
    expenses: 0,
    savingsProgress: 0,
    spendingBreakdown: [],
    insights: []
  })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchDashboardData()
  }, [])

  const fetchDashboardData = async () => {
    try {
      setLoading(true)
      setError(null)

      // Fetch summary data from backend API
      const summaryResponse = await fetch('/api/transactions/summary')

      if (!summaryResponse.ok) {
        throw new Error(`Failed to fetch summary data: ${summaryResponse.status}`)
      }

      const summary = await summaryResponse.json()

      // Fetch recent transactions for spending breakdown
      const transactionsResponse = await fetch('/api/transactions?limit=50')

      let spendingBreakdown: Array<{ name: string; value: number }> = []
      if (transactionsResponse.ok) {
        const transactions = await transactionsResponse.json()

        // Calculate spending by category
        const categoryTotals: Record<string, number> = {}
        transactions.forEach((tx: any) => {
          if (tx.type === 'EXPENSE' && tx.category) {
            const category = tx.category.toLowerCase()
            categoryTotals[category] = (categoryTotals[category] || 0) + (tx.amount || 0)
          }
        })

        spendingBreakdown = Object.entries(categoryTotals)
          .map(([name, value]) => ({ name: name.charAt(0).toUpperCase() + name.slice(1), value }))
          .sort((a, b) => b.value - a.value)
          .slice(0, 8) // Top 8 categories
      }

      // Fetch AI insights
      let insights: Array<{ type: string; message: string }> = []
      try {
        const insightsResponse = await fetch('/api/insights')

        if (insightsResponse.ok) {
          insights = await insightsResponse.json()
        }
      } catch (insightsError) {
        console.warn('AI insights service unavailable, using fallback insights')
        // Fallback insights if AI service is unavailable
        insights = [
          { type: 'info', message: 'Track your expenses regularly to maintain financial health.' },
          { type: 'tip', message: 'Consider setting up automatic savings transfers.' },
          { type: 'goal', message: 'Review your goals progress monthly.' }
        ]
      }

      // Calculate balance and savings progress
      const income = summary.totalIncome || 0
      const expenses = summary.totalExpenses || 0
      const balance = income - expenses
      const savingsProgress = income > 0 ? Math.min((balance / income) * 100, 100) : 0

      setData({
        balance,
        income,
        expenses,
        savingsProgress,
        spendingBreakdown,
        insights
      })
    } catch (err) {
      console.error('Dashboard data fetch error:', err)
      setError(err instanceof Error ? err.message : 'Failed to load dashboard data')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="grid md:grid-cols-3 gap-4">
        <motion.div className="card p-5 col-span-2" initial={{opacity:0, y:10}} animate={{opacity:1,y:0}}>
          <div className="animate-pulse">
            <div className="h-8 bg-white/10 rounded mb-4"></div>
            <div className="h-12 bg-white/10 rounded mb-4"></div>
            <div className="h-3 bg-white/10 rounded"></div>
          </div>
        </motion.div>
        <motion.div className="card p-5" initial={{opacity:0, y:10}} animate={{opacity:1,y:0}}>
          <div className="animate-pulse h-48 bg-white/10 rounded"></div>
        </motion.div>
        <motion.div className="card p-5 md:col-span-3" initial={{opacity:0, y:10}} animate={{opacity:1,y:0}}>
          <div className="animate-pulse h-32 bg-white/10 rounded"></div>
        </motion.div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="grid md:grid-cols-3 gap-4">
        <motion.div className="card p-5 md:col-span-3" initial={{opacity:0, y:10}} animate={{opacity:1,y:0}}>
          <div className="text-center py-8">
            <div className="text-red-400 mb-2">⚠️ Error loading dashboard</div>
            <div className="text-sm text-slate-400">{error}</div>
            <button
              onClick={fetchDashboardData}
              className="mt-4 px-4 py-2 bg-brand-500 hover:bg-brand-600 text-white rounded-lg transition"
            >
              Try Again
            </button>
          </div>
        </motion.div>
      </div>
    )
  }

  const budgetAlerts = generateBudgetAlerts(data)

  return (
    <div className="grid md:grid-cols-3 gap-4">
      <motion.div className="card p-5 col-span-2" initial={{opacity:0, y:10}} animate={{opacity:1,y:0}}>
        <div className="flex items-start justify-between">
          <div>
            <div className="text-sm text-slate-400">Monthly Balance</div>
            <div className={`text-4xl font-semibold tracking-tight ${data.balance >= 0 ? 'text-green-400' : 'text-red-400'}`}>
              ${data.balance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </div>
            <div className="mt-4 text-sm text-slate-400">Savings Progress</div>
            <div className="h-3 bg-white/10 rounded-full overflow-hidden">
              <div
                className="h-full bg-gradient-to-r from-brand-500 to-cyan-400 transition-all duration-500"
                style={{width: `${Math.max(data.savingsProgress, 5)}%`}}
              />
            </div>
            <div className="text-xs text-slate-400 mt-1">{data.savingsProgress.toFixed(1)}% saved</div>
          </div>
          <div className="text-right">
            <div className="text-sm text-slate-400">Income</div>
            <div className="text-xl text-green-400">${data.income.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</div>
            <div className="text-sm text-slate-400 mt-2">Expenses</div>
            <div className="text-xl text-red-400">${data.expenses.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</div>
          </div>
        </div>
      </motion.div>

      <motion.div className="card p-5" initial={{opacity:0, y:10}} animate={{opacity:1,y:0}}>
        <div className="text-sm text-slate-400 mb-2">Spending Breakdown</div>
        <div className="h-48">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie data={data.spendingBreakdown} dataKey="value" nameKey="name" outerRadius={70} innerRadius={40}>
                {data.spendingBreakdown.map((_, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
            </PieChart>
          </ResponsiveContainer>
        </div>
      </motion.div>

      <motion.div className="card p-5 md:col-span-3" initial={{opacity:0, y:10}} animate={{opacity:1,y:0}}>
        <BudgetAlerts alerts={budgetAlerts} />
      </motion.div>

      <motion.div className="card p-5 md:col-span-3" initial={{opacity:0, y:10}} animate={{opacity:1,y:0}}>
        <div className="text-sm text-slate-400 mb-2">AI Insights</div>
        <div className="grid md:grid-cols-3 gap-3">
          {data.insights.slice(0, 3).map((insight, index) => (
            <div key={index} className="p-4 rounded-lg bg-white/5 border border-white/10">
              {insight.message}
            </div>
          ))}
        </div>
      </motion.div>
    </div>
  )
}
