import React, { useState } from 'react';
import { motion } from 'framer-motion';

interface CreateCategoryFormProps {
  onSubmit: (category: CategoryFormData) => void;
  onClose: () => void;
  predefinedIcons?: string[];
}

interface CategoryFormData {
  name: string;
  description: string;
  icon: string;
  color: string;
  monthlyBudget?: number;
}

const DEFAULT_ICONS = ['🛒', '🚗', '🎬', '💡', '🍔', '⚕️', '🛍️', '📱', '🏦', '📌'];
const DEFAULT_COLORS = [
  '#FF6B6B', '#4ECDC4', '#95E1D3', '#F7DC6F', '#F8B739',
  '#BB8FCE', '#85C1E2', '#A9DFBF', '#F1948A', '#D5D8DC'
];

export const CreateCategoryForm: React.FC<CreateCategoryFormProps> = ({
  onSubmit,
  onClose,
  predefinedIcons = DEFAULT_ICONS,
}) => {
  const [formData, setFormData] = useState<CategoryFormData>({
    name: '',
    description: '',
    icon: '📌',
    color: '#95E1D3',
    monthlyBudget: undefined,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    if (!formData.name.trim()) newErrors.name = 'Category name is required';
    if (formData.monthlyBudget !== undefined && formData.monthlyBudget < 0) {
      newErrors.monthlyBudget = 'Budget must be positive';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validateForm()) {
      onSubmit(formData);
      toast.success('Category created!');
      onClose();
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      onClick={onClose}
    >
      <motion.div
        initial={{ scale: 0.95, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.95, opacity: 0 }}
        className="bg-white rounded-lg shadow-xl max-w-md w-full p-6"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-bold text-gray-900">Create Category</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 text-2xl"
          >
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Category Name */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Name *
            </label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                errors.name ? 'border-red-500' : 'border-gray-300'
              }`}
              placeholder="e.g., Dining Out"
            />
            {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name}</p>}
          </div>

          {/* Description */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Description
            </label>
            <textarea
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
              placeholder="Optional description..."
              rows={2}
            />
          </div>

          {/* Icon Selection */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Icon
            </label>
            <div className="grid grid-cols-6 gap-2">
              {predefinedIcons.map((icon) => (
                <button
                  key={icon}
                  type="button"
                  onClick={() => setFormData({ ...formData, icon })}
                  className={`p-2 text-2xl rounded-lg border-2 transition-all ${
                    formData.icon === icon
                      ? 'border-blue-500 bg-blue-50'
                      : 'border-gray-200 hover:border-gray-300'
                  }`}
                >
                  {icon}
                </button>
              ))}
            </div>
          </div>

          {/* Color Selection */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Color
            </label>
            <div className="grid grid-cols-5 gap-2">
              {DEFAULT_COLORS.map((color) => (
                <button
                  key={color}
                  type="button"
                  onClick={() => setFormData({ ...formData, color })}
                  className={`p-3 rounded-lg border-2 transition-all ${
                    formData.color === color ? 'border-gray-900 scale-110' : 'border-gray-300'
                  }`}
                  style={{ backgroundColor: color }}
                />
              ))}
            </div>
          </div>

          {/* Monthly Budget */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Monthly Budget (Optional)
            </label>
            <input
              type="number"
              value={formData.monthlyBudget || ''}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  monthlyBudget: e.target.value ? parseFloat(e.target.value) : undefined,
                })
              }
              className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                errors.monthlyBudget ? 'border-red-500' : 'border-gray-300'
              }`}
              placeholder="0.00"
              min="0"
              step="0.01"
            />
            {errors.monthlyBudget && (
              <p className="text-red-500 text-xs mt-1">{errors.monthlyBudget}</p>
            )}
          </div>

          {/* Buttons */}
          <div className="flex gap-2 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
            >
              Create
            </button>
          </div>
        </form>
      </motion.div>
    </motion.div>
  );
};

// Dummy toast function (replace with actual toast library)
const toast = {
  success: (msg: string) => console.log(msg),
  error: (msg: string) => console.error(msg),
};
