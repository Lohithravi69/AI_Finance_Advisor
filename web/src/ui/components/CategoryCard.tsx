import React, { useState } from 'react';
import { motion } from 'framer-motion';

interface Category {
  id: number;
  name: string;
  icon: string;
  color: string;
  description: string;
  monthlyBudget?: number;
  spendingThisMonth?: number;
  percentageSpent?: number;
  isActive: boolean;
}

interface CategoryCardProps {
  category: Category;
  onEdit?: (category: Category) => void;
  onDelete?: (categoryId: number) => void;
  showBudget?: boolean;
}

export const CategoryCard: React.FC<CategoryCardProps> = ({
  category,
  onEdit,
  onDelete,
  showBudget = true,
}) => {
  const [showMenu, setShowMenu] = useState(false);

  const percentageSpent = category.percentageSpent || 0;
  const getStatusColor = () => {
    if (percentageSpent >= 100) return '#EF4444';
    if (percentageSpent >= 80) return '#F59E0B';
    return '#10B981';
  };

  return (
    <motion.div
      whileHover={{ y: -4 }}
      className="bg-white rounded-lg shadow-md p-4 hover:shadow-lg transition-shadow"
    >
      {/* Header */}
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <span className="text-3xl">{category.icon}</span>
          <div>
            <h3 className="font-semibold text-gray-900">{category.name}</h3>
            <p className="text-xs text-gray-500">{category.description}</p>
          </div>
        </div>
        <div className="relative">
          <button
            onClick={() => setShowMenu(!showMenu)}
            className="text-gray-400 hover:text-gray-600 p-1"
          >
            ⋮
          </button>
          {showMenu && (
            <div className="absolute right-0 mt-1 w-32 bg-white border border-gray-200 rounded-md shadow-lg z-10">
              {onEdit && (
                <button
                  onClick={() => {
                    onEdit(category);
                    setShowMenu(false);
                  }}
                  className="block w-full text-left px-3 py-2 text-sm text-gray-700 hover:bg-gray-100"
                >
                  Edit
                </button>
              )}
              {onDelete && (
                <button
                  onClick={() => {
                    onDelete(category.id);
                    setShowMenu(false);
                  }}
                  className="block w-full text-left px-3 py-2 text-sm text-red-600 hover:bg-red-50"
                >
                  Delete
                </button>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Budget Progress */}
      {showBudget && category.monthlyBudget && category.spendingThisMonth !== undefined && (
        <>
          <div className="mb-3">
            <div className="flex justify-between text-xs mb-1">
              <span className="text-gray-600">
                ${category.spendingThisMonth.toFixed(2)} of ${category.monthlyBudget.toFixed(2)}
              </span>
              <span className="text-gray-600">{percentageSpent.toFixed(0)}%</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <motion.div
                initial={{ width: 0 }}
                animate={{ width: `${Math.min(percentageSpent, 100)}%` }}
                className="h-2 rounded-full"
                style={{ backgroundColor: getStatusColor() }}
              />
            </div>
          </div>

          {/* Status */}
          <div className="flex items-center gap-1 text-xs">
            {percentageSpent >= 100 && <span>⚠️ Over Budget</span>}
            {percentageSpent >= 80 && percentageSpent < 100 && <span>🔔 Warning</span>}
            {percentageSpent < 80 && <span>✓ On Track</span>}
          </div>
        </>
      )}

      {/* Category Color Indicator */}
      <div className="mt-3 pt-3 border-t border-gray-100">
        <div
          className="w-full h-1 rounded-full"
          style={{ backgroundColor: category.color }}
        />
      </div>
    </motion.div>
  );
};
