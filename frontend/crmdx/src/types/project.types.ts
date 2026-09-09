// src/types/project.types.ts
export type RubroProyecto =
  | 'ventas'
  | 'comercio'
  | 'demografia'
  | 'poblacion'
  | 'territorial'
  | 'inventario'
  | 'otros';

export type EstadoProyecto =
  | 'creado'
  | 'con_datasets'
  | 'en_mapeo'
  | 'en_limpieza'
  | 'procesado'
  | 'archivado';

export interface Proyecto {
  id: string;
  usuario_propietario_id: string;
  empresa_id: string;
  nombre: string;
  rubro: RubroProyecto;
  descripcion?: string;
  estado: EstadoProyecto;
  creado_en: string;
  actualizado_en: string;
}
