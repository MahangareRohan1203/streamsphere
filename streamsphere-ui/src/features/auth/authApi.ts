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

export interface RegisterRequest {
  username: string;
  email: string;
  password?: string;
  role: string;
}

export interface RegisterResponse {
  id: number;
  username: string;
  email: string;
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
    register: builder.mutation<RegisterResponse, RegisterRequest>({
      query: (user) => ({
        url: '/users',
        method: 'POST',
        body: user,
      }),
    }),
  }),
});

export const { useLoginMutation, useRegisterMutation } = authApi;
