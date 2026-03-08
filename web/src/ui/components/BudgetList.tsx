import React, { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { BudgetCard } from './BudgetCard'

interface Budget {
  id: number
  name: string
  category: string
  monthlyLimit: number
  spentAmount: number
  percentageSpent: number
  alertThreshold: number
  createdAt: string
}

interface BudgetListProps {
  onAddBudget?: () => void
}

export const BudgetList: React.FC<BudgetListProps> = ({ onAddBudget }) => {
  const [budgets, setBudgets] = useState<Budget[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchBudgets()
  }, [])

  const fetchBudgets = async () => {
    try {
      const response = await fetch('/api/budgets')
      if (response.ok) {
        const data = await response.json()
        setBudgets(data.map((b: any) => ({
          id: b.id,
          name: b.name,
          category: b.category,
          monthlyLimit: b.monthlyLimit || b.monthly_limit || 0,
          spentAmount: b.spentAmount || b.spent_amount || 0,
          percentageSpent: b.percentageSpent || b.percentage_spent || 0,
          alertThreshold: b.alertThreshold || b.alert_threshold || 80,
          createdAt: b.createdAt || b.created_at || new Date().toISOString()
        })))
      } else {
        // Use empty array if API fails
        setBudgets([])
      }
    } catch (error) {
      console.warn('Failed to fetch budgets:', error)
      setBudgets([])
    } finally {
      setLoading(false)
    }
  }

  const handleEdit = (id: number) => {
    console.log('Edit budget:', id)
    // TODO: Open edit modal
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this budget?')) return
    
    try {
      const response = await fetch(`/api/budgets/${id}`, {
        method: 'DELETE'
      })
      if (response.ok) {
        setBudgets(budgets.filter(b => b.id !== id))
      }
    } catch (error) {
      console.error('Failed to delete budget:', error)
    }
  }

  const handleView = (id: number) => {
    console.log('View budget:', id)
    // TODO: Navigate to detail view
  }

  const handleAddBudget = () => {
    if (onAddBudget) {
      onAddBudget()
    }
  }

  const totalLimit = budgets.reduce((sum, b) => sum + b.monthlyLimit, 0)
  const totalSpent = budgets.reduce((sum, b) => sum + b.spentAmount, 0)
  const totalPercentage = totalLimit > 0 ? (totalSpent / totalLimit) * 100 : 0

  if (loading) {
    return (
      <div className="space-y-4">
        <div className="animate-pulse bg-white/5 rounded-lg p-6 h-40"></div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {[1, 2, 3].map(i => (
            <div key={i} className="animate-pulse bg-white/5 rounded-lg h-48"></div>
          ))}
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Summary Card - Dark Theme */}
      <motion.div 
        initial={{opacity: 0, y: 10}} 
        animate={{opacity: 1, y: 0}}
        className="card p-5 bg-gradient-to-r from-slate-800 to-slate-900"
      >
        <h2 className="text-xl font-bold text-white mb-4">Budget Overview</h2>
        <div className="grid grid-cols-3 gap-4">
          <div>
            <p className="text-slate-400 text-sm">Total Budget</p>
            <p className="text-2xl font-bold text-white">${totalLimit.toFixed(2)}</p>
          </div>
          <div>
            <p className="text-slate-400 text-sm">Total Spent</p>
            <p className="text-2xl font-bold text-brand-400">${totalSpent.toFixed(2)}</p>
          </div>
          <div>
            <p className="text-slate-400 text-sm">Overall Usage</p>
            <p className="text-2xl font-bold text-cyan-400">{totalPercentage.toFixed(1)}%</p>
          </div>
        </div>
        <div className="mt-4">
          <div className="w-full bg-white/10 rounded-full h-3">
            <div
              className="bg-gradient-to-r from-brand-500 to-cyan-400 h-3 rounded-full transition-all duration-300"
              style={{
                width: `${Math.min(totalPercentage, 100)}%`,
              }}
            />
          </div>
        </div>
      </motion.div>

      {/* Budgets Grid */}
      <div>
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-lg font-semibold text-white">Active Budgets</h3>
          <button 
            onClick={handleAddBudget}
            className="px-4 py-2 bg-brand-500 hover:bg-brand-600 text-white rounded-lg transition font-medium"
          >
            + Add Budget
          </button>
        </div>

        {budgets.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {budgets.map((budget) => (
              <BudgetCard
                key={budget.id}
                {...budget}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onView={handleView}
              />
            ))}
          </div>
        ) : (
          <div className="text-center py-12 card">
            <p className="text-slate-400 mb-4">No budgets created yet</p>
            <button 
              onClick={handleAddBudget}
              className="px-6 py-2 bg-brand-500 hover:bg-brand-600 text-white rounded-lg transition font-medium"
            >
              Create Your First Budget
            </button>
          </div>
        )}
      </div>
    </div>
  )
}
