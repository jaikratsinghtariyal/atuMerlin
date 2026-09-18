import axios, { AxiosError } from 'axios';
import { API_BASE_URL } from './config';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
});

export interface ApiFieldViolation {
  field: string;
  message: string;
}

export interface ApiErrorBody {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  violations: ApiFieldViolation[];
}

/** Turns any axios error into a readable message (including field violations). */
export function toErrorMessage(err: unknown): string {
  const axiosError = err as AxiosError<ApiErrorBody>;
  const body = axiosError.response?.data;
  if (body) {
    if (body.violations && body.violations.length > 0) {
      return body.violations.map((v) => `${v.field}: ${v.message}`).join('\n');
    }
    if (body.message) {
      return body.message;
    }
  }
  return axiosError.message || 'Unexpected error';
}
