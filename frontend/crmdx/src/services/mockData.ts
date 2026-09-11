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

// ---------------------------------------------------------------------------
// Mock: Fusión y Master (P5) — solo para que el diseño tenga contenido real.
// No representa lógica de negocio: se reemplazará por datos del backend
// (EjecucionProcesamiento, DatasetProcesado, MapeoColumna) vía /api.
// ---------------------------------------------------------------------------
export interface MockFuenteDataset {
  id: string;
  nombre: string;
  archivo: string;
  filas: number;
  columnas: number;
  mapeoCompletado: number; // % de columnas mapeadas al esquema canónico
  estado: 'listo' | 'con_alertas';
}

export const mockFuentesFusion: MockFuenteDataset[] = [
  { id: 'ds_001', nombre: 'Ventas Sucursal Trujillo', archivo: 'ventas_trujillo_q3.xlsx', filas: 4820, columnas: 14, mapeoCompletado: 100, estado: 'listo' },
  { id: 'ds_002', nombre: 'Ventas Sucursal Chiclayo', archivo: 'ventas_chiclayo_q3.csv', filas: 3190, columnas: 13, mapeoCompletado: 100, estado: 'listo' },
  { id: 'ds_003', nombre: 'Ventas Sucursal Piura', archivo: 'ventas_piura_q3.csv', filas: 2745, columnas: 12, mapeoCompletado: 92, estado: 'con_alertas' },
];

export const mockEsquemaMaestro = [
  { campo: 'fecha_venta', tipo: 'fecha', fuentes: 3 },
  { campo: 'sucursal', tipo: 'texto', fuentes: 3 },
  { campo: 'vendedor', tipo: 'texto', fuentes: 3 },
  { campo: 'producto', tipo: 'texto', fuentes: 3 },
  { campo: 'categoria', tipo: 'texto', fuentes: 3 },
  { campo: 'monto_soles', tipo: 'decimal', fuentes: 3 },
  { campo: 'cantidad', tipo: 'entero', fuentes: 3 },
  { campo: 'cliente_id', tipo: 'texto', fuentes: 2 },
];

export const mockPreviewMaestro = [
  { fecha_venta: '2026-07-03', sucursal: 'Trujillo', vendedor: 'M. Rojas', producto: 'Filtro de aceite', categoria: 'Repuestos', monto_soles: 145.0, cantidad: 2 },
  { fecha_venta: '2026-07-04', sucursal: 'Chiclayo', vendedor: 'J. Peña', producto: 'Mantenimiento 10k', categoria: 'Servicios', monto_soles: 320.5, cantidad: 1 },
  { fecha_venta: '2026-07-04', sucursal: 'Piura', vendedor: 'L. Vera', producto: 'Batería 12V', categoria: 'Repuestos', monto_soles: 480.0, cantidad: 1 },
  { fecha_venta: '2026-07-05', sucursal: 'Trujillo', vendedor: 'M. Rojas', producto: 'Llanta aro 16', categoria: 'Repuestos', monto_soles: 610.0, cantidad: 4 },
  { fecha_venta: '2026-07-06', sucursal: 'Chiclayo', vendedor: 'S. Díaz', producto: 'Revisión técnica', categoria: 'Servicios', monto_soles: 95.0, cantidad: 1 },
];

// ---------------------------------------------------------------------------
// Mock: Panel Analítico (P6) — KPIs, gráficos e insights.
// ---------------------------------------------------------------------------
export const mockKpis = [
  { label: 'Registros Fusionados', value: '10,755', delta: '+12.4%', positive: true },
  { label: 'Ingreso Total (S/)', value: 'S/ 486,920', delta: '+8.1%', positive: true },
  { label: 'Calidad de Datos', value: '96.3%', delta: '+1.5%', positive: true },
  { label: 'Valores Duplicados', value: '38', delta: '-64%', positive: true },
];

export const mockVentasPorSucursal = [
  { label: 'Trujillo', value: 198400 },
  { label: 'Chiclayo', value: 152300 },
  { label: 'Piura', value: 136220 },
];

export const mockTendenciaMensual = [
  { label: 'Ene', value: 62000 },
  { label: 'Feb', value: 58400 },
  { label: 'Mar', value: 71200 },
  { label: 'Abr', value: 69800 },
  { label: 'May', value: 78100 },
  { label: 'Jun', value: 84300 },
  { label: 'Jul', value: 91600 },
];

export const mockDistribucionCategoria = [
  { label: 'Repuestos', value: 46, color: '#0284c7' },
  { label: 'Servicios', value: 31, color: '#6366f1' },
  { label: 'Accesorios', value: 14, color: '#10b981' },
  { label: 'Otros', value: 9, color: '#f59e0b' },
];

export const mockInsights = [
  { tipo: 'positivo', texto: 'Trujillo concentra el 41% del ingreso total, el mejor trimestre desde su apertura.' },
  { tipo: 'alerta', texto: 'Piura tiene 8% de columnas sin mapear; revisa el mapeo semántico antes de re-fusionar.' },
  { tipo: 'positivo', texto: 'Los duplicados detectados bajaron de 106 a 38 tras la última limpieza de datos.' },
  { tipo: 'neutral', texto: 'La categoría "Servicios" creció 22% frente al trimestre anterior.' },
];

export const mockTopVendedores = [
  { vendedor: 'M. Rojas', sucursal: 'Trujillo', ventas: 128, monto: 58200 },
  { vendedor: 'J. Peña', sucursal: 'Chiclayo', ventas: 104, monto: 47650 },
  { vendedor: 'L. Vera', sucursal: 'Piura', ventas: 96, monto: 41980 },
  { vendedor: 'S. Díaz', sucursal: 'Chiclayo', ventas: 88, monto: 36720 },
];
