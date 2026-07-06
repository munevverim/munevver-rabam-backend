import { apiClient } from './apiClient';
import type { ApiResponse, SpringPage } from '../types/api';
import type {
  ServiceRequest,
  ServiceResponse,
  ServiceStatus,
  ServiceUpdateRequest
} from '../types/service';

export type ServiceFilterParams = {
  carId?: number | '';
  status?: ServiceStatus | '';
};

export async function getServices(
  page = 0,
  size = 10,
  filters: ServiceFilterParams = {}
) {
  const response = await apiClient.get<ApiResponse<SpringPage<ServiceResponse>>>('/services', {
    params: {
      page,
      size,
      sort: 'id,desc',
      carId: filters.carId || undefined,
      status: filters.status || undefined
    }
  });

  return response.data.data;
}

export async function createService(request: ServiceRequest) {
  const response = await apiClient.post<ApiResponse<ServiceResponse>>('/services', request);
  return response.data.data;
}

export async function updateService(id: number, request: ServiceUpdateRequest) {
  const response = await apiClient.put<ApiResponse<ServiceResponse>>(`/services/${id}`, request);
  return response.data.data;
}