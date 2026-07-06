import { apiClient } from './apiClient';
import type { ApiResponse, PageResponse } from '../types/api';
import type { AuditLogResponse } from '../types/audit';

export async function getAuditLogs(page: number, size: number) {
  const response = await apiClient.get<ApiResponse<PageResponse<AuditLogResponse>>>(
    '/audit-logs',
    {
      params: {
        page,
        size,
        sort: 'createdAt,desc'
      }
    }
  );

  return response.data.data;
}