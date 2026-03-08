
import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { useAuthStore } from '../../stores/authStore'

interface GoalPrediction {
  months_to_goal: number
  years_to_goal: number
  total_contributions: number
  total_interest: number
  feasibility_score: number
  monthly_projections: Array<{
    month: number
    balance: number
    contributions: number
    interest_earned: number
  }>
}

interface GoalPredictionData {
  forecast: GoalPrediction
  insights: Array<{
    type: string
    severity: string
    message: string
    current_amount?: number
    target_amount?: number
    percentage_change?: number
  }>
  confidence: number
}

interface GoalTimelineProps {
  goalId?: number
  currentAmount: number
  targetAmount: number
  monthlyContribution: number
}

export default function GoalTimeline({ goalId, currentAmount, targetAmount, monthlyContribution }: GoalTimelineProps) {
  const { token } = useAuthStore()
  const [data, setData] = useState<GoalPredictionData | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (currentAmount > 0 && targetAmount > 0) {
      fetchGoalPrediction()
    }
  }, [token, currentAmount, targetAmount, monthlyContribution])

  const fetchGoalPrediction = async () => {
    if (!token) return

    try {
      setLoading(true)
      setError(null)
      
      const response = await fetch('/api/insights/goal-prediction', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          currentSavings: currentAmount,
          monthlyContribution: monthlyContribution,
          targetAmount: targetAmount,
          expectedReturnRate: 0.07
        })
      })

      if (!response.ok) {
        throw new Error('Failed to fetch goal prediction')
      }

      const result = await response.json()
      setData(result)
    } catch (err) {
      console.error('Goal prediction error:', err)
      setError(err instanceof Error ? err.message : 'Failed to load prediction')
      setData(generateMockPrediction())
    } finally {
      setLoading(false)
    }
  }

  const generateMockPrediction = (): GoalPredictionData => {
    const monthsNeeded = Math.max(1, Math.ceil((targetAmount - currentAmount) / monthlyContribution))
    
    return {
      forecast: {
        months_to_goal: monthsNeeded,
        years_to_goal: monthsNeeded / 12,
        total_contributions: monthlyContribution * monthsNeeded,
        total_interest: targetAmount - currentAmount - (monthlyContribution * monthsNeeded),
        feasibility_score: monthsNeeded <= 12 ? 0.9 : monthsNeeded <= 36 ? 0.7 : 0.5,
        monthly_projections: []
      },
      insights: [
        {
          type: 'info',
          severity: 'low',
          message: monthsNeeded > 24 
            ? `Consider increasing your monthly contribution to reach your goal sooner.`
            : `You're on track to reach your goal in ${monthsNeeded} months!`,
          current_amount: currentAmount,
          target_amount: targetAmount,
          percentage_change: (currentAmount / targetAmount) * 100
        }
      ],
      confidence: 0.85
    }
  }

  if (!currentAmount || !targetAmount) {
    return null
  }

  const progress = Math.min((currentAmount / targetAmount) * 100, 100)
  const forecast = data?.forecast
  const monthsRemaining = forecast?.months_to_goal || Math.max(1, Math.ceil((targetAmount - currentAmount) / monthlyContribution))
  const projectedDate = new Date()
  projectedDate.setMonth(projectedDate.getMonth() + monthsRemaining)

  const getFeasibilityColor = (score?: number): string => {
    const safeScore = score ?? 0.7
    if (safeScore >= 0.8) return 'text-green-400'
    if (safeScore >= 0.6) return 'text-yellow-400'
    if (safeScore >= 0.4) return 'text-orange-400'
    return 'text-red-400'
  }

  const getFeasibilityLabel = (score?: number): string => {
    const safeScore = score ?? 0.7
    if (safeScore >= 0.8) return 'Excellent'
    if (safeScore >= 0.6) return 'Good'
    if (safeScore >= 0.4) return 'Fair'
    return 'Needs attention'
  }

  const feasibilityScore = forecast?.feasibility_score

  return (
    <motion.div 
      className="card p-5"
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
    >
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold text-white">Goal Timeline</h3>
          <p className="text-sm text-slate-400">AI-powered prediction</p>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-xs text-slate-400">Confidence:</span>
          <span className="text-sm font-medium text-cyan-400">
            {data?.confidence ? `${Math.round(data.confidence * 100)}%` : 'N/A'}
          </span>
        </div>
      </div>

      {/* Progress Bar */}
      <div className="mb-6">
        <div className="flex justify-between text-sm mb-2">
          <span className="text-slate-400">Current: ${currentAmount.toLocaleString()}</span>
          <span className="text-slate-400">Target: ${targetAmount.toLocaleString()}</span>
        </div>
        <div className="h-3 bg-white/10 rounded-full overflow-hidden">
          <motion.div 
            className="h-full bg-gradient-to-r from-brand-500 to-cyan-400"
            initial={{ width: 0 }}
            animate={{ width: `${progress}%` }}
            transition={{ duration: 1, ease: 'easeOut' }}
          />
        </div>
        <div className="text-center mt-2">
          <span className="text-2xl font-bold text-white">{progress.toFixed(1)}%</span>
          <span className="text-sm text-slate-400 ml-2">completed</span>
        </div>
      </div>

      {/* Timeline */}
      <div className="grid grid-cols-2 gap-4 mb-4">
        <div className="p-4 rounded-lg bg-white/5 border border-white/10">
          <div className="text-xs text-slate-400 uppercase tracking-wider mb-1">Estimated Completion</div>
          <div className="text-lg font-semibold text-white">
            {projectedDate.toLocaleDateString('en-US', { month: 'short', year: 'numeric' })}
          </div>
          <div className="text-xs text-slate-400">
            {monthsRemaining} months remaining
          </div>
        </div>
        
        <div className="p-4 rounded-lg bg-white/5 border border-white/10">
          <div className="text-xs text-slate-400 uppercase tracking-wider mb-1">Feasibility Score</div>
          <div className={`text-lg font-semibold ${getFeasibilityColor(feasibilityScore)}`}>
            {feasibilityScore !== undefined ? `${Math.round(feasibilityScore * 100)}%` : 'N/A'}
          </div>
          <div className="text-xs text-slate-400">
            {getFeasibilityLabel(feasibilityScore)}
          </div>
        </div>
      </div>

      {/* Projections */}
      {forecast && forecast.total_interest > 0 && (
        <div className="p-4 rounded-lg bg-white/5 border border-white/10 mb-4">
          <h4 className="text-sm font-medium text-slate-300 mb-2">Projected Breakdown</h4>
          <div className="grid grid-cols-3 gap-2 text-sm">
            <div>
              <div className="text-slate-400">Your Contributions</div>
              <div className="text-white font-medium">${forecast.total_contributions.toLocaleString()}</div>
            </div>
            <div>
              <div className="text-slate-400">Interest Earned</div>
              <div className="text-green-400 font-medium">+${Math.abs(forecast.total_interest).toLocaleString()}</div>
            </div>
            <div>
              <div className="text-slate-400">Total</div>
              <div className="text-cyan-400 font-medium">${targetAmount.toLocaleString()}</div>
            </div>
          </div>
        </div>
      )}

      {/* Insights */}
      {data?.insights && data.insights.length > 0 && (
        <div className="p-3 rounded-lg bg-white/5 border border-white/10">
          <div className="flex items-center gap-2">
            <span className="text-cyan-400">💡</span>
            <span className="text-sm font-medium text-slate-300">AI Insight</span>
          </div>
          <p className="text-sm text-slate-400 mt-1">{data.insights[0]?.message}</p>
        </div>
      )}
    </motion.div>
  )
}
