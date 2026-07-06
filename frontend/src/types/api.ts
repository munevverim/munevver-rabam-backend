export type ApiResponse<T> = {
  success?: boolean;
  message?: string;
  data: T;
  timestamp?: string;
};

export type SpringPage<T> = {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
};