import React from 'react';
import { AccountResponse } from '../api/accountsApi';

interface AccountCardProps {
  account: AccountResponse;
  onEdit: (account: AccountResponse) => void;
  onDelete: (id: number) => void;
}

const AccountCard: React.FC<AccountCardProps> = ({ account, onEdit, onDelete }) => {
  const getAccountTypeIcon = (type: string) => {
    switch (type) {
      case 'CHECKING':
        return '💳';
      case 'SAVINGS':
        return '🏦';
      case 'CREDIT_CARD':
        return '💰';
      case 'INVESTMENT':
        return '📈';
      case 'LOAN':
        return '🏠';
      case 'OTHER':
        return '📊';
      default:
        return '💼';
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: account.currency || 'USD',
    }).format(amount);
  };

  const getBalanceColor = () => {
    if (account.accountType === 'CREDIT_CARD' || account.accountType === 'LOAN') {
      return account.currentBalance > 0 ? 'text-red-600' : 'text-green-600';
    }
    return account.currentBalance >= 0 ? 'text-green-600' : 'text-red-600';
  };

  return (
    <div
      className="p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow border-l-4"
      style={{ borderLeftColor: account.accountColor || '#3B82F6' }}
    >
      <div className="flex justify-between items-start mb-4">
        <div className="flex items-center gap-3">
          <span className="text-3xl">{getAccountTypeIcon(account.accountType)}</span>
          <div>
            <h3 className="text-lg font-semibold text-gray-900">
              {account.accountName}
              {account.isPrimary && (
                <span className="ml-2 px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded-full">
                  Primary
                </span>
              )}
            </h3>
            {account.institutionName && (
              <p className="text-sm text-gray-600">{account.institutionName}</p>
            )}
          </div>
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => onEdit(account)}
            className="px-3 py-1 text-sm bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Edit
          </button>
          <button
            onClick={() => onDelete(account.id)}
            className="px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600"
          >
            Delete
          </button>
        </div>
      </div>

      <div className="space-y-2">
        <div className="flex justify-between items-center">
          <span className="text-sm text-gray-600">Current Balance:</span>
          <span className={`text-lg font-semibold ${getBalanceColor()}`}>
            {formatCurrency(account.currentBalance)}
          </span>
        </div>

        {account.availableBalance !== null && account.availableBalance !== undefined && (
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-600">Available Balance:</span>
            <span className="text-md font-medium text-gray-800">
              {formatCurrency(account.availableBalance)}
            </span>
          </div>
        )}

        {(account.accountType === 'CREDIT_CARD' || account.accountType === 'LOAN') && (
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-600">Net Balance:</span>
            <span className="text-md font-medium text-gray-800">
              {formatCurrency(account.netBalance)}
            </span>
          </div>
        )}

        {account.accountNumber && (
          <div className="flex justify-between items-center pt-2 border-t">
            <span className="text-sm text-gray-600">Account Number:</span>
            <span className="text-sm font-mono text-gray-800">
              ****{account.accountNumber.slice(-4)}
            </span>
          </div>
        )}

        <div className="flex justify-between items-center text-xs text-gray-500 pt-2">
          <span>Type: {account.accountType.replace('_', ' ')}</span>
          <span className={account.isActive ? 'text-green-600' : 'text-red-600'}>
            {account.isActive ? 'Active' : 'Inactive'}
          </span>
        </div>
      </div>
    </div>
  );
};

export default AccountCard;
