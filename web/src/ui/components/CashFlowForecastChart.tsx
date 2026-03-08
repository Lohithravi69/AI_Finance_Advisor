import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Area, AreaChart, ReferenceLine } from 'recharts'
import { useAuthStore } from '../../stores/authStore'

interface CashFlowPrediction {
  date: string
  predicted_balance: number
  confidence: number
  risk_level: string
  factors: string[]
}

interface ForecastData {
  predictions: CashFlowPrediction[]
  confidence: number
  metadata?: {
    model_accuracy: number
    prediction_horizon: number
  }
}

const RISK_COLORS = {
  low: '#22c55e',
  medium: '#eab308',
  high: '#f97316',
  critical: '#ef4444'
}

type TimePeriod = '3m' | '6m' | '12m'

export default function CashFlowForecastChart() {
  const { token } = useAuthStore()
  const [data, setData] = useState<ForecastData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [timePeriod, setTimePeriod] = useState<TimePeriod>('6m')
  const [isRefreshing, setIsRefreshing] = useState(false)
  const [hoveredPoint, setHoveredPoint] = useState<{date: string, balance: number, risk: string} | null>(null)

  useEffect(() => {
    fetchForecast()
  }, [token, timePeriod])

  const fetchForecast = async () => {
    if (!token) {
      setLoading(false)
      return
    }

    const monthsNum = timePeriod === '3m' ? 3 : timePeriod === '6m' ? 6 : 12
    
    try {
      setLoading(true)
      setError(null)
      
      const response = await fetch(`/api/insights/cashflow-forecast?months=${monthsNum}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (!response.ok) {
        throw new Error('Failed to fetch cash flow forecast')
      }

      const result = await response.json()
      setData(result)
    } catch (err) {
      console.error('Cash flow forecast error:', err)
      setError(err instanceof Error ? err.message : 'Failed to load forecast')
      setData(generateMockForecast(monthsNum))
    } finally {
      setLoading(false)
      setIsRefreshing(false)
    }
  }

  const handleRefresh = () => {
    setIsRefreshing(true)
    fetchForecast()
  }

  const generateMockForecast = (months: number): ForecastData => {
    const predictions: CashFlowPrediction[] = []
    const baseBalance = 5000
    const today = new Date()
    
    for (let i = 1; i <= months; i++) {
      const futureDate = new Date(today)
      futureDate.setMonth(today.getMonth() + i)
      
      predictions.push({
        date: futureDate.toISOString().split('T')[0],
        predicted_balance: baseBalance + (Math.random() * 3000 - 1000) + (i * 100),
        confidence: Math.max(0.5, 0.85 - (i * 0.05)),
        risk_level: i <= 2 ? 'low' : i <= 4 ? 'medium' : i <= 6 ? 'high' : 'critical',
        factors: ['Income stable', 'Regular expenses', 'Seasonal variation']
      })
    }

    return {
      predictions,
      confidence: 0.75,
      metadata: {
        model_accuracy: 0.85,
        prediction_horizon: months
      }
    }
  }

  const getTrendDirection = () => {
    if (!data?.predictions || data.predictions.length < 2) return 'neutral'
    const first = data.predictions[0].predicted_balance
    const last = data.predictions[data.predictions.length - 1].predicted_balance
    return last > first ? 'up' : last < first ? 'down' : 'neutral'
  }

  const getTrendPercentage = () => {
    if (!data?.predictions || data.predictions.length < 2) return 0
    const first = data.predictions[0].predicted_balance
    const last = data.predictions[data.predictions.length - 1].predicted_balance
    return ((last - first) / first) * 100
  }

  if (loading && !data) {
    return (
      <div className="card p-5">
        <div className="animate-pulse">
          <div className="h-4 bg-white/10 rounded w-1/3 mb-4"></div>
          <div className="h-48 bg-white/10 rounded"></div>
        </div>
      </div>
    )
  }

  const chartData = data?.predictions.map(p => ({
    date: new Date(p.date).toLocaleDateString('en-US', { month: 'short', year: '2-digit' }),
    fullDate: p.date,
    balance: Math.round(p.predicted_balance),
    risk: p.risk_level,
    confidence: p.confidence
  })) || []

  const trend = getTrendDirection()
  const trendPercent = getTrendPercentage()
  const currentBalance = data?.predictions?.[data.predictions.length - 1]?.predicted_balance || 0

  return (
    <motion.div 
      className="card p-5"
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
    >
      {/* Header */}
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold text-white flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-cyan-400 animate-pulse"></span>
            Cash Flow Forecast
          </h3>
          <p className="text-sm text-slate-400">AI-powered predictions</p>
        </div>
        
        <div className="flex items-center gap-3">
          {/* Time Period Selector */}
          <div className="flex bg-white/5 rounded-lg p-1">
            {(['3m', '6m', '12m'] as TimePeriod[]).map((period) => (
              <button
                key={period}
                onClick={() => setTimePeriod(period)}
                className={`px-3 py-1 text-xs rounded-md transition-all ${
                  timePeriod === period 
                    ? 'bg-brand-500 text-white' 
                    : 'text-slate-400 hover:text-white'
                }`}
              >
                {period}
              </button>
            ))}
          </div>
          
          {/* Refresh Button */}
          <motion.button
            onClick={handleRefresh}
            disabled={isRefreshing}
            className="p-2 rounded-lg bg-white/5 hover:bg-white/10 transition-colors"
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
          >
            <svg 
              className={`w-4 h-4 text-slate-400 ${isRefreshing ? 'animate-spin' : ''}`} 
              fill="none" 
              viewBox="0 0 24 24" 
              stroke="currentColor"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.582 0a8.002 8.002 0 011.582 8.166m0 0a8.003 8.003 0 01-1.582 8.166m0 0a8.001 8.001 0 01-1.582-8.166m0 0h.582M4 12a8 8 0 0116 0" />
            </svg>
          </motion.button>
        </div>
      </div>

      {/* Stats Row */}
      <div className="grid grid-cols-3 gap-4 mb-4">
        <div className="p-3 rounded-lg bg-white/5 border border-white/10">
          <div className="text-xs text-slate-400 uppercase tracking-wider">Projected Balance</div>
          <motion.div 
            className="text-xl font-semibold text-white"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            key={currentBalance}
          >
            ${Math.round(currentBalance).toLocaleString()}
          </motion.div>
        </div>
        
        <div className="p-3 rounded-lg bg-white/5 border border-white/10">
          <div className="text-xs text-slate-400 uppercase tracking-wider">Trend</div>
          <div className="flex items-center gap-2">
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              className={`text-xl font-semibold ${
                trend === 'up' ? 'text-green-400' : trend === 'down' ? 'text-red-400' : 'text-slate-400'
              }`}
            >
              {trend === 'up' ? '↑' : trend === 'down' ? '↓' : '→'}
            </motion.div>
            <span className={`text-sm ${
              trendPercent > 0 ? 'text-green-400' : trendPercent < 0 ? 'text-red-400' : 'text-slate-400'
            }`}>
              {trendPercent > 0 ? '+' : ''}{trendPercent.toFixed(1)}%
            </span>
          </div>
        </div>
        
        <div className="p-3 rounded-lg bg-white/5 border border-white/10">
          <div className="text-xs text-slate-400 uppercase tracking-wider">Confidence</div>
          <div className="flex items-center gap-2">
            <span className="text-xl font-semibold text-cyan-400">
              {data?.confidence ? `${Math.round(data.confidence * 100)}%` : 'N/A'}
            </span>
          </div>
        </div>
      </div>

      {/* Chart */}
      <div className="h-64">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={chartData} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
            <defs>
              <linearGradient id="balanceGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#2ac0ff" stopOpacity={0.4}/>
                <stop offset="95%" stopColor="#2ac0ff" stopOpacity={0.05}/>
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#334155" opacity={0.3} />
            <XAxis 
              dataKey="date" 
              stroke="#64748b" 
              fontSize={11}
              tickLine={false}
              axisLine={{ stroke: '#334155' }}
            />
            <YAxis 
              stroke="#64748b" 
              fontSize={11}
              tickLine={false}
              axisLine={{ stroke: '#334155' }}
              tickFormatter={(value) => `$${(value / 1000).toFixed(0)}k`}
              width={45}
            />
            <Tooltip 
              contentStyle={{ 
                backgroundColor: '#0f172a', 
                border: '1px solid #1e293b',
                borderRadius: '12px',
                color: '#f8fafc',
                boxShadow: '0 10px 40px rgba(0,0,0,0.5)'
              }}
              formatter={(value: number, name: string, props: any) => [
                `$${value.toLocaleString()}`,
                props.payload.risk ? `Risk: ${props.payload.risk}` : 'Balance'
              ]}
              labelStyle={{ color: '#94a3b8', marginBottom: '4px' }}
            />
            <ReferenceLine y={0} stroke="#334155" strokeDasharray="3 3" />
            <Area 
              type="monotone" 
              dataKey="balance" 
              stroke="#2ac0ff" 
              strokeWidth={3}
              fill="url(#balanceGradient)" 
              animationDuration={1500}
              animationEasing="ease-out"
              dot={{ fill: '#2ac0ff', strokeWidth: 0, r: 4 }}
              activeDot={{ 
                r: 8, 
                fill: '#2ac0ff',
                stroke: '#fff',
                strokeWidth: 2,
                style: { filter: 'drop-shadow(0 0 8px rgba(42, 192, 255, 0.6))' }
              }}
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>

      {/* Legend */}
      <div className="mt-4 flex flex-wrap items-center justify-between">
        <div className="flex items-center gap-4">
          {Object.entries(RISK_COLORS).map(([level, color]) => (
            <div key={level} className="flex items-center gap-2">
              <div 
                className="w-3 h-3 rounded-full" 
                style={{ backgroundColor: color, boxShadow: `0 0 8px ${color}50` }}
              />
              <span className="text-xs text-slate-400 capitalize">{level}</span>
            </div>
          ))}
        </div>
        
        <div className="text-xs text-slate-500">
          Last updated: {new Date().toLocaleTimeString()}
        </div>
      </div>

      {/* Error Toast */}
      <AnimatePresence>
        {error && (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className="mt-4 p-3 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-sm"
          >
            {error}
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  )
}
