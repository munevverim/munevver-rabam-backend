export type CarResponse = {
  id: number;
  licensePlate: string;
  brand: string;
  model: string;
  createdAt: string;
  updatedAt: string | null;
};

export type CarRequest = {
  licensePlate: string;
  brand: string;
  model: string;
};