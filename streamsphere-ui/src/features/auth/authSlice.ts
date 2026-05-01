import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

interface AuthState {
  user: string | null;
  role: string | null;
  token: string | null;
  isAuthenticated: boolean;
}

// Simple manual JWT decoding
const decodeRole = (token: string | null): string | null => {
  if (!token) return null;
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload).role || 'USER';
  } catch (e) {
    return 'USER';
  }
};

// Load initial state from localStorage
const storedUser = localStorage.getItem('user');
const storedToken = localStorage.getItem('token');

const initialState: AuthState = {
  user: storedUser,
  role: decodeRole(storedToken),
  token: storedToken,
  isAuthenticated: !!storedToken,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (
      state,
      action: PayloadAction<{ user: string; token: string }>
    ) => {
      const role = decodeRole(action.payload.token);
      state.user = action.payload.user;
      state.role = role;
      state.token = action.payload.token;
      state.isAuthenticated = true;
      localStorage.setItem('user', action.payload.user);
      localStorage.setItem('token', action.payload.token);
    },
    logout: (state) => {
      state.user = null;
      state.role = null;
      state.token = null;
      state.isAuthenticated = false;
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    },
  },
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;
