import React, { useState } from 'react';
import { X, FolderPlus } from 'lucide-react';
import type { RubroProyecto, Proyecto } from '@/types/project.types';
import { apiClient } from '@/services/api.config';

interface CreateProjectModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: (project: Proyecto) => void;
}

const rubros: { id: RubroProyecto; label: string; desc: string; icon: string }[] = [
  { id: 'ventas', label: 'Ventas & Comercio', desc: 'Ingresos, clientes, transacciones, productos, campañas', icon: '💰' },
  { id: 'demografia', label: 'Demografía & Población', desc: 'Edades, género, hogares, estratos socioeconómicos', icon: '👥' },
  { id: 'territorial', label: 'Territorial & Geográfico', desc: 'Coordenadas, regiones, calor espacial, zonas', icon: '🗺️' },
  { id: 'inventario', label: 'Inventario & Logística', desc: 'Stock, almacenes, movimientos, proveedores', icon: '📦' },
];

export const CreateProjectModal: React.FC<CreateProjectModalProps> = ({ isOpen, onClose, onSuccess }) => {
  const [nombre, setNombre] = useState('');
  const [rubro, setRubro] = useState<RubroProyecto>('ventas');
  const [descripcion, setDescripcion] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!nombre.trim() || isSaving) return;
    setIsSaving(true);
    setError('');
    try {
      const { data } = await apiClient.post<Proyecto>('/proyectos', {
        nombre: nombre.trim(), rubro, descripcion: descripcion.trim(),
      });
      onSuccess(data);
      setNombre(''); setDescripcion(''); setRubro('ventas');
    } catch {
      setError('No se pudo crear el proyecto. Verifica que hayas iniciado sesión y que el backend esté activo.');
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm">
      <div className="bg-white rounded-2xl max-w-lg w-full shadow-2xl border border-slate-200 overflow-hidden">
        <div className="p-6 border-b border-slate-100 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-sky-50 border border-sky-200 text-sky-600 flex items-center justify-center"><FolderPlus className="w-5 h-5" /></div>
            <div><h3 className="text-base font-bold text-slate-900">Crear Nuevo Proyecto</h3><p className="text-xs text-slate-500">Se guardará asociado a tu usuario</p></div>
          </div>
          <button onClick={onClose} className="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100"><X className="w-5 h-5" /></button>
        </div>
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div><label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">Nombre del Proyecto *</label><input type="text" required value={nombre} onChange={(e) => setNombre(e.target.value)} placeholder="Ej. Análisis de Ventas Q1" className="w-full text-sm px-3.5 py-2 border border-slate-300 rounded-lg text-slate-900 focus:outline-none focus:ring-2 focus:ring-sky-500" /></div>
          <div><label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-2">Rubro / Dominio *</label><div className="grid grid-cols-2 gap-2">{rubros.map((r) => <button type="button" key={r.id} onClick={() => setRubro(r.id)} className={`p-3 rounded-xl border text-left transition-all ${rubro === r.id ? 'border-sky-500 bg-sky-50/50 ring-1 ring-sky-500 shadow-sm' : 'border-slate-200 hover:border-slate-300 bg-white'}`}><div className="flex items-center space-x-2"><span className="text-base">{r.icon}</span><span className={`text-xs font-bold ${rubro === r.id ? 'text-sky-900' : 'text-slate-900'}`}>{r.label}</span></div><p className="text-[10px] text-slate-500 mt-1 line-clamp-2 leading-tight">{r.desc}</p></button>)}</div></div>
          <div><label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">Descripción u Objetivos (Opcional)</label><textarea rows={2} value={descripcion} onChange={(e) => setDescripcion(e.target.value)} placeholder="Objetivo del proyecto..." className="w-full text-sm px-3.5 py-2 border border-slate-300 rounded-lg text-slate-900 focus:outline-none focus:ring-2 focus:ring-sky-500 resize-none" /></div>
          {error && <p className="text-xs text-rose-600 bg-rose-50 border border-rose-200 rounded-lg p-2">{error}</p>}
          <div className="pt-2 flex items-center justify-end space-x-3"><button type="button" onClick={onClose} className="px-4 py-2 text-xs font-semibold text-slate-600 hover:text-slate-800 hover:bg-slate-100 rounded-lg">Cancelar</button><button type="submit" disabled={!nombre.trim() || isSaving} className="px-5 py-2 bg-sky-600 hover:bg-sky-500 text-white text-xs font-semibold rounded-lg shadow-sm disabled:opacity-50">{isSaving ? 'Guardando...' : 'Crear Proyecto'}</button></div>
        </form>
      </div>
    </div>
  );
};
