import { api } from '../../services/api';

export interface LoginRequest {
  username: string;
  password?: string; // Optional for simple demo if backend doesn't require it yet
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  type: string;
}

export const authApi = api.injectEndpoints({
  endpoints: (builder) => ({
    login: builder.mutation<AuthResponse, LoginRequest>({
      query: (credentials) => ({
        url: '/auth/login',
        method: 'POST',
        body: credentials,
      }),
    }),
    register: builder.mutation<any, any>({
      query: (user) => ({
        url: '/users',
        method: 'POST',
        body: user,
      }),
    }),
  }),
});

export const { useLoginMutation, useRegisterMutation } = authApi;
