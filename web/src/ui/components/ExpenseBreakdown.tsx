import React, { useMemo } from 'react';
import { motion } from 'framer-motion';

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

interface ExpenseBreakdownProps {
  data: SpendingAnalysis[];
  title?: string;
  showTrend?: boolean;
  showAnomalies?: boolean;
}

export const ExpenseBreakdown: React.FC<ExpenseBreakdownProps> = ({
  data,
  title = 'Expense Breakdown',
  showTrend = true,
  showAnomalies = true,
}) => {
  const totalSpending = useMemo(
    () => data.reduce((sum, item) => sum + item.totalSpent, 0),
    [data]
  );

  const anomalousCategories = useMemo(
    () => data.filter((item) => item.isAnomalous),
    [data]
  );

  const getTrendEmoji = (trend: string) => {
    switch (trend) {
      case 'UP':
        return '📈';
      case 'DOWN':
        return '📉';
      default:
        return '➡️';
    }
  };

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold text-gray-900">{title}</h2>

      {/* Anomalies Alert */}
      {showAnomalies && anomalousCategories.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-amber-50 border border-amber-200 rounded-lg p-4"
        >
          <p className="text-sm font-semibold text-amber-900">
            🔔 Anomaly Detected in{' '}
            {anomalousCategories.length > 1
              ? `${anomalousCategories.length} categories`
              : '1 category'}
          </p>
          <p className="text-sm text-amber-700 mt-1">
            {anomalousCategories.map((c) => c.categoryName).join(', ')}{' '}
            have unusual spending patterns
          </p>
        </motion.div>
      )}

      {/* Breakdown List */}
      <div className="space-y-2">
        {data
          .sort((a, b) => b.totalSpent - a.totalSpent)
          .map((item, index) => (
            <motion.div
              key={item.categoryId}
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.05 }}
              className={`p-3 rounded-lg border ${
                item.isAnomalous
                  ? 'bg-red-50 border-red-200'
                  : 'bg-gray-50 border-gray-200'
              }`}
            >
              <div className="flex items-start justify-between mb-2">
                <div className="flex items-center gap-2">
                  <span className="text-2xl">{item.icon}</span>
                  <div>
                    <p className="font-semibold text-gray-900">
                      {item.categoryName}
                    </p>
                    <p className="text-xs text-gray-600">
                      {item.transactionCount} transactions
                    </p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="font-bold text-gray-900">
                    ${item.totalSpent.toFixed(2)}
                  </p>
                  <p className="text-xs text-gray-600">
                    {item.percentageOfTotal.toFixed(1)}% of total
                  </p>
                </div>
              </div>

              {/* Progress Bar */}
              <div className="mb-2">
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <motion.div
                    initial={{ width: 0 }}
                    animate={{
                      width: `${Math.min((item.totalSpent / totalSpending) * 100, 100)}%`,
                    }}
                    className="h-2 rounded-full"
                    style={{ backgroundColor: item.color }}
                  />
                </div>
              </div>

              {/* Stats */}
              <div className="grid grid-cols-2 gap-2 text-xs">
                <div>
                  <span className="text-gray-600">Avg Transaction:</span>
                  <p className="font-semibold text-gray-900">
                    ${item.averageTransaction.toFixed(2)}
                  </p>
                </div>
                {item.percentageOfBudget !== undefined && item.monthlyBudget && (
                  <div>
                    <span className="text-gray-600">Budget Usage:</span>
                    <p className="font-semibold text-gray-900">
                      {item.percentageOfBudget.toFixed(0)}% of ${item.monthlyBudget.toFixed(2)}
                    </p>
                  </div>
                )}
              </div>

              {/* Trend & Anomaly Indicators */}
              {showTrend && (
                <div className="mt-2 pt-2 border-t border-gray-300 flex items-center justify-between text-xs">
                  <span className="text-gray-600">
                    Trend: {getTrendEmoji(item.trendIndicator)} {item.trendIndicator}
                  </span>
                  {item.isAnomalous && (
                    <span className="text-red-600 font-semibold">⚠️ Anomalous</span>
                  )}
                </div>
              )}
            </motion.div>
          ))}
      </div>

      {/* Summary Stats */}
      {totalSpending > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-blue-50 rounded-lg p-4 border border-blue-200"
        >
          <div className="grid grid-cols-3 gap-4 text-center">
            <div>
              <p className="text-xs text-gray-600">Total Spending</p>
              <p className="text-xl font-bold text-gray-900">
                ${totalSpending.toFixed(2)}
              </p>
            </div>
            <div>
              <p className="text-xs text-gray-600">Categories</p>
              <p className="text-xl font-bold text-gray-900">{data.length}</p>
            </div>
            <div>
              <p className="text-xs text-gray-600">Avg per Category</p>
              <p className="text-xl font-bold text-gray-900">
                ${(totalSpending / data.length).toFixed(2)}
              </p>
            </div>
          </div>
        </motion.div>
      )}
    </div>
  );
};
