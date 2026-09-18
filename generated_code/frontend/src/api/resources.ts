import { api } from './client';
import type {
  Order,
  PageResponse,
} from './types';

export interface ListParams {
  search?: string;
  page?: number;
  size?: number;
  [key: string]: string | number | undefined;
}

/**
 * Generic REST resource client. Works for every CRUD resource exposed by the
 * backend; the id type is a string (natural keys) or number (surrogate keys).
 */
export function createResourceApi<T, ID extends string | number>(path: string) {
  return {
    list: async (params: ListParams = {}): Promise<PageResponse<T>> => {
      const { data } = await api.get<PageResponse<T>>(path, { params });
      return data;
    },
    get: async (id: ID): Promise<T> => {
      const { data } = await api.get<T>(`${path}/${id}`);
      return data;
    },
    create: async (payload: Partial<T>): Promise<T> => {
      const { data } = await api.post<T>(path, payload);
      return data;
    },
    update: async (id: ID, payload: Partial<T>): Promise<T> => {
      const { data } = await api.put<T>(`${path}/${id}`, payload);
      return data;
    },
    remove: async (id: ID): Promise<void> => {
      await api.delete(`${path}/${id}`);
    },
  };
}

/** Order-specific operations (create with lines, status update). */
export interface OrderLineInput {
  articleId: string;
  quantity: number;
  unitPrice?: number;
  vatCode?: string;
}

export interface OrderInput {
  customerId: number;
  orderDate?: string;
  lines: OrderLineInput[];
}

export const ordersApi = {
  list: async (params: ListParams = {}): Promise<PageResponse<Order>> => {
    const { data } = await api.get<PageResponse<Order>>('/orders', { params });
    return data;
  },
  get: async (id: number): Promise<Order> => {
    const { data } = await api.get<Order>(`/orders/${id}`);
    return data;
  },
  create: async (payload: OrderInput): Promise<Order> => {
    const { data } = await api.post<Order>('/orders', payload);
    return data;
  },
  markDelivered: async (id: number): Promise<Order> => {
    const { data } = await api.patch<Order>(`/orders/${id}/status`, {
      markFullyDelivered: true,
    });
    return data;
  },
  remove: async (id: number): Promise<void> => {
    await api.delete(`/orders/${id}`);
  },
};
