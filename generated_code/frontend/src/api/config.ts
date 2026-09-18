/**
 * Central, configurable API settings.
 * The base URL is read from the Vite env var VITE_API_BASE_URL so the same build
 * can target different backends without code changes.
 */
export const API_BASE_URL: string =
  import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1';
