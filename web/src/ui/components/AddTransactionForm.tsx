import { useState } from 'react'
import { motion } from 'framer-motion'

interface TransactionFormData {
  type: 'INCOME' | 'EXPENSE'
  amount: string
  description: string
  merchant: string
  category: string
  transactionDate: string
}

interface ValidationErrors {
  type?: string
  amount?: string
  description?: string
  merchant?: string
  category?: string
  transactionDate?: string
  general?: string
}

export default function AddTransactionForm({ onClose, onSuccess }: {
  onClose: () => void
  onSuccess: () => void
}) {
  const [formData, setFormData] = useState<TransactionFormData>({
    type: 'EXPENSE',
    amount: '',
    description: '',
    merchant: '',
    category: '',
    transactionDate: new Date().toISOString().split('T')[0]
  })

  const [errors, setErrors] = useState<ValidationErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  const validateForm = (): boolean => {
    const newErrors: ValidationErrors = {}

    // Amount validation
    if (!formData.amount.trim()) {
      newErrors.amount = 'Amount is required'
    } else {
      const amount = parseFloat(formData.amount)
      if (isNaN(amount) || amount <= 0) {
        newErrors.amount = 'Amount must be a positive number'
      } else if (amount > 1000000) {
        newErrors.amount = 'Amount cannot exceed $1,000,000'
      }
    }

    // Description validation
    if (!formData.description.trim()) {
      newErrors.description = 'Description is required'
    } else if (formData.description.length < 3) {
      newErrors.description = 'Description must be at least 3 characters'
    } else if (formData.description.length > 200) {
      newErrors.description = 'Description cannot exceed 200 characters'
    }

    // Merchant validation (optional but if provided, validate)
    if (formData.merchant && formData.merchant.length > 100) {
      newErrors.merchant = 'Merchant name cannot exceed 100 characters'
    }

    // Category validation
    if (!formData.category.trim()) {
      newErrors.category = 'Category is required'
    } else if (formData.category.length > 50) {
      newErrors.category = 'Category cannot exceed 50 characters'
    }

    // Date validation
    if (!formData.transactionDate) {
      newErrors.transactionDate = 'Transaction date is required'
    } else {
      const date = new Date(formData.transactionDate)
      const now = new Date()
      const oneYearAgo = new Date()
      oneYearAgo.setFullYear(now.getFullYear() - 1)

      if (date > now) {
        newErrors.transactionDate = 'Transaction date cannot be in the future'
      } else if (date < oneYearAgo) {
        newErrors.transactionDate = 'Transaction date cannot be more than 1 year ago'
      }
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validateForm()) {
      return
    }

    setIsSubmitting(true)
    setErrors({})

    try {
      const token = localStorage.getItem('access_token')
      if (!token) {
        setErrors({ general: 'Authentication required. Please log in again.' })
        return
      }

      const response = await fetch('/api/finance/api/transactions', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          ...formData,
          amount: parseFloat(formData.amount)
        })
      })

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}))
        throw new Error(errorData.message || `HTTP ${response.status}`)
      }

      onSuccess()
      onClose()
    } catch (error) {
      console.error('Transaction submission error:', error)
      setErrors({
        general: error instanceof Error
          ? error.message
          : 'Failed to add transaction. Please try again.'
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleInputChange = (field: keyof TransactionFormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }))
    // Clear field-specific error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }))
    }
  }

  const categories = [
    'Housing', 'Food', 'Transportation', 'Entertainment',
    'Healthcare', 'Shopping', 'Utilities', 'Insurance',
    'Education', 'Travel', 'Other'
  ]

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4"
      onClick={onClose}
    >
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.9, opacity: 0 }}
        className="card p-6 w-full max-w-md"
        onClick={e => e.stopPropagation()}
      >
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold">Add Transaction</h2>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-white transition"
          >
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Transaction Type */}
          <div>
            <label className="block text-sm font-medium mb-2">Type</label>
            <div className="flex gap-4">
              {(['INCOME', 'EXPENSE'] as const).map(type => (
                <label key={type} className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="radio"
                    name="type"
                    value={type}
                    checked={formData.type === type}
                    onChange={e => handleInputChange('type', e.target.value as 'INCOME' | 'EXPENSE')}
                    className="text-brand-500"
                  />
                  <span className="text-sm">{type}</span>
                </label>
              ))}
            </div>
          </div>

          {/* Amount */}
          <div>
            <label className="block text-sm font-medium mb-2">Amount ($)</label>
            <input
              type="number"
              step="0.01"
              min="0.01"
              max="1000000"
              value={formData.amount}
              onChange={e => handleInputChange('amount', e.target.value)}
              className={`w-full px-3 py-2 bg-white/10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500 ${
                errors.amount ? 'border-red-500' : 'border-white/20'
              }`}
              placeholder="0.00"
            />
            {errors.amount && (
              <p className="text-red-400 text-sm mt-1">{errors.amount}</p>
            )}
          </div>

          {/* Description */}
          <div>
            <label className="block text-sm font-medium mb-2">Description</label>
            <input
              type="text"
              value={formData.description}
              onChange={e => handleInputChange('description', e.target.value)}
              className={`w-full px-3 py-2 bg-white/10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500 ${
                errors.description ? 'border-red-500' : 'border-white/20'
              }`}
              placeholder="Transaction description"
              maxLength={200}
            />
            {errors.description && (
              <p className="text-red-400 text-sm mt-1">{errors.description}</p>
            )}
          </div>

          {/* Merchant */}
          <div>
            <label className="block text-sm font-medium mb-2">Merchant (Optional)</label>
            <input
              type="text"
              value={formData.merchant}
              onChange={e => handleInputChange('merchant', e.target.value)}
              className={`w-full px-3 py-2 bg-white/10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500 ${
                errors.merchant ? 'border-red-500' : 'border-white/20'
              }`}
              placeholder="Merchant name"
              maxLength={100}
            />
            {errors.merchant && (
              <p className="text-red-400 text-sm mt-1">{errors.merchant}</p>
            )}
          </div>

          {/* Category */}
          <div>
            <label className="block text-sm font-medium mb-2">Category</label>
            <select
              value={formData.category}
              onChange={e => handleInputChange('category', e.target.value)}
              className={`w-full px-3 py-2 bg-white/10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500 ${
                errors.category ? 'border-red-500' : 'border-white/20'
              }`}
            >
              <option value="">Select a category</option>
              {categories.map(cat => (
                <option key={cat} value={cat.toLowerCase()}>{cat}</option>
              ))}
            </select>
            {errors.category && (
              <p className="text-red-400 text-sm mt-1">{errors.category}</p>
            )}
          </div>

          {/* Date */}
          <div>
            <label className="block text-sm font-medium mb-2">Date</label>
            <input
              type="date"
              value={formData.transactionDate}
              onChange={e => handleInputChange('transactionDate', e.target.value)}
              className={`w-full px-3 py-2 bg-white/10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500 ${
                errors.transactionDate ? 'border-red-500' : 'border-white/20'
              }`}
            />
            {errors.transactionDate && (
              <p className="text-red-400 text-sm mt-1">{errors.transactionDate}</p>
            )}
          </div>

          {/* General Error */}
          {errors.general && (
            <div className="p-3 bg-red-500/20 border border-red-500/30 rounded-lg">
              <p className="text-red-400 text-sm">{errors.general}</p>
            </div>
          )}

          {/* Submit Button */}
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 bg-white/10 hover:bg-white/20 border border-white/20 rounded-lg transition"
              disabled={isSubmitting}
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="flex-1 px-4 py-2 bg-brand-500 hover:bg-brand-600 text-white rounded-lg transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isSubmitting ? 'Adding...' : 'Add Transaction'}
            </button>
          </div>
        </form>
      </motion.div>
    </motion.div>
  )
}
