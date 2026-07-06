import { apiClient } from './apiClient';
import type { ApiResponse, SpringPage } from '../types/api';
import type { CarRequest, CarResponse } from '../types/car';

export async function getCars(page = 0, size = 10) {
  const response = await apiClient.get<ApiResponse<SpringPage<CarResponse>>>('/cars', {
    params: {
      page,
      size,
      sort: 'id,desc'
    }
  });

  return response.data.data;
}

export async function createCar(request: CarRequest) {
  const response = await apiClient.post<ApiResponse<CarResponse>>('/cars', request);
  return response.data.data;
}

export async function updateCar(id: number, request: CarRequest) {
  const response = await apiClient.put<ApiResponse<CarResponse>>(`/cars/${id}`, request);
  return response.data.data;
}