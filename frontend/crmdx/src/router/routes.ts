// src/router/routes.ts
export const PUBLIC_ROUTES = {
  LOGIN: '/',
} as const;

export const APP_ROUTES = {
  ROOT: '/app',
  PROYECTOS: '/app/proyectos',
  DATASETS: '/app/datasets',
  MAPEO: '/app/mapeo',
  LIMPIEZA: '/app/limpieza',
  FUSION: '/app/fusion',
  DASHBOARD: '/app/dashboard',
  OPORTUNIDADES: '/app/oportunidades',
  MODULOS_OPERATIVOS: '/app/modulos-operativos',
  USUARIOS: '/app/usuarios',
  AUDITORIA: '/app/auditoria',
} as const;
