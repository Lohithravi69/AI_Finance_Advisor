import { useEffect, useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useAuthStore } from '../../stores/authStore'

interface RiskAssessmentData {
  risk_assessment: {
    overall_risk_score: number
    risk_factors: string[]
    mitigation_strategies: string[]
    confidence_level: number
  }
  insights: Array<{
    type: string
    severity: string
    message: string
  }>
  confidence: number
}

const getRiskLevel = (score: number): { level: string; color: string; bgColor: string; ringColor: string } => {
  if (score < 0.3) return { level: 'Low', color: 'text-green-400', bgColor: 'bg-green-400', ringColor: 'ring-green-400' }
  if (score < 0.6) return { level: 'Medium', color: 'text-yellow-400', bgColor: 'bg-yellow-400', ringColor: 'ring-yellow-400' }
  if (score < 0.8) return { level: 'High', color: 'text-orange-400', bgColor: 'bg-orange-400', ringColor: 'ring-orange-400' }
  return { level: 'Critical', color: 'text-red-400', bgColor: 'bg-red-400', ringColor: 'ring-red-400' }
}

export default function RiskAssessmentCard() {
  const { token } = useAuthStore()
  const [data, setData] = useState<RiskAssessmentData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [isRefreshing, setIsRefreshing] = useState(false)
  const [expandedStrategy, setExpandedStrategy] = useState<number | null>(null)

  useEffect(() => {
    fetchRiskAssessment()
  }, [token])

  const fetchRiskAssessment = async () => {
    if (!token) {
      setLoading(false)
      return
    }

    try {
      setLoading(true)
      setError(null)
      
      const response = await fetch('/api/insights/risk-assessment', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (!response.ok) {
        throw new Error('Failed to fetch risk assessment')
      }

      const result = await response.json()
      setData(result)
    } catch (err) {
      console.error('Risk assessment error:', err)
      setError(err instanceof Error ? err.message : 'Failed to load assessment')
      setData(generateMockRiskData())
    } finally {
      setLoading(false)
      setIsRefreshing(false)
    }
  }

  const handleRefresh = () => {
    setIsRefreshing(true)
    fetchRiskAssessment()
  }

  const generateMockRiskData = (): RiskAssessmentData => ({
    risk_assessment: {
      overall_risk_score: Math.random() * 0.6 + 0.1,
      risk_factors: [
        'Limited emergency fund coverage',
        'High discretionary spending ratio',
        'Income variability detected'
      ],
      mitigation_strategies: [
        'Build 3-6 months of expenses in emergency fund',
        'Create category budgets and track spending',
        'Automate monthly savings transfers',
        'Diversify income sources',
        'Review and reduce subscription services'
      ],
      confidence_level: 0.82
    },
    insights: [
      {
        type: 'warning',
        severity: 'medium',
        message: 'Your emergency fund covers only 2 months of expenses. Consider building it to 6 months.'
      },
      {
        type: 'success',
        severity: 'low',
        message: 'Great progress on reducing high-interest debt!'
      }
    ],
    confidence: 0.82
  })

  if (loading && !data) {
    return (
      <div className="card p-5">
        <div className="animate-pulse">
          <div className="h-4 bg-white/10 rounded w-1/3 mb-4"></div>
          <div className="h-32 bg-white/10 rounded mb-4"></div>
          <div className="h-20 bg-white/10 rounded"></div>
        </div>
      </div>
    )
  }

  const riskScore = data?.risk_assessment?.overall_risk_score ?? 0.5
  const riskInfo = getRiskLevel(riskScore)
  const riskFactors = data?.risk_assessment?.risk_factors || []
  const mitigationStrategies = data?.risk_assessment?.mitigation_strategies || []

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
            <span className={`w-2 h-2 rounded-full ${riskInfo.bgColor} animate-pulse`}></span>
            Financial Risk Assessment
          </h3>
          <p className="text-sm text-slate-400">AI-powered analysis</p>
        </div>
        
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

      {/* Risk Score Gauge */}
      <div className="flex items-center justify-center mb-6">
        <div className="relative w-36 h-36">
          <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
            {/* Background circle */}
            <circle
              cx="50"
              cy="50"
              r="42"
              stroke="#1e293b"
              strokeWidth="8"
              fill="none"
            />
            {/* Progress circle */}
            <motion.circle
              cx="50"
              cy="50"
              r="42"
              stroke={riskInfo.bgColor.replace('bg-', '')}
              strokeWidth="8"
              fill="none"
              strokeLinecap="round"
              initial={{ strokeDasharray: "0 264" }}
              animate={{ strokeDasharray: `${riskScore * 264} 264` }}
              transition={{ duration: 1.5, ease: "easeOut" }}
              style={{
                filter: `drop-shadow(0 0 8px ${riskInfo.color.replace('text-', '')})`
              }}
            />
          </svg>
          <div className="absolute inset-0 flex flex-col items-center justify-center">
            <motion.span 
              className={`text-4xl font-bold ${riskInfo.color}`}
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ delay: 0.5, type: "spring" }}
            >
              {Math.round(riskScore * 100)}
            </motion.span>
            <span className={`text-xs font-medium ${riskInfo.color} uppercase tracking-wider`}>
              {riskInfo.level} Risk
            </span>
          </div>
        </div>
      </div>

      {/* Confidence Badge */}
      <div className="flex justify-center mb-4">
        <div className="px-3 py-1 rounded-full bg-white/5 border border-white/10 flex items-center gap-2">
          <span className="text-xs text-slate-400">Confidence:</span>
          <span className="text-sm font-medium text-cyan-400">
            {data?.confidence ? `${Math.round(data.confidence * 100)}%` : 'N/A'}
          </span>
        </div>
      </div>

      {/* Risk Factors */}
      <AnimatePresence>
        {riskFactors.length > 0 && (
          <motion.div 
            className="mb-4"
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
          >
            <h4 className="text-sm font-medium text-slate-300 mb-2 flex items-center gap-2">
              <span className="w-1 h-1 rounded-full bg-red-400"></span>
              Risk Factors
            </h4>
            <ul className="space-y-2">
              {riskFactors.map((factor, idx) => (
                <motion.li 
                  key={idx}
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: idx * 0.1 }}
                  className="flex items-start gap-2 text-sm text-slate-400 bg-red-500/5 p-2 rounded-lg"
                >
                  <span className="text-red-400 mt-0.5">⚠</span>
                  {factor}
                </motion.li>
              ))}
            </ul>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Mitigation Strategies */}
      <AnimatePresence>
        {mitigationStrategies.length > 0 && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
          >
            <h4 className="text-sm font-medium text-slate-300 mb-2 flex items-center gap-2">
              <span className="w-1 h-1 rounded-full bg-green-400"></span>
              Recommended Actions
            </h4>
            <ul className="space-y-2">
              {mitigationStrategies.slice(0, 4).map((strategy, idx) => (
                <motion.li 
                  key={idx}
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: (idx + riskFactors.length) * 0.1 }}
                  className="flex items-start gap-2 text-sm text-slate-400"
                >
                  <span className="text-green-400 mt-0.5">✓</span>
                  <span>{strategy}</span>
                </motion.li>
              ))}
            </ul>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Insights */}
      <AnimatePresence>
        {data?.insights && data.insights.length > 0 && (
          <motion.div 
            className="mt-4 space-y-2"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.5 }}
          >
            {data.insights.map((insight, idx) => (
              <motion.div
                key={idx}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.6 + idx * 0.1 }}
                className={`p-3 rounded-lg border ${
                  insight.severity === 'high' || insight.severity === 'medium'
                    ? 'bg-yellow-500/5 border-yellow-500/20'
                    : 'bg-green-500/5 border-green-500/20'
                }`}
              >
                <div className="flex items-center gap-2 mb-1">
                  <span className={insight.severity === 'high' || insight.severity === 'medium' ? 'text-yellow-400' : 'text-green-400'}>
                    {insight.severity === 'high' || insight.severity === 'medium' ? '⚠️' : '✅'}
                  </span>
                  <span className={`text-xs font-medium uppercase ${
                    insight.severity === 'high' || insight.severity === 'medium'
                      ? 'text-yellow-400'
                      : 'text-green-400'
                  }`}>
                    {insight.type}
                  </span>
                </div>
                <p className="text-sm text-slate-300">{insight.message}</p>
              </motion.div>
            ))}
          </motion.div>
        )}
      </AnimatePresence>

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
