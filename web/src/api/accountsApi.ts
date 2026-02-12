import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

export interface AccountRequest {
  accountName: string;
  accountType: 'CHECKING' | 'SAVINGS' | 'CREDIT_CARD' | 'INVESTMENT' | 'LOAN' | 'OTHER';
  institutionName?: string;
  accountNumber?: string;
  currentBalance: number;
  availableBalance?: number;
  currency: string;
  accountColor?: string;
  isPrimary: boolean;
}

export interface AccountResponse {
  id: number;
  accountName: string;
  accountType: 'CHECKING' | 'SAVINGS' | 'CREDIT_CARD' | 'INVESTMENT' | 'LOAN' | 'OTHER';
  institutionName?: string;
  accountNumber?: string;
  currentBalance: number;
  availableBalance?: number;
  netBalance: number;
  currency: string;
  accountColor?: string;
  isPrimary: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export const accountsApi = {
  async createAccount(userId: number, request: AccountRequest): Promise<AccountResponse> {
    const response = await axios.post(`${API_BASE_URL}/accounts?userId=${userId}`, request);
    return response.data;
  },

  async getAccounts(userId: number): Promise<AccountResponse[]> {
    const response = await axios.get(`${API_BASE_URL}/accounts?userId=${userId}`);
    return response.data;
  },

  async getActiveAccounts(userId: number): Promise<AccountResponse[]> {
    const response = await axios.get(`${API_BASE_URL}/accounts/active?userId=${userId}`);
    return response.data;
  },

  async getPrimaryAccount(userId: number): Promise<AccountResponse> {
    const response = await axios.get(`${API_BASE_URL}/accounts/primary?userId=${userId}`);
    return response.data;
  },

  async getAccountById(id: number): Promise<AccountResponse> {
    const response = await axios.get(`${API_BASE_URL}/accounts/${id}`);
    return response.data;
  },

  async getAccountsByType(userId: number, type: string): Promise<AccountResponse[]> {
    const response = await axios.get(`${API_BASE_URL}/accounts/type/${type}?userId=${userId}`);
    return response.data;
  },

  async updateAccount(id: number, request: AccountRequest): Promise<AccountResponse> {
    const response = await axios.put(`${API_BASE_URL}/accounts/${id}`, request);
    return response.data;
  },

  async deleteAccount(id: number): Promise<void> {
    await axios.delete(`${API_BASE_URL}/accounts/${id}`);
  },

  async getNetWorth(userId: number): Promise<number> {
    const response = await axios.get(`${API_BASE_URL}/accounts/net-worth?userId=${userId}`);
    return response.data;
  },
};
