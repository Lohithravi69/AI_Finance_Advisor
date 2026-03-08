import React, { useState } from 'react'
import { motion } from 'framer-motion'
import { BudgetList } from '../components/BudgetList'
import { CreateBudgetForm } from '../components/CreateBudgetForm'

export const BudgetsPage: React.FC = () => {
  const [showCreateForm, setShowCreateForm] = useState(false)

  return (
    <div className="min-h-screen">
      <motion.div 
        initial={{opacity: 0, y: 10}} 
        animate={{opacity: 1, y: 0}}
        className="mb-8"
      >
        <h1 className="text-3xl font-bold text-white mb-2">Budget Management</h1>
        <p className="text-slate-400">Track and manage your spending across different categories</p>
      </motion.div>

      {showCreateForm && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50">
          <CreateBudgetForm
            onClose={() => setShowCreateForm(false)}
            onSuccess={() => {
              setShowCreateForm(false)
              // Optionally refresh the list here
            }}
          />
        </div>
      )}

      <BudgetList onAddBudget={() => setShowCreateForm(true)} />
    </div>
  )
}

export default BudgetsPage
