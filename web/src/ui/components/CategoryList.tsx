import React, { useState, useMemo } from 'react';
import { motion } from 'framer-motion';
import { CategoryCard } from './CategoryCard';

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

interface CategoryListProps {
  categories: Category[];
  onEdit?: (category: Category) => void;
  onDelete?: (categoryId: number) => void;
  showBudget?: boolean;
  filterActive?: boolean;
}

export const CategoryList: React.FC<CategoryListProps> = ({
  categories,
  onEdit,
  onDelete,
  showBudget = true,
  filterActive = true,
}) => {
  const [sortBy, setSortBy] = useState<'name' | 'spending' | 'budget'>('name');
  const [searchTerm, setSearchTerm] = useState('');

  const filteredAndSorted = useMemo(() => {
    let filtered = filterActive
      ? categories.filter((c) => c.isActive)
      : categories;

    // Search filter
    if (searchTerm) {
      filtered = filtered.filter((c) =>
        c.name.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Sort
    return filtered.sort((a, b) => {
      switch (sortBy) {
        case 'spending':
          return (b.spendingThisMonth || 0) - (a.spendingThisMonth || 0);
        case 'budget':
          return (b.monthlyBudget || 0) - (a.monthlyBudget || 0);
        case 'name':
        default:
          return a.name.localeCompare(b.name);
      }
    });
  }, [categories, sortBy, searchTerm, filterActive]);

  const totalBudget = useMemo(
    () =>
      filteredAndSorted.reduce((sum, c) => sum + (c.monthlyBudget || 0), 0),
    [filteredAndSorted]
  );

  const totalSpending = useMemo(
    () =>
      filteredAndSorted.reduce((sum, c) => sum + (c.spendingThisMonth || 0), 0),
    [filteredAndSorted]
  );

  const overBudgetCount = useMemo(
    () =>
      filteredAndSorted.filter(
        (c) =>
          c.monthlyBudget &&
          c.spendingThisMonth &&
          c.spendingThisMonth > c.monthlyBudget
      ).length,
    [filteredAndSorted]
  );

  return (
    <div className="space-y-6">
      {/* Summary Card */}
      {showBudget && totalBudget > 0 && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-lg p-4 border border-blue-200"
        >
          <h3 className="text-sm font-semibold text-gray-900 mb-3">
            Budget Summary
          </h3>
          <div className="grid grid-cols-3 gap-4">
            <div>
              <p className="text-xs text-gray-600">Total Budget</p>
              <p className="text-xl font-bold text-gray-900">
                ${totalBudget.toFixed(2)}
              </p>
            </div>
            <div>
              <p className="text-xs text-gray-600">Total Spent</p>
              <p className="text-xl font-bold text-gray-900">
                ${totalSpending.toFixed(2)}
              </p>
            </div>
            <div>
              <p className="text-xs text-gray-600">Percentage</p>
              <p className="text-xl font-bold text-gray-900">
                {totalBudget > 0 ? ((totalSpending / totalBudget) * 100).toFixed(0) : 0}%
              </p>
            </div>
          </div>
          {overBudgetCount > 0 && (
            <div className="mt-3 p-2 bg-red-50 rounded border border-red-200 text-xs text-red-700">
              ⚠️ {overBudgetCount} categor{overBudgetCount !== 1 ? 'ies' : 'y'} over budget
            </div>
          )}
        </motion.div>
      )}

      {/* Controls */}
      <div className="flex flex-col sm:flex-row gap-3">
        <input
          type="text"
          placeholder="Search categories..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
        />
        <select
          value={sortBy}
          onChange={(e) => setSortBy(e.target.value as any)}
          className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm bg-white"
        >
          <option value="name">Sort by Name</option>
          <option value="spending">Sort by Spending</option>
          <option value="budget">Sort by Budget</option>
        </select>
      </div>

      {/* Categories Grid */}
      {filteredAndSorted.length > 0 ? (
        <motion.div
          layout
          className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4"
        >
          {filteredAndSorted.map((category, index) => (
            <motion.div
              key={category.id}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.05 }}
            >
              <CategoryCard
                category={category}
                onEdit={onEdit}
                onDelete={onDelete}
                showBudget={showBudget}
              />
            </motion.div>
          ))}
        </motion.div>
      ) : (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="text-center py-12"
        >
          <p className="text-gray-500 text-lg">
            {searchTerm ? 'No categories match your search' : 'No categories yet'}
          </p>
          <p className="text-gray-400 text-sm mt-1">
            Create a category to get started
          </p>
        </motion.div>
      )}
    </div>
  );
};
