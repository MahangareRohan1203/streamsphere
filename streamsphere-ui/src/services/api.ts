import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

// Define the base URL for our Spring Cloud API Gateway
const BASE_URL = 'http://localhost:8080';

export const api = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_URL,
    prepareHeaders: (headers) => {
      // Automatically inject the JWT token into all requests if it exists in localStorage
      const token = localStorage.getItem('access_token');
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
