import React from 'react';
import { IncomeSourceResponse } from '../api/incomeSourcesApi';

interface IncomeSourceCardProps {
  incomeSource: IncomeSourceResponse;
  onEdit: (incomeSource: IncomeSourceResponse) => void;
  onDelete: (id: number) => void;
  onMarkReceived: (id: number) => void;
}

const IncomeSourceCard: React.FC<IncomeSourceCardProps> = ({
  incomeSource,
  onEdit,
  onDelete,
  onMarkReceived,
}) => {
  const getFrequencyBadge = (frequency: string) => {
    const colors: Record<string, string> = {
      WEEKLY: 'bg-purple-100 text-purple-800',
      BIWEEKLY: 'bg-blue-100 text-blue-800',
      MONTHLY: 'bg-green-100 text-green-800',
      QUARTERLY: 'bg-yellow-100 text-yellow-800',
      ANNUAL: 'bg-red-100 text-red-800',
    };
    return colors[frequency] || 'bg-gray-100 text-gray-800';
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const isOverdue = () => {
    if (!incomeSource.nextExpected) return false;
    return new Date(incomeSource.nextExpected) < new Date();
  };

  return (
    <div className="p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow border-l-4 border-green-500">
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900">
            {incomeSource.sourceName}
            <span className={`ml-2 px-2 py-1 text-xs rounded-full ${getFrequencyBadge(incomeSource.frequency)}`}>
              {incomeSource.frequency}
            </span>
            {!incomeSource.isActive && (
              <span className="ml-2 px-2 py-1 text-xs bg-gray-100 text-gray-800 rounded-full">
                Inactive
              </span>
            )}
          </h3>
          {incomeSource.description && (
            <p className="text-sm text-gray-600 mt-1">{incomeSource.description}</p>
          )}
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => onEdit(incomeSource)}
            className="px-3 py-1 text-sm bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Edit
          </button>
          <button
            onClick={() => onDelete(incomeSource.id)}
            className="px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600"
          >
            Delete
          </button>
        </div>
      </div>

      <div className="space-y-2">
        <div className="flex justify-between items-center">
          <span className="text-sm text-gray-600">Amount:</span>
          <span className="text-lg font-semibold text-green-600">
            {formatCurrency(incomeSource.amount)}
          </span>
        </div>

        <div className="flex justify-between items-center">
          <span className="text-sm text-gray-600">Monthly Equivalent:</span>
          <span className="text-md font-medium text-gray-800">
            {formatCurrency(incomeSource.monthlyAmount)}
          </span>
        </div>

        <div className="flex justify-between items-center">
          <span className="text-sm text-gray-600">Annual Equivalent:</span>
          <span className="text-md font-medium text-gray-800">
            {formatCurrency(incomeSource.annualAmount)}
          </span>
        </div>

        {incomeSource.incomeType && (
          <div className="flex justify-between items-center pt-2 border-t">
            <span className="text-sm text-gray-600">Type:</span>
            <span className="text-sm text-gray-800">{incomeSource.incomeType}</span>
          </div>
        )}

        <div className="flex justify-between items-center">
          <span className="text-sm text-gray-600">Last Received:</span>
          <span className="text-sm text-gray-800">{formatDate(incomeSource.lastReceived)}</span>
        </div>

        <div className="flex justify-between items-center">
          <span className="text-sm text-gray-600">Next Expected:</span>
          <span className={`text-sm font-medium ${isOverdue() ? 'text-red-600' : 'text-gray-800'}`}>
            {formatDate(incomeSource.nextExpected)}
            {isOverdue() && ' (Overdue)'}
          </span>
        </div>

        <div className="flex justify-between items-center text-xs text-gray-500 pt-2">
          <span>
            {formatDate(incomeSource.startDate)} - {formatDate(incomeSource.endDate)}
          </span>
        </div>

        {incomeSource.isActive && (
          <div className="pt-3">
            <button
              onClick={() => onMarkReceived(incomeSource.id)}
              className="w-full px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition-colors"
              disabled={!isOverdue() && incomeSource.lastReceived !== null}
            >
              Mark as Received
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default IncomeSourceCard;
