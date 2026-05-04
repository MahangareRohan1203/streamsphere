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

export interface UserProfileResponse {
  id: number;
  username: string;
  email: string;
  role: string;
  currentTier: string;
}

export interface SubscribeRequest {
  tier: string;
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
    getUserProfile: builder.query<UserProfileResponse, string>({
      query: (username) => `/users/${username}`,
      providesTags: ['User'],
    }),
    subscribe: builder.mutation<any, { userId: number; tier: string }>({
      query: ({ userId, tier }) => ({
        url: `/users/${userId}/subscriptions`,
        method: 'POST',
        body: { tier },
      }),
      // Invalidate User so profile query refetches
      invalidatesTags: ['User'],
    }),
  }),
});

export const { useLoginMutation, useRegisterMutation, useGetUserProfileQuery, useSubscribeMutation } = authApi;
