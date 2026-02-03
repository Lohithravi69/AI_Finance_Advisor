import { motion } from 'framer-motion'

interface BudgetAlert {
  id: string
  type: 'critical' | 'warning' | 'info' | 'success'
  title: string
  message: string
  category?: string
  amount?: number
}

interface BudgetAlertsProps {
  alerts: BudgetAlert[]
}

const alertStyles = {
  critical: 'bg-red-500/20 border-red-500/30 text-red-200',
  warning: 'bg-yellow-500/20 border-yellow-500/30 text-yellow-200',
  info: 'bg-blue-500/20 border-blue-500/30 text-blue-200',
  success: 'bg-green-500/20 border-green-500/30 text-green-200'
}

const alertIcons = {
  critical: '🚨',
  warning: '⚠️',
  info: 'ℹ️',
  success: '✅'
}

export function generateBudgetAlerts(data: {
  balance: number
  income: number
  expenses: number
  savingsProgress: number
  spendingBreakdown: Array<{ name: string; value: number }>
}): BudgetAlert[] {
  const alerts: BudgetAlert[] = []

  // Critical alerts
  if (data.balance < 0) {
    alerts.push({
      id: 'negative-balance',
      type: 'critical',
      title: 'Negative Balance Alert',
      message: `Your account is overdrawn by $${Math.abs(data.balance).toFixed(2)}. Consider reducing expenses or increasing income.`,
      amount: data.balance
    })
  }

  // Overspending alerts
  const expenseRatio = data.income > 0 ? (data.expenses / data.income) * 100 : 0
  if (expenseRatio > 80) {
    alerts.push({
      id: 'high-expenses',
      type: 'critical',
      title: 'High Expense Alert',
      message: `Your expenses (${expenseRatio.toFixed(1)}% of income) are very high. Aim to keep expenses below 70% of income.`,
      amount: data.expenses
    })
  } else if (expenseRatio > 70) {
    alerts.push({
      id: 'moderate-expenses',
      type: 'warning',
      title: 'Expense Warning',
      message: `Your expenses are ${expenseRatio.toFixed(1)}% of income. Consider optimizing your spending.`,
      amount: data.expenses
    })
  }

  // Savings alerts
  if (data.savingsProgress < 10 && data.income > 0) {
    alerts.push({
      id: 'low-savings',
      type: 'warning',
      title: 'Low Savings Rate',
      message: `Your savings rate is only ${data.savingsProgress.toFixed(1)}%. Aim for at least 20% of income.`,
      amount: data.income * 0.2
    })
  } else if (data.savingsProgress >= 20) {
    alerts.push({
      id: 'good-savings',
      type: 'success',
      title: 'Great Savings Rate!',
      message: `You're saving ${data.savingsProgress.toFixed(1)}% of your income. Keep up the excellent work!`,
      amount: data.income * (data.savingsProgress / 100)
    })
  }

  // Category-specific alerts
  const totalExpenses = data.expenses
  data.spendingBreakdown.forEach(category => {
    const percentage = totalExpenses > 0 ? (category.value / totalExpenses) * 100 : 0

    // Housing should be max 30%
    if (category.name.toLowerCase().includes('hous') && percentage > 30) {
      alerts.push({
        id: `high-${category.name.toLowerCase()}`,
        type: 'warning',
        title: 'High Housing Costs',
        message: `${percentage.toFixed(1)}% of expenses on housing. Consider optimizing rent/mortgage costs.`,
        category: category.name,
        amount: category.value
      })
    }

    // Dining should be max 15%
    if (category.name.toLowerCase().includes('din') && percentage > 15) {
      alerts.push({
        id: `high-${category.name.toLowerCase()}`,
        type: 'info',
        title: 'Dining Expenses',
        message: `${percentage.toFixed(1)}% of expenses on dining. Consider meal planning to reduce costs.`,
        category: category.name,
        amount: category.value
      })
    }
  })

  // Positive reinforcement
  if (data.balance > data.income * 0.5) {
    alerts.push({
      id: 'healthy-balance',
      type: 'success',
      title: 'Healthy Balance',
      message: `You have a strong emergency fund with $${data.balance.toFixed(2)} saved.`,
      amount: data.balance
    })
  }

  return alerts.slice(0, 6) // Limit to 6 alerts
}

export default function BudgetAlerts({ alerts }: BudgetAlertsProps) {
  if (alerts.length === 0) {
    return (
      <div className="text-sm text-slate-400 mb-2">Budget Alerts</div>
    )
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className="mb-6"
    >
      <div className="text-sm text-slate-400 mb-2">Budget Alerts</div>
      <div className="grid gap-3">
        {alerts.map((alert, index) => (
          <motion.div
            key={alert.id}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: index * 0.1 }}
            className={`p-4 rounded-lg border ${alertStyles[alert.type]} backdrop-blur-sm`}
          >
            <div className="flex items-start gap-3">
              <span className="text-lg">{alertIcons[alert.type]}</span>
              <div className="flex-1">
                <div className="font-medium text-sm mb-1">{alert.title}</div>
                <div className="text-sm opacity-90">{alert.message}</div>
                {alert.amount && (
                  <div className="text-xs mt-2 opacity-75">
                    Amount: ${alert.amount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                  </div>
                )}
              </div>
            </div>
          </motion.div>
        ))}
      </div>
    </motion.div>
  )
}
