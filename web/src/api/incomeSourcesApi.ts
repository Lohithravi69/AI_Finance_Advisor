import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export interface IncomeSourceRequest {
  sourceName: string;
  description?: string;
  accountId: number;
  amount: number;
  frequency: 'WEEKLY' | 'BIWEEKLY' | 'MONTHLY' | 'QUARTERLY' | 'ANNUAL';
  incomeType?: string;
  startDate: string;
  endDate?: string;
}

export interface IncomeSourceResponse {
  id: number;
  sourceName: string;
  description?: string;
  accountId: number;
  amount: number;
  frequency: 'WEEKLY' | 'BIWEEKLY' | 'MONTHLY' | 'QUARTERLY' | 'ANNUAL';
  incomeType?: string;
  startDate: string;
  endDate?: string;
  annualAmount: number;
  monthlyAmount: number;
  lastReceived?: string;
  nextExpected?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export const incomeSourcesApi = {
  async createIncomeSource(userId: number, request: IncomeSourceRequest): Promise<IncomeSourceResponse> {
    const response = await axios.post(`${API_BASE_URL}/api/income-sources?userId=${userId}`, request);
    return response.data;
  },

  async getIncomeSources(userId: number): Promise<IncomeSourceResponse[]> {
    const response = await axios.get(`${API_BASE_URL}/api/income-sources?userId=${userId}`);
    return response.data;
  },

  async getActiveIncomeSources(userId: number): Promise<IncomeSourceResponse[]> {
    const response = await axios.get(`${API_BASE_URL}/api/income-sources/active?userId=${userId}`);
    return response.data;
  },

  async getIncomeSourceById(id: number): Promise<IncomeSourceResponse> {
    const response = await axios.get(`${API_BASE_URL}/api/income-sources/${id}`);
    return response.data;
  },

  async updateIncomeSource(id: number, request: IncomeSourceRequest): Promise<IncomeSourceResponse> {
    const response = await axios.put(`${API_BASE_URL}/api/income-sources/${id}`, request);
    return response.data;
  },

  async deleteIncomeSource(id: number): Promise<void> {
    await axios.delete(`${API_BASE_URL}/api/income-sources/${id}`);
  },

  async markAsReceived(id: number): Promise<IncomeSourceResponse> {
    const response = await axios.post(`${API_BASE_URL}/api/income-sources/${id}/received`);
    return response.data;
  },

  async getTotalMonthlyIncome(userId: number): Promise<number> {
    const response = await axios.get(`${API_BASE_URL}/api/income-sources/summary/monthly?userId=${userId}`);
    return response.data;
  },

  async getTotalAnnualIncome(userId: number): Promise<number> {
    const response = await axios.get(`${API_BASE_URL}/api/income-sources/summary/annual?userId=${userId}`);
    return response.data;
  },
};
