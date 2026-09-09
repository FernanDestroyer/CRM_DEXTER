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
