import React from 'react';
import { motion } from 'framer-motion';

interface BudgetCardProps {
  id: number;
  name: string;
  category: string;
  monthlyLimit: number;
  spentAmount: number;
  percentageSpent: number;
  alertThreshold: number;
  onEdit?: (id: number) => void;
  onDelete?: (id: number) => void;
  onView?: (id: number) => void;
}

export const BudgetCard: React.FC<BudgetCardProps> = ({
  id,
  name,
  category,
  monthlyLimit,
  spentAmount,
  percentageSpent,
  alertThreshold,
  onEdit,
  onDelete,
  onView,
}) => {
  const getProgressColor = () => {
    if (percentageSpent >= 100) return 'bg-red-500';
    if (percentageSpent >= alertThreshold) return 'bg-yellow-500';
    return 'bg-green-500';
  };

  const getStatusText = () => {
    if (percentageSpent >= 100) return '⚠️ Over Budget';
    if (percentageSpent >= alertThreshold) return '🔔 Warning';
    return '✓ On Track';
  };

  const remaining = monthlyLimit - spentAmount;

  return (
    <motion.div
      whileHover={{ scale: 1.02 }}
      className="bg-white rounded-lg shadow-md p-6 border-l-4 border-blue-500"
    >
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-lg font-bold text-gray-900">{name}</h3>
          <p className="text-sm text-gray-600">Category: {category}</p>
        </div>
        <div className="flex gap-2">
          {onView && (
            <button
              onClick={() => onView(id)}
              className="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded text-sm"
            >
              View
            </button>
          )}
          {onEdit && (
            <button
              onClick={() => onEdit(id)}
              className="bg-green-600 hover:bg-green-700 text-white px-3 py-1 rounded text-sm"
            >
              Edit
            </button>
          )}
          {onDelete && (
            <button
              onClick={() => onDelete(id)}
              className="bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded text-sm"
            >
              Delete
            </button>
          )}
        </div>
      </div>

      {/* Progress Bar */}
      <div className="mb-4">
        <div className="flex justify-between mb-2">
          <span className="text-sm font-medium text-gray-700">
            ${spentAmount.toFixed(2)} / ${monthlyLimit.toFixed(2)}
          </span>
          <span className="text-sm font-medium">{getStatusText()}</span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div
            className={`${getProgressColor()} h-2 rounded-full transition-all duration-300`}
            style={{
              width: `${Math.min(percentageSpent, 100)}%`,
            }}
          />
        </div>
        <p className="text-xs text-gray-500 mt-2">
          {percentageSpent.toFixed(1)}% spent • ${remaining.toFixed(2)} remaining
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-4 pt-4 border-t border-gray-200">
        <div>
          <p className="text-xs text-gray-600">Spent</p>
          <p className="text-lg font-bold text-gray-900">${spentAmount.toFixed(2)}</p>
        </div>
        <div>
          <p className="text-xs text-gray-600">Remaining</p>
          <p className="text-lg font-bold text-green-600">${remaining.toFixed(2)}</p>
        </div>
        <div>
          <p className="text-xs text-gray-600">Alert at</p>
          <p className="text-lg font-bold text-blue-600">{alertThreshold}%</p>
        </div>
      </div>
    </motion.div>
  );
};
