// src/types/auth.types.ts
export type RolUsuario =
  | 'administrador'
  | 'propietario_empresa'
  | 'analista'
  | 'usuario_operativo';

export type EstadoUsuario = 'activo' | 'inactivo' | 'suspendido';

export interface Usuario {
  id: string;
  empresa_id: string;
  nombre: string;
  email: string;
  rol: RolUsuario;
  estado: EstadoUsuario;
  ultimo_acceso?: string;
  creado_en: string;
  actualizado_en: string;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export type AuthResponseStatus = 'AUTHENTICATED' | 'PENDING' | 'OTP_REQUIRED' | 'REJECTED';

export interface LoginResponse {
  message: string;
  rol: string;
  token?: string | null;
  status: AuthResponseStatus;
}

export interface AuthUser {
  id: string;
  email: string;
  nombre: string;
  rol: string;
  estado: string;
}

export interface AccessRequest {
  id: string;
  email: string;
  estado: string;
  creadoEn: string;
}
