import React, { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { useAuthStore } from '../stores/authStore'
import { accountsApi, AccountResponse, AccountRequest } from '../api/accountsApi'
import AccountCard from '../components/AccountCard'

const AccountsPage: React.FC = () => {
  const { token } = useAuthStore()
  const [accounts, setAccounts] = useState<AccountResponse[]>([])
  const [netWorth, setNetWorth] = useState<number>(0)
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingAccount, setEditingAccount] = useState<AccountResponse | null>(null)
  const [formData, setFormData] = useState<AccountRequest>({
    accountName: '',
    accountType: 'CHECKING',
    institutionName: '',
    currentBalance: 0,
    availableBalance: 0,
    currency: 'USD',
    accountColor: '#3B82F6',
    isPrimary: false,
  })

  useEffect(() => {
    loadAccounts()
  }, [token])

  const loadAccounts = async () => {
    try {
      setLoading(true)
      // For now, use userId=1 as default, in production get from auth
      const userId = 1
      const data = await accountsApi.getAccounts(userId)
      setAccounts(data)
      
      // Calculate net worth
      const total = data.reduce((sum, acc) => {
        if (acc.accountType === 'CREDIT_CARD' || acc.accountType === 'LOAN') {
          return sum - (acc.currentBalance || 0)
        }
        return sum + (acc.currentBalance || 0)
      }, 0)
      setNetWorth(total)
    } catch (error) {
      console.error('Error loading accounts:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const userId = 1
      if (editingAccount) {
        await accountsApi.updateAccount(editingAccount.id, formData)
      } else {
        await accountsApi.createAccount(userId, formData)
      }
      loadAccounts()
      setShowModal(false)
      resetForm()
    } catch (error) {
      console.error('Error saving account:', error)
    }
  }

  const handleEdit = (account: AccountResponse) => {
    setEditingAccount(account)
    setFormData({
      accountName: account.accountName,
      accountType: account.accountType,
      institutionName: account.institutionName || '',
      currentBalance: account.currentBalance,
      availableBalance: account.availableBalance || 0,
      currency: account.currency,
      accountColor: account.accountColor || '#3B82F6',
      isPrimary: account.isPrimary,
    })
    setShowModal(true)
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this account?')) return
    
    try {
      await accountsApi.deleteAccount(id)
      loadAccounts()
    } catch (error) {
      console.error('Error deleting account:', error)
    }
  }

  const resetForm = () => {
    setEditingAccount(null)
    setFormData({
      accountName: '',
      accountType: 'CHECKING',
      institutionName: '',
      currentBalance: 0,
      availableBalance: 0,
      currency: 'USD',
      accountColor: '#3B82F6',
      isPrimary: false,
    })
  }

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount)
  }

  const calculateTotalAssets = () => {
    return accounts
      .filter(a => !['CREDIT_CARD', 'LOAN'].includes(a.accountType))
      .reduce((sum, a) => sum + (a.currentBalance || 0), 0)
  }

  const calculateTotalLiabilities = () => {
    return accounts
      .filter(a => ['CREDIT_CARD', 'LOAN'].includes(a.accountType))
      .reduce((sum, a) => sum + (a.currentBalance || 0), 0)
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-brand-500"></div>
      </div>
    )
  }

  return (
    <div className="min-h-screen">
      <motion.div 
        initial={{opacity: 0, y: 10}} 
        animate={{opacity: 1, y: 0}}
        className="mb-8"
      >
        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-3xl font-bold text-white">Accounts</h1>
            <p className="text-slate-400">Manage your financial accounts</p>
          </div>
          <button
            onClick={() => {
              resetForm()
              setShowModal(true)
            }}
            className="px-6 py-3 bg-brand-500 text-white rounded-lg hover:bg-brand-600 transition-colors font-medium"
          >
            + Add Account
          </button>
        </div>
      </motion.div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
        <motion.div 
          initial={{opacity: 0, y: 10}} 
          animate={{opacity: 1, y: 0}}
          className="card p-5 bg-gradient-to-r from-green-900/50 to-green-800/30 border-green-500/30"
        >
          <h3 className="text-sm text-slate-400 mb-1">Total Assets</h3>
          <p className="text-2xl font-bold text-green-400">{formatCurrency(calculateTotalAssets())}</p>
        </motion.div>
        <motion.div 
          initial={{opacity: 0, y: 10}} 
          animate={{opacity: 1, y: 0}}
          transition={{delay: 0.1}}
          className="card p-5 bg-gradient-to-r from-red-900/50 to-red-800/30 border-red-500/30"
        >
          <h3 className="text-sm text-slate-400 mb-1">Total Liabilities</h3>
          <p className="text-2xl font-bold text-red-400">{formatCurrency(calculateTotalLiabilities())}</p>
        </motion.div>
        <motion.div 
          initial={{opacity: 0, y: 10}} 
          animate={{opacity: 1, y: 0}}
          transition={{delay: 0.2}}
          className="card p-5 bg-gradient-to-r from-blue-900/50 to-blue-800/30 border-blue-500/30"
        >
          <h3 className="text-sm text-slate-400 mb-1">Net Worth</h3>
          <p className="text-2xl font-bold text-blue-400">{formatCurrency(netWorth)}</p>
        </motion.div>
      </div>

      {/* Accounts Grid */}
      {accounts.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {accounts.map((account) => (
            <AccountCard
              key={account.id}
              account={account}
              onEdit={handleEdit}
              onDelete={handleDelete}
            />
          ))}
        </div>
      ) : (
        <div className="text-center py-12 card">
          <p className="text-slate-400 mb-4">No accounts yet. Add your first account to get started!</p>
          <button
            onClick={() => setShowModal(true)}
            className="px-6 py-3 bg-brand-500 text-white rounded-lg hover:bg-brand-600 transition-colors font-medium"
          >
            + Add Account
          </button>
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <motion.div 
            initial={{opacity: 0, scale: 0.95}}
            animate={{opacity: 1, scale: 1}}
            className="bg-slate-900 border border-white/10 rounded-xl p-6 w-full max-w-lg"
          >
            <h2 className="text-xl font-bold text-white mb-6">
              {editingAccount ? 'Edit Account' : 'Add New Account'}
            </h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm text-slate-400 mb-1">Account Name *</label>
                  <input
                    type="text"
                    required
                    value={formData.accountName}
                    onChange={(e) => setFormData({ ...formData, accountName: e.target.value })}
                    className="w-full px-3 py-2 bg-white/5 border border-white/10 rounded-lg text-white focus:ring-2 focus:ring-brand-500"
                  />
                </div>
                <div>
                  <label className="block text-sm text-slate-400 mb-1">Account Type *</label>
                  <select
                    required
                    value={formData.accountType}
                    onChange={(e) => setFormData({ ...formData, accountType: e.target.value as AccountRequest['accountType'] })}
                    className="w-full px-3 py-2 bg-white/5 border border-white/10 rounded-lg text-white focus:ring-2 focus:ring-brand-500"
                  >
                    <option value="CHECKING">Checking</option>
                    <option value="SAVINGS">Savings</option>
                    <option value="CREDIT_CARD">Credit Card</option>
                    <option value="INVESTMENT">Investment</option>
                    <option value="LOAN">Loan</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-sm text-slate-400 mb-1">Institution Name</label>
                <input
                  type="text"
                  value={formData.institutionName}
                  onChange={(e) => setFormData({ ...formData, institutionName: e.target.value })}
                  className="w-full px-3 py-2 bg-white/5 border border-white/10 rounded-lg text-white focus:ring-2 focus:ring-brand-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm text-slate-400 mb-1">Current Balance *</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={formData.currentBalance}
                    onChange={(e) => setFormData({ ...formData, currentBalance: parseFloat(e.target.value) })}
                    className="w-full px-3 py-2 bg-white/5 border border-white/10 rounded-lg text-white focus:ring-2 focus:ring-brand-500"
                  />
                </div>
                <div>
                  <label className="block text-sm text-slate-400 mb-1">Account Color</label>
                  <input
                    type="color"
                    value={formData.accountColor}
                    onChange={(e) => setFormData({ ...formData, accountColor: e.target.value })}
                    className="w-full h-10 bg-white/5 border border-white/10 rounded-lg"
                  />
                </div>
              </div>

              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  checked={formData.isPrimary}
                  onChange={(e) => setFormData({ ...formData, isPrimary: e.target.checked })}
                  className="w-4 h-4 rounded border-white/20 bg-white/5 text-brand-500 focus:ring-brand-500"
                />
                <label className="text-sm text-slate-400">Set as Primary Account</label>
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => {
                    setShowModal(false)
                    resetForm()
                  }}
                  className="px-6 py-2 border border-white/10 rounded-lg text-slate-400 hover:text-white hover:bg-white/5"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-6 py-2 bg-brand-500 text-white rounded-lg hover:bg-brand-600"
                >
                  {editingAccount ? 'Update' : 'Create'}
                </button>
              </div>
            </form>
          </motion.div>
        </div>
      )}
    </div>
  )
}

export default AccountsPage
