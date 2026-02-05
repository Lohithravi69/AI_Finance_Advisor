import React, { useState, useEffect } from 'react';
import { incomeSourcesApi, IncomeSourceRequest, IncomeSourceResponse } from '../api/incomeSourcesApi';
import { accountsApi, AccountResponse } from '../api/accountsApi';
import IncomeSourceCard from '../components/IncomeSourceCard';

const IncomeSourcesPage: React.FC = () => {
  const [incomeSources, setIncomeSources] = useState<IncomeSourceResponse[]>([]);
  const [accounts, setAccounts] = useState<AccountResponse[]>([]);
  const [totalMonthly, setTotalMonthly] = useState<number>(0);
  const [totalAnnual, setTotalAnnual] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingSource, setEditingSource] = useState<IncomeSourceResponse | null>(null);
  const [formData, setFormData] = useState<IncomeSourceRequest>({
    sourceName: '',
    description: '',
    accountId: 0,
    amount: 0,
    frequency: 'MONTHLY',
    incomeType: '',
    startDate: new Date().toISOString().split('T')[0],
    endDate: undefined,
  });

  const userId = 1; // TODO: Get from auth context

  useEffect(() => {
    loadIncomeSources();
    loadAccounts();
    loadTotals();
  }, []);

  const loadIncomeSources = async () => {
    try {
      setLoading(true);
      const data = await incomeSourcesApi.getIncomeSources(userId);
      setIncomeSources(data);
    } catch (error) {
      console.error('Error loading income sources:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadAccounts = async () => {
    try {
      const data = await accountsApi.getActiveAccounts(userId);
      setAccounts(data);
    } catch (error) {
      console.error('Error loading accounts:', error);
    }
  };

  const loadTotals = async () => {
    try {
      const [monthly, annual] = await Promise.all([
        incomeSourcesApi.getTotalMonthlyIncome(userId),
        incomeSourcesApi.getTotalAnnualIncome(userId),
      ]);
      setTotalMonthly(monthly);
      setTotalAnnual(annual);
    } catch (error) {
      console.error('Error loading totals:', error);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingSource) {
        await incomeSourcesApi.updateIncomeSource(editingSource.id, formData);
      } else {
        await incomeSourcesApi.createIncomeSource(userId, formData);
      }
      loadIncomeSources();
      loadTotals();
      setShowModal(false);
      resetForm();
    } catch (error) {
      console.error('Error saving income source:', error);
    }
  };

  const handleEdit = (source: IncomeSourceResponse) => {
    setEditingSource(source);
    setFormData({
      sourceName: source.sourceName,
      description: source.description || '',
      accountId: source.accountId,
      amount: source.amount,
      frequency: source.frequency,
      incomeType: source.incomeType || '',
      startDate: source.startDate,
      endDate: source.endDate,
    });
    setShowModal(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this income source?')) {
      try {
        await incomeSourcesApi.deleteIncomeSource(id);
        loadIncomeSources();
        loadTotals();
      } catch (error) {
        console.error('Error deleting income source:', error);
      }
    }
  };

  const handleMarkReceived = async (id: number) => {
    try {
      await incomeSourcesApi.markAsReceived(id);
      loadIncomeSources();
    } catch (error) {
      console.error('Error marking as received:', error);
    }
  };

  const resetForm = () => {
    setEditingSource(null);
    setFormData({
      sourceName: '',
      description: '',
      accountId: accounts[0]?.id || 0,
      amount: 0,
      frequency: 'MONTHLY',
      incomeType: '',
      startDate: new Date().toISOString().split('T')[0],
      endDate: undefined,
    });
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  if (loading) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Income Sources</h1>
        <button
          onClick={() => {
            resetForm();
            setShowModal(true);
          }}
          className="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
        >
          + Add Income Source
        </button>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div className="p-6 rounded-lg shadow-md bg-gradient-to-r from-green-400 to-green-600 text-white">
          <h3 className="text-lg font-semibold mb-2">Total Monthly Income</h3>
          <p className="text-3xl font-bold">{formatCurrency(totalMonthly)}</p>
        </div>
        <div className="p-6 rounded-lg shadow-md bg-gradient-to-r from-blue-400 to-blue-600 text-white">
          <h3 className="text-lg font-semibold mb-2">Total Annual Income</h3>
          <p className="text-3xl font-bold">{formatCurrency(totalAnnual)}</p>
        </div>
      </div>

      {/* Income Sources Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {incomeSources.map((source) => (
          <IncomeSourceCard
            key={source.id}
            incomeSource={source}
            onEdit={handleEdit}
            onDelete={handleDelete}
            onMarkReceived={handleMarkReceived}
          />
        ))}
      </div>

      {incomeSources.length === 0 && (
        <div className="text-center py-12 text-gray-500">
          <p className="text-xl">No income sources yet. Add your first income source to track your earnings!</p>
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-8 max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <h2 className="text-2xl font-bold mb-6">
              {editingSource ? 'Edit Income Source' : 'Add New Income Source'}
            </h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Source Name *
                </label>
                <input
                  type="text"
                  required
                  value={formData.sourceName}
                  onChange={(e) => setFormData({ ...formData, sourceName: e.target.value })}
                  className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-green-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-green-500"
                  rows={3}
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Account *
                  </label>
                  <select
                    required
                    value={formData.accountId}
                    onChange={(e) => setFormData({ ...formData, accountId: parseInt(e.target.value) })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-green-500"
                  >
                    <option value={0}>Select Account</option>
                    {accounts.map((account) => (
                      <option key={account.id} value={account.id}>
                        {account.accountName} ({account.accountType})
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Income Type
                  </label>
                  <input
                    type="text"
                    value={formData.incomeType}
                    onChange={(e) => setFormData({ ...formData, incomeType: e.target.value })}
                    placeholder="e.g., Salary, Freelance, Rental"
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-green-500"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Amount *</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={formData.amount}
                    onChange={(e) => setFormData({ ...formData, amount: parseFloat(e.target.value) })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-green-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Frequency *
                  </label>
                  <select
                    required
                    value={formData.frequency}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        frequency: e.target.value as IncomeSourceRequest['frequency'],
                      })
                    }
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-green-500"
                  >
                    <option value="WEEKLY">Weekly</option>
                    <option value="BIWEEKLY">Bi-weekly</option>
                    <option value="MONTHLY">Monthly</option>
                    <option value="QUARTERLY">Quarterly</option>
                    <option value="ANNUAL">Annual</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Start Date *
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.startDate}
                    onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-green-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    End Date (Optional)
                  </label>
                  <input
                    type="date"
                    value={formData.endDate || ''}
                    onChange={(e) => setFormData({ ...formData, endDate: e.target.value || undefined })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-green-500"
                  />
                </div>
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
                  className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                >
                  {editingSource ? 'Update' : 'Create'} Income Source
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default IncomeSourcesPage;
