import React, { useState } from 'react';
import { BudgetList } from '../components/BudgetList';
import { CreateBudgetForm } from '../components/CreateBudgetForm';

export const BudgetsPage: React.FC = () => {
  const [showCreateForm, setShowCreateForm] = useState(false);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">Budget Management</h1>
          <p className="text-gray-600">Track and manage your spending across different categories</p>
        </div>

        {showCreateForm && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <CreateBudgetForm
              onClose={() => setShowCreateForm(false)}
              onSuccess={() => setShowCreateForm(false)}
            />
          </div>
        )}

        <BudgetList />
      </div>
    </div>
  );
};

export default BudgetsPage;
