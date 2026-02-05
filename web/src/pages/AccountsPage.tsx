import React, { useState, useEffect } from 'react';
import { accountsApi, AccountRequest, AccountResponse } from '../api/accountsApi';
import AccountCard from '../components/AccountCard';

const AccountsPage: React.FC = () => {
  const [accounts, setAccounts] = useState<AccountResponse[]>([]);
  const [netWorth, setNetWorth] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingAccount, setEditingAccount] = useState<AccountResponse | null>(null);
  const [formData, setFormData] = useState<AccountRequest>({
    accountName: '',
    accountType: 'CHECKING',
    institutionName: '',
    accountNumber: '',
    currentBalance: 0,
    availableBalance: 0,
    currency: 'USD',
    accountColor: '#3B82F6',
    isPrimary: false,
  });

  const userId = 1; // TODO: Get from auth context

  useEffect(() => {
    loadAccounts();
    loadNetWorth();
  }, []);

  const loadAccounts = async () => {
    try {
      setLoading(true);
      const data = await accountsApi.getAccounts(userId);
      setAccounts(data);
    } catch (error) {
      console.error('Error loading accounts:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadNetWorth = async () => {
    try {
      const worth = await accountsApi.getNetWorth(userId);
      setNetWorth(worth);
    } catch (error) {
      console.error('Error loading net worth:', error);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingAccount) {
        await accountsApi.updateAccount(editingAccount.id, formData);
      } else {
        await accountsApi.createAccount(userId, formData);
      }
      loadAccounts();
      loadNetWorth();
      setShowModal(false);
      resetForm();
    } catch (error) {
      console.error('Error saving account:', error);
    }
  };

  const handleEdit = (account: AccountResponse) => {
    setEditingAccount(account);
    setFormData({
      accountName: account.accountName,
      accountType: account.accountType,
      institutionName: account.institutionName || '',
      accountNumber: account.accountNumber || '',
      currentBalance: account.currentBalance,
      availableBalance: account.availableBalance || 0,
      currency: account.currency,
      accountColor: account.accountColor || '#3B82F6',
      isPrimary: account.isPrimary,
    });
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this account?')) {
      try {
        await accountsApi.deleteAccount(id);
        loadAccounts();
        loadNetWorth();
      } catch (error) {
        console.error('Error deleting account:', error);
      }
    }
  };

  const resetForm = () => {
    setEditingAccount(null);
    setFormData({
      accountName: '',
      accountType: 'CHECKING',
      institutionName: '',
      accountNumber: '',
      currentBalance: 0,
      availableBalance: 0,
      currency: 'USD',
      accountColor: '#3B82F6',
      isPrimary: false,
    });
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  const calculateTotalAssets = () => {
    return accounts
      .filter(
        (a) =>
          a.accountType === 'CHECKING' ||
          a.accountType === 'SAVINGS' ||
          a.accountType === 'INVESTMENT' ||
          a.accountType === 'OTHER'
      )
      .reduce((sum, a) => sum + a.currentBalance, 0);
  };

  const calculateTotalLiabilities = () => {
    return accounts
      .filter((a) => a.accountType === 'CREDIT_CARD' || a.accountType === 'LOAN')
      .reduce((sum, a) => sum + a.currentBalance, 0);
  };

  if (loading) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Accounts</h1>
        <button
          onClick={() => {
            resetForm();
            setShowModal(true);
          }}
          className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          + Add Account
        </button>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="p-6 rounded-lg shadow-md bg-gradient-to-r from-green-400 to-green-600 text-white">
          <h3 className="text-lg font-semibold mb-2">Total Assets</h3>
          <p className="text-3xl font-bold">{formatCurrency(calculateTotalAssets())}</p>
        </div>
        <div className="p-6 rounded-lg shadow-md bg-gradient-to-r from-red-400 to-red-600 text-white">
          <h3 className="text-lg font-semibold mb-2">Total Liabilities</h3>
          <p className="text-3xl font-bold">{formatCurrency(calculateTotalLiabilities())}</p>
        </div>
        <div className="p-6 rounded-lg shadow-md bg-gradient-to-r from-blue-400 to-blue-600 text-white">
          <h3 className="text-lg font-semibold mb-2">Net Worth</h3>
          <p className="text-3xl font-bold">{formatCurrency(netWorth)}</p>
        </div>
      </div>

      {/* Accounts Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {accounts.map((account) => (
          <AccountCard
            key={account.id}
            account={account}
            onEdit={handleEdit}
            onDelete={handleDelete}
          />
        ))}
      </div>

      {accounts.length === 0 && (
        <div className="text-center py-12 text-gray-500">
          <p className="text-xl">No accounts yet. Add your first account to get started!</p>
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-8 max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <h2 className="text-2xl font-bold mb-6">
              {editingAccount ? 'Edit Account' : 'Add New Account'}
            </h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Account Name *
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.accountName}
                    onChange={(e) => setFormData({ ...formData, accountName: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Account Type *
                  </label>
                  <select
                    required
                    value={formData.accountType}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        accountType: e.target.value as AccountRequest['accountType'],
                      })
                    }
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
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

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Institution Name
                  </label>
                  <input
                    type="text"
                    value={formData.institutionName}
                    onChange={(e) => setFormData({ ...formData, institutionName: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Account Number
                  </label>
                  <input
                    type="text"
                    value={formData.accountNumber}
                    onChange={(e) => setFormData({ ...formData, accountNumber: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Current Balance *
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={formData.currentBalance}
                    onChange={(e) =>
                      setFormData({ ...formData, currentBalance: parseFloat(e.target.value) })
                    }
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Available Balance
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.availableBalance}
                    onChange={(e) =>
                      setFormData({ ...formData, availableBalance: parseFloat(e.target.value) })
                    }
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Currency</label>
                  <input
                    type="text"
                    value={formData.currency}
                    onChange={(e) => setFormData({ ...formData, currency: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Account Color
                  </label>
                  <input
                    type="color"
                    value={formData.accountColor}
                    onChange={(e) => setFormData({ ...formData, accountColor: e.target.value })}
                    className="w-full h-10 border rounded-lg"
                  />
                </div>
              </div>

              <div className="flex items-center">
                <input
                  type="checkbox"
                  checked={formData.isPrimary}
                  onChange={(e) => setFormData({ ...formData, isPrimary: e.target.checked })}
                  className="w-4 h-4 text-blue-600 rounded focus:ring-2 focus:ring-blue-500"
                />
                <label className="ml-2 text-sm font-medium text-gray-700">
                  Set as Primary Account
                </label>
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => {
                    setShowModal(false);
                    resetForm();
                  }}
                  className="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  {editingAccount ? 'Update' : 'Create'} Account
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AccountsPage;
