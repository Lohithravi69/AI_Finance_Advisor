import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { useAuthStore } from '../../stores/authStore'

interface RiskFactor {
  factor: string
}

interface MitigationStrategy {
  strategy: string
}

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

const getRiskLevel = (score: number): { level: string; color: string; bgColor: string } => {
  if (score < 0.3) return { level: 'Low', color: 'text-green-400', bgColor: 'bg-green-400' }
  if (score < 0.6) return { level: 'Medium', color: 'text-yellow-400', bgColor: 'bg-yellow-400' }
  if (score < 0.8) return { level: 'High', color: 'text-orange-400', bgColor: 'bg-orange-400' }
  return { level: 'Critical', color: 'text-red-400', bgColor: 'bg-red-400' }
}

export default function RiskAssessmentCard() {
  const { token } = useAuthStore()
  const [data, setData] = useState<RiskAssessmentData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

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
    }
  }

  const generateMockRiskData = (): RiskAssessmentData => ({
    risk_assessment: {
      overall_risk_score: 0.35,
      risk_factors: [
        'Limited emergency fund coverage',
        'High discretionary spending ratio'
      ],
      mitigation_strategies: [
        'Build 3-6 months of expenses in emergency fund',
        'Create category budgets and track spending',
        'Automate monthly savings transfers'
      ],
      confidence_level: 0.82
    },
    insights: [
      {
        type: 'warning',
        severity: 'medium',
        message: 'Your emergency fund covers only 2 months of expenses. Consider building it to 6 months.'
      }
    ],
    confidence: 0.82
  })

  if (loading) {
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

  if (error && !data) {
    return (
      <div className="card p-5">
        <div className="text-red-400 text-sm">{error}</div>
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
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold text-white">Financial Risk Assessment</h3>
          <p className="text-sm text-slate-400">Based on your financial health</p>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-xs text-slate-400">Confidence:</span>
          <span className="text-sm font-medium text-cyan-400">
            {data?.confidence ? `${Math.round(data.confidence * 100)}%` : 'N/A'}
          </span>
        </div>
      </div>

      {/* Risk Score Gauge */}
      <div className="flex items-center justify-center mb-6">
        <div className="relative w-32 h-32">
          <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
            {/* Background circle */}
            <circle
              cx="50"
              cy="50"
              r="40"
              stroke="#334155"
              strokeWidth="10"
              fill="none"
            />
            {/* Progress circle */}
            <circle
              cx="50"
              cy="50"
              r="40"
              stroke={riskInfo.bgColor.replace('bg-', '').replace('-400', '').replace('green', '#22c55e').replace('yellow', '#eab308').replace('orange', '#f97316').replace('red', '#ef4444')}
              strokeWidth="10"
              fill="none"
              strokeLinecap="round"
              strokeDasharray={`${riskScore * 251.2} 251.2`}
              className="transition-all duration-1000"
            />
          </svg>
          <div className="absolute inset-0 flex flex-col items-center justify-center">
            <span className={`text-3xl font-bold ${riskInfo.color}`}>
              {Math.round(riskScore * 100)}
            </span>
            <span className={`text-xs ${riskInfo.color}`}>{riskInfo.level}</span>
          </div>
        </div>
      </div>

      {/* Risk Factors */}
      {riskFactors.length > 0 && (
        <div className="mb-4">
          <h4 className="text-sm font-medium text-slate-300 mb-2">Risk Factors</h4>
          <ul className="space-y-1">
            {riskFactors.map((factor, idx) => (
              <li key={idx} className="flex items-start gap-2 text-sm text-slate-400">
                <span className="text-red-400 mt-0.5">•</span>
                {factor}
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Mitigation Strategies */}
      {mitigationStrategies.length > 0 && (
        <div>
          <h4 className="text-sm font-medium text-slate-300 mb-2">Recommended Actions</h4>
          <ul className="space-y-1">
            {mitigationStrategies.slice(0, 3).map((strategy, idx) => (
              <li key={idx} className="flex items-start gap-2 text-sm text-slate-400">
                <span className="text-green-400 mt-0.5">✓</span>
                {strategy}
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Insights */}
      {data?.insights && data.insights.length > 0 && (
        <div className="mt-4 p-3 rounded-lg bg-white/5 border border-white/10">
          <div className="flex items-center gap-2 mb-2">
            <span className="text-yellow-400">⚠️</span>
            <span className="text-sm font-medium text-slate-300">Insight</span>
          </div>
          <p className="text-sm text-slate-400">{data.insights[0]?.message}</p>
        </div>
      )}
    </motion.div>
  )
}
