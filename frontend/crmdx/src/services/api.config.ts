// src/services/api.config.ts
// NOTA: instancia lista para cuando se conecte el backend real (Spring Boot).
// Por ahora no se usa: las vistas trabajan solo con datos mock / navegación directa.
import axios from 'axios';

export const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1/',
  headers: {
    'Content-Type': 'application/json',
  },
});
