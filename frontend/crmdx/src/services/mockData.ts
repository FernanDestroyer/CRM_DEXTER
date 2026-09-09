// src/services/mockData.ts
// Datos de ejemplo (mock) usados solo para que el diseño tenga contenido que mostrar.
// No representan lógica de negocio real: se reemplazarán por datos del backend Spring Boot.
import type { Usuario } from '@/types/auth.types';
import type { Proyecto } from '@/types/project.types';

export const mockUsuario: Usuario = {
  id: 'usr_001',
  empresa_id: 'emp_001',
  nombre: 'Usuario Demo',
  email: 'demo@crmdexter.com',
  rol: 'administrador',
  estado: 'activo',
  creado_en: new Date().toISOString(),
  actualizado_en: new Date().toISOString(),
};

export const mockEmpresa = {
  id: 'emp_001',
  nombre: 'Mi Empresa',
};

const rand = (min: number, max: number) => Math.floor(Math.random() * (max - min + 1)) + min;

export const mockProyectos: Proyecto[] = [
  {
    id: 'proj_001',
    usuario_propietario_id: 'usr_001',
    empresa_id: 'emp_001',
    nombre: 'Ventas Región Norte',
    rubro: 'ventas',
    descripcion: 'Consolidado de ventas trimestrales',
    estado: 'procesado',
    creado_en: new Date(Date.now() - rand(1, 30) * 86400000).toISOString(),
    actualizado_en: new Date().toISOString(),
  },
  {
    id: 'proj_002',
    usuario_propietario_id: 'usr_001',
    empresa_id: 'emp_001',
    nombre: 'Demografía Lima 2026',
    rubro: 'demografia',
    descripcion: 'Análisis poblacional por distrito',
    estado: 'en_mapeo',
    creado_en: new Date(Date.now() - rand(1, 30) * 86400000).toISOString(),
    actualizado_en: new Date().toISOString(),
  },
  {
    id: 'proj_003',
    usuario_propietario_id: 'usr_001',
    empresa_id: 'emp_001',
    nombre: 'Inventario Almacén Central',
    rubro: 'inventario',
    descripcion: 'Control de stock multi-sede',
    estado: 'con_datasets',
    creado_en: new Date(Date.now() - rand(1, 30) * 86400000).toISOString(),
    actualizado_en: new Date().toISOString(),
  },
];

export const randomStat = (min = 10, max = 999) => rand(min, max);
