import React, { useState } from 'react';
import { BudgetCard } from './BudgetCard';

interface Budget {
  id: number;
  name: string;
  category: string;
  monthlyLimit: number;
  spentAmount: number;
  percentageSpent: number;
  alertThreshold: number;
  createdAt: string;
}

export const BudgetList: React.FC = () => {
  // Mock data - in production, fetch from API
  const [budgets] = useState<Budget[]>([
    {
      id: 1,
      name: 'Groceries',
      category: 'Food',
      monthlyLimit: 500,
      spentAmount: 350,
      percentageSpent: 70,
      alertThreshold: 80,
      createdAt: new Date().toISOString(),
    },
    {
      id: 2,
      name: 'Transportation',
      category: 'Transport',
      monthlyLimit: 300,
      spentAmount: 280,
      percentageSpent: 93.33,
      alertThreshold: 80,
      createdAt: new Date().toISOString(),
    },
    {
      id: 3,
      name: 'Entertainment',
      category: 'Entertainment',
      monthlyLimit: 200,
      spentAmount: 220,
      percentageSpent: 110,
      alertThreshold: 80,
      createdAt: new Date().toISOString(),
    },
  ]);

  const handleEdit = (id: number) => {
    console.log('Edit budget:', id);
    // TODO: Open edit modal
  };

  const handleDelete = (id: number) => {
    console.log('Delete budget:', id);
    // TODO: Implement delete
  };

  const handleView = (id: number) => {
    console.log('View budget:', id);
    // TODO: Navigate to detail view
  };

  const totalLimit = budgets.reduce((sum, b) => sum + b.monthlyLimit, 0);
  const totalSpent = budgets.reduce((sum, b) => sum + b.spentAmount, 0);
  const totalPercentage = (totalSpent / totalLimit) * 100;

  return (
    <div className="space-y-6">
      {/* Summary Card */}
      <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg p-6 border border-blue-200">
        <h2 className="text-2xl font-bold text-gray-900 mb-4">Budget Overview</h2>
        <div className="grid grid-cols-3 gap-4">
          <div>
            <p className="text-gray-600">Total Budget</p>
            <p className="text-3xl font-bold text-gray-900">${totalLimit.toFixed(2)}</p>
          </div>
          <div>
            <p className="text-gray-600">Total Spent</p>
            <p className="text-3xl font-bold text-blue-600">${totalSpent.toFixed(2)}</p>
          </div>
          <div>
            <p className="text-gray-600">Overall Usage</p>
            <p className="text-3xl font-bold text-indigo-600">{totalPercentage.toFixed(1)}%</p>
          </div>
        </div>
        <div className="mt-4">
          <div className="w-full bg-gray-200 rounded-full h-3">
            <div
              className="bg-gradient-to-r from-blue-500 to-indigo-600 h-3 rounded-full transition-all duration-300"
              style={{
                width: `${Math.min(totalPercentage, 100)}%`,
              }}
            />
          </div>
        </div>
      </div>

      {/* Budgets Grid */}
      <div>
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-xl font-bold text-gray-900">Active Budgets</h3>
          <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
            + Add Budget
          </button>
        </div>

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
      </div>

      {/* Empty State */}
      {budgets.length === 0 && (
        <div className="text-center py-12 bg-gray-50 rounded-lg">
          <p className="text-gray-600 mb-4">No budgets created yet</p>
          <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-6 rounded">
            Create Your First Budget
          </button>
        </div>
      )}
    </div>
  );
};
