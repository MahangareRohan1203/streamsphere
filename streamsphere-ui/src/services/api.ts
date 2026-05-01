import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import type { RootState } from '../app/store';

// Senior SE Tip: In development, we use Vite Proxy to avoid CORS issues.
// Empty string means requests go to the same origin (localhost:5173),
// and Vite proxies them to the backend (localhost:8080).
const BASE_URL = '';

export const api = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_URL,
    prepareHeaders: (headers, { getState }) => {
      // Get the token from the Redux state (in-memory)
      const token = (getState() as RootState).auth.token;
      if (token) {
        headers.set('authorization', `Bearer ${token}`);
      }
      return headers;
    },
  }),
  // Tag types are used for caching and invalidation
  tagTypes: ['User', 'Video'],
  endpoints: () => ({}),
});
