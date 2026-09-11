import axios from 'axios';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
});

apiClient.interceptors.response.use((response) => response, (error) => {
  if (error.response?.status === 401 && window.location.pathname.startsWith('/app')) {
    window.location.assign('/');
  }
  return Promise.reject(error);
});
