export type ServiceStatus = 'PENDING' | 'IN_PROGRESS' | 'DONE';

export type ServiceResponse = {
  id: number;
  title: string;
  description: string | null;
  status: ServiceStatus;
  carId: number;
  carLicensePlate: string;
  carBrand: string;
  carModel: string;
  version: number;
  createdAt: string;
  updatedAt: string | null;
};

export type ServiceRequest = {
  title: string;
  description: string;
  carId: number;
};

export type ServiceUpdateRequest = {
  title?: string;
  description?: string;
  status?: ServiceStatus;
  version?: number;
};