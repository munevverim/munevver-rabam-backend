import { apiClient } from './apiClient';
import type { ApiResponse } from '../types/api';
import type { DashboardSummaryResponse } from '../types/dashboard';

export async function getDashboardSummary() {
  const response = await apiClient.get<ApiResponse<DashboardSummaryResponse>>(
    '/dashboard/summary'
  );

  return response.data.data;
}