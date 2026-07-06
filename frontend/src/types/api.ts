export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
};

export type SpringPage<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
};

export type PageResponse<T> = SpringPage<T>;