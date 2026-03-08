import React, { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useAuthStore } from '../stores/authStore'
import { CategoryList } from '../ui/components/CategoryList'
import { CreateCategoryForm } from '../ui/components/CreateCategoryForm'
import { ExpenseBreakdown } from '../ui/components/ExpenseBreakdown'

interface Category {
  id: number
  name: string
  icon: string
  color: string
  description: string
  monthlyBudget?: number
  spendingThisMonth?: number
  percentageSpent?: number
  isActive: boolean
}

interface SpendingAnalysis {
  categoryId: number
  categoryName: string
  icon: string
  color: string
  totalSpent: number
  monthlyBudget?: number
  percentageOfTotal: number
  percentageOfBudget?: number
  transactionCount: number
  averageTransaction: number
  trendIndicator: 'UP' | 'DOWN' | 'STABLE'
  isAnomalous: boolean
}

export const CategoriesPage: React.FC = () => {
  const { token } = useAuthStore()
  const [categories, setCategories] = useState<Category[]>([])
  const [spendingAnalysis, setSpendingAnalysis] = useState<SpendingAnalysis[]>([])
  const [showCreateForm, setShowCreateForm] = useState(false)
  const [activeTab, setActiveTab] = useState<'categories' | 'analysis'>('categories')
  const [loading, setLoading] = useState(true)
  const [editingCategory, setEditingCategory] = useState<Category | null>(null)

  useEffect(() => {
    fetchCategories()
  }, [token])

  const fetchCategories = async () => {
    try {
      const response = await fetch('/api/categories', {
        headers: token ? { Authorization: `Bearer ${token}` } : {}
      })
      if (response.ok) {
        const data = await response.json()
        setCategories(data.map((c: any) => ({
          id: c.id,
          name: c.name,
          icon: c.icon || '📁',
          color: c.color || '#6366f1',
          description: c.description || '',
          monthlyBudget: c.monthlyBudget || c.monthly_budget,
          spendingThisMonth: c.spendingThisMonth || c.spending_this_month || 0,
          percentageSpent: c.percentageSpent || c.percentage_spent || 0,
          isActive: c.isActive !== false
        })))
      }
    } catch (error) {
      console.warn('Failed to fetch categories:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleCreateCategory = async (categoryData: any) => {
    try {
      const response = await fetch('/api/categories', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? { Authorization: `Bearer ${token}` } : {})
        },
        body: JSON.stringify(categoryData)
      })
      if (response.ok) {
        const newCategory = await response.json()
        setCategories([...categories, newCategory])
      }
    } catch (error) {
      console.error('Failed to create category:', error)
    }
    setShowCreateForm(false)
  }

  const handleEditCategory = async (updatedCategory: any) => {
    if (!editingCategory) return
    
    try {
      const response = await fetch(`/api/categories/${editingCategory.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? { Authorization: `Bearer ${token}` } : {})
        },
        body: JSON.stringify(updatedCategory)
      })
      if (response.ok) {
        const updated = await response.json()
        setCategories(categories.map(c => c.id === editingCategory.id ? updated : c))
      }
    } catch (error) {
      console.error('Failed to update category:', error)
    }
    setEditingCategory(null)
    setShowCreateForm(false)
  }

  const handleDeleteCategory = async (categoryId: number) => {
    if (!confirm('Are you sure you want to delete this category?')) return
    
    try {
      const response = await fetch(`/api/categories/${categoryId}`, {
        method: 'DELETE',
        headers: token ? { Authorization: `Bearer ${token}` } : {}
      })
      if (response.ok) {
        setCategories(categories.filter(c => c.id !== categoryId))
      }
    } catch (error) {
      console.error('Failed to delete category:', error)
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-brand-500"></div>
      </div>
    )
  }

  return (
    <div className="min-h-screen">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="mb-8"
      >
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-3xl font-bold text-white">Categories</h1>
            <p className="text-slate-400 mt-1">
              Manage spending categories and analyze expenses
            </p>
          </div>
          <button
            onClick={() => setShowCreateForm(true)}
            className="px-6 py-3 bg-brand-500 text-white rounded-lg hover:bg-brand-600 transition-colors font-semibold flex items-center gap-2"
          >
            + Create Category
          </button>
        </div>
      </motion.div>

      {/* Tabs */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        className="flex gap-4 mb-6 border-b border-white/10"
      >
        {(['categories', 'analysis'] as const).map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-4 py-3 font-medium transition-colors ${
              activeTab === tab
                ? 'text-brand-400 border-b-2 border-brand-400'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            {tab === 'categories' ? 'Categories' : 'Spending Analysis'}
          </button>
        ))}
      </motion.div>

      {/* Content */}
      <AnimatePresence mode="wait">
        {activeTab === 'categories' ? (
          <motion.div
            key="categories"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
          >
            <CategoryList
              categories={categories}
              onEdit={(category) => {
                setEditingCategory(category)
                setShowCreateForm(true)
              }}
              onDelete={handleDeleteCategory}
              showBudget={true}
            />
          </motion.div>
        ) : (
          <motion.div
            key="analysis"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
          >
            <ExpenseBreakdown
              data={spendingAnalysis}
              showTrend={true}
              showAnomalies={true}
            />
          </motion.div>
        )}
      </AnimatePresence>

      {/* Create/Edit Form Modal */}
      <AnimatePresence>
        {showCreateForm && (
          <CreateCategoryForm
            onSubmit={editingCategory ? handleEditCategory : handleCreateCategory}
            onClose={() => {
              setShowCreateForm(false)
              setEditingCategory(null)
            }}
          />
        )}
      </AnimatePresence>
    </div>
  )
}
