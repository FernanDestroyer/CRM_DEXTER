import { apiClient } from '@/services/api.config';
import type { AuthUser, LoginResponse } from '@/types/auth.types';

export const authService = {
  login: (email: string) => apiClient.post<LoginResponse>('/auth/login', { email }),
  verifyOtp: (email: string, otp: string) => apiClient.post<LoginResponse>('/auth/verify-otp', { email, otp }),
  me: () => apiClient.get<AuthUser>('/auth/me'),
  session: () => apiClient.get<{ authenticated: boolean }>('/auth/session'),
  logout: () => apiClient.post('/auth/logout'),
};
