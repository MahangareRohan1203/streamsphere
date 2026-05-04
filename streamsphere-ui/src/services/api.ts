import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import type { RootState } from '../app/store';

// Define the base URL for our Spring Cloud API Gateway
// Senior SE Tip: In development, we use Vite Proxy to avoid CORS issues if VITE_API_BASE_URL is empty.
const BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

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
