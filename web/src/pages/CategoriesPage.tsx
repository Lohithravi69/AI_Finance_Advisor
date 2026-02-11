import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useAuthStore } from '../stores/authStore';
import { CategoryList } from '../ui/components/CategoryList';
import { CreateCategoryForm } from '../ui/components/CreateCategoryForm';
import { ExpenseBreakdown } from '../ui/components/ExpenseBreakdown';

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

interface SpendingAnalysis {
  categoryId: number;
  categoryName: string;
  icon: string;
  color: string;
  totalSpent: number;
  monthlyBudget?: number;
  percentageOfTotal: number;
  percentageOfBudget?: number;
  transactionCount: number;
  averageTransaction: number;
  trendIndicator: 'UP' | 'DOWN' | 'STABLE';
  isAnomalous: boolean;
}

export const CategoriesPage: React.FC = () => {
  const { user } = useAuthStore();
  const [categories, setCategories] = useState<Category[]>([]);
  const [spendingAnalysis, setSpendingAnalysis] = useState<SpendingAnalysis[]>([]);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [activeTab, setActiveTab] = useState<'categories' | 'analysis'>('categories');
  const [loading, setLoading] = useState(true);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);

  // Mock data - Replace with API calls
  const mockCategories: Category[] = [
    {
      id: 1,
      name: 'Groceries',
      icon: '🛒',
      color: '#FF6B6B',
      description: 'Food and grocery shopping',
      monthlyBudget: 400,
      spendingThisMonth: 320,
      percentageSpent: 80,
      isActive: true,
    },
    {
      id: 2,
      name: 'Transportation',
      icon: '🚗',
      color: '#4ECDC4',
      description: 'Gas, public transport, rides',
      monthlyBudget: 200,
      spendingThisMonth: 190,
      percentageSpent: 95,
      isActive: true,
    },
    {
      id: 3,
      name: 'Entertainment',
      icon: '🎬',
      color: '#95E1D3',
      description: 'Movies, games, hobbies',
      monthlyBudget: 150,
      spendingThisMonth: 175,
      percentageSpent: 116.7,
      isActive: true,
    },
    {
      id: 4,
      name: 'Utilities',
      icon: '💡',
      color: '#F7DC6F',
      description: 'Electricity, water, internet',
      monthlyBudget: 120,
      spendingThisMonth: 120,
      percentageSpent: 100,
      isActive: true,
    },
    {
      id: 5,
      name: 'Dining Out',
      icon: '🍔',
      color: '#F8B739',
      description: 'Restaurants and cafes',
      monthlyBudget: 250,
      spendingThisMonth: 280,
      percentageSpent: 112,
      isActive: true,
    },
  ];

  const mockAnalysis: SpendingAnalysis[] = [
    {
      categoryId: 1,
      categoryName: 'Groceries',
      icon: '🛒',
      color: '#FF6B6B',
      totalSpent: 320,
      monthlyBudget: 400,
      percentageOfTotal: 28.6,
      percentageOfBudget: 80,
      transactionCount: 12,
      averageTransaction: 26.67,
      trendIndicator: 'STABLE',
      isAnomalous: false,
    },
    {
      categoryId: 3,
      categoryName: 'Entertainment',
      icon: '🎬',
      color: '#95E1D3',
      totalSpent: 175,
      monthlyBudget: 150,
      percentageOfTotal: 15.6,
      percentageOfBudget: 116.7,
      transactionCount: 5,
      averageTransaction: 35,
      trendIndicator: 'UP',
      isAnomalous: true,
    },
    {
      categoryId: 5,
      categoryName: 'Dining Out',
      icon: '🍔',
      color: '#F8B739',
      totalSpent: 280,
      monthlyBudget: 250,
      percentageOfTotal: 25,
      percentageOfBudget: 112,
      transactionCount: 18,
      averageTransaction: 15.56,
      trendIndicator: 'UP',
      isAnomalous: false,
    },
    {
      categoryId: 2,
      categoryName: 'Transportation',
      icon: '🚗',
      color: '#4ECDC4',
      totalSpent: 190,
      monthlyBudget: 200,
      percentageOfTotal: 17,
      percentageOfBudget: 95,
      transactionCount: 8,
      averageTransaction: 23.75,
      trendIndicator: 'STABLE',
      isAnomalous: false,
    },
    {
      categoryId: 4,
      categoryName: 'Utilities',
      icon: '💡',
      color: '#F7DC6F',
      totalSpent: 120,
      monthlyBudget: 120,
      percentageOfTotal: 10.7,
      percentageOfBudget: 100,
      transactionCount: 2,
      averageTransaction: 60,
      trendIndicator: 'STABLE',
      isAnomalous: false,
    },
  ];

  useEffect(() => {
    // Simulate API call
    setLoading(true);
    setTimeout(() => {
      setCategories(mockCategories);
      setSpendingAnalysis(mockAnalysis);
      setLoading(false);
    }, 500);
  }, [user]);

  const handleCreateCategory = (categoryData: any) => {
    const newCategory: Category = {
      id: Math.max(...categories.map((c) => c.id), 0) + 1,
      ...categoryData,
      spendingThisMonth: 0,
      percentageSpent: 0,
      isActive: true,
    };
    setCategories([...categories, newCategory]);
    setShowCreateForm(false);
  };

  const handleEditCategory = (updatedCategory: any) => {
    if (editingCategory) {
      setCategories(
        categories.map((c) =>
          c.id === editingCategory.id ? { ...c, ...updatedCategory } : c
        )
      );
      setEditingCategory(null);
    }
  };

  const handleDeleteCategory = (categoryId: number) => {
    if (confirm('Are you sure you want to delete this category?')) {
      setCategories(categories.filter((c) => c.id !== categoryId));
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <div className="flex items-center justify-between mb-4">
            <div>
              <h1 className="text-4xl font-bold text-gray-900">💰 Categories</h1>
              <p className="text-gray-600 mt-1">
                Manage spending categories and analyze expenses
              </p>
            </div>
            <button
              onClick={() => setShowCreateForm(true)}
              className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-semibold flex items-center gap-2"
            >
              + Create Category
            </button>
          </div>
        </motion.div>

        {/* Tabs */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="flex gap-4 mb-6 border-b border-gray-200"
        >
          {(['categories', 'analysis'] as const).map((tab) => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`px-4 py-3 font-medium transition-colors ${
                activeTab === tab
                  ? 'text-blue-600 border-b-2 border-blue-600'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              {tab === 'categories' ? '📋 Categories' : '📊 Spending Analysis'}
            </button>
          ))}
        </motion.div>

        {/* Content */}
        <AnimatePresence mode="wait">
          {activeTab === 'categories' ? (
            <motion.div
              key="categories"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
            >
              <CategoryList
                categories={categories}
                onEdit={(category) => {
                  setEditingCategory(category);
                  setShowCreateForm(true);
                }}
                onDelete={handleDeleteCategory}
                showBudget={true}
              />
            </motion.div>
          ) : (
            <motion.div
              key="analysis"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
            >
              <ExpenseBreakdown
                data={spendingAnalysis}
                showTrend={true}
                showAnomalies={true}
              />
            </motion.div>
          )}
        </AnimatePresence>

        {/* Create/Edit Form Modal */}
        <AnimatePresence>
          {showCreateForm && (
            <CreateCategoryForm
              onSubmit={
                editingCategory ? handleEditCategory : handleCreateCategory
              }
              onClose={() => {
                setShowCreateForm(false);
                setEditingCategory(null);
              }}
            />
          )}
        </AnimatePresence>
      </div>
    </div>
  );
};
