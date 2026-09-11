// src/pages/Fusion/FusionPage.tsx
import { useState } from 'react';
import {
  GitMerge,
  CheckCircle2,
  AlertTriangle,
  Database,
  Layers,
  Play,
  Download,
  ShieldCheck,
  Clock,
} from 'lucide-react';
import {
  mockFuentesFusion,
  mockEsquemaMaestro,
  mockPreviewMaestro,
} from '@/services/mockData';

type EstrategiaConflicto = 'prioridad' | 'reciente' | 'promedio';

const ESTRATEGIAS: { id: EstrategiaConflicto; label: string; desc: string }[] = [
  { id: 'prioridad', label: 'Prioridad por fuente', desc: 'Usa el valor del dataset con mayor prioridad configurada.' },
  { id: 'reciente', label: 'Registro más reciente', desc: 'Conserva el valor con la fecha de carga más nueva.' },
  { id: 'promedio', label: 'Promedio (numéricos)', desc: 'Calcula el promedio entre las fuentes para campos numéricos.' },
];

type FusionState = 'idle' | 'ejecutando' | 'completado';

export default function FusionPage() {
  const [seleccionadas, setSeleccionadas] = useState<Record<string, boolean>>(
    Object.fromEntries(mockFuentesFusion.map((f) => [f.id, true])),
  );
  const [estrategia, setEstrategia] = useState<EstrategiaConflicto>('prioridad');
  const [estado, setEstado] = useState<FusionState>('idle');
  const [progreso, setProgreso] = useState(0);

  const totalSeleccionadas = mockFuentesFusion.filter((f) => seleccionadas[f.id]).length;
  const totalFilas = mockFuentesFusion
    .filter((f) => seleccionadas[f.id])
    .reduce((sum, f) => sum + f.filas, 0);
  const alertas = mockFuentesFusion.filter((f) => seleccionadas[f.id] && f.estado === 'con_alertas').length;

  const toggleFuente = (id: string) => {
    if (estado === 'ejecutando') return;
    setSeleccionadas((prev) => ({ ...prev, [id]: !prev[id] }));
  };

  const ejecutarFusion = () => {
    if (totalSeleccionadas < 2 || estado === 'ejecutando') return;
    setEstado('ejecutando');
    setProgreso(0);
    const interval = setInterval(() => {
      setProgreso((p) => {
        const next = p + 14 + Math.random() * 10;
        if (next >= 100) {
          clearInterval(interval);
          setEstado('completado');
          return 100;
        }
        return next;
      });
    }, 350);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <GitMerge className="w-5 h-5 text-sky-600" />
            Fusión y Master
          </h1>
          <p className="text-xs text-slate-500 mt-1">
            Consolida los datasets mapeados en un único dataset maestro (patrón Data Lake).
          </p>
        </div>
        <button
          onClick={ejecutarFusion}
          disabled={totalSeleccionadas < 2 || estado === 'ejecutando'}
          className="flex items-center space-x-2 px-4 py-2 bg-sky-600 hover:bg-sky-500 disabled:bg-slate-200 disabled:text-slate-400 disabled:cursor-not-allowed text-white rounded-lg text-xs font-semibold shadow-sm transition-colors"
        >
          <Play className="w-4 h-4" />
          <span>{estado === 'ejecutando' ? 'Fusionando…' : 'Ejecutar Fusión'}</span>
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Fuentes a fusionar */}
        <div className="lg:col-span-2 bg-white rounded-xl border border-slate-200 shadow-sm">
          <div className="px-5 py-4 border-b border-slate-100 flex items-center justify-between">
            <h2 className="text-sm font-bold text-slate-900 flex items-center gap-2">
              <Database className="w-4 h-4 text-slate-400" />
              Datasets de Origen
            </h2>
            <span className="text-[11px] font-medium text-slate-500">
              {totalSeleccionadas} de {mockFuentesFusion.length} seleccionados
            </span>
          </div>
          <div className="divide-y divide-slate-100">
            {mockFuentesFusion.map((f) => {
              const checked = !!seleccionadas[f.id];
              return (
                <label
                  key={f.id}
                  className={`flex items-center gap-4 px-5 py-3.5 cursor-pointer transition-colors ${
                    checked ? 'bg-sky-50/40' : 'hover:bg-slate-50'
                  }`}
                >
                  <input
                    type="checkbox"
                    checked={checked}
                    onChange={() => toggleFuente(f.id)}
                    className="w-4 h-4 rounded border-slate-300 text-sky-600 focus:ring-sky-500 shrink-0"
                  />
                  <div className="min-w-0 flex-1">
                    <p className="text-sm font-semibold text-slate-900 truncate">{f.nombre}</p>
                    <p className="text-[11px] text-slate-500 truncate">{f.archivo}</p>
                  </div>
                  <div className="hidden sm:flex items-center gap-4 text-xs text-slate-500 shrink-0">
                    <span>{f.filas.toLocaleString('es-PE')} filas</span>
                    <span>{f.columnas} cols</span>
                  </div>
                  <div className="flex items-center gap-1.5 w-28 justify-end shrink-0">
                    {f.estado === 'listo' ? (
                      <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-emerald-700 bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-full">
                        <CheckCircle2 className="w-3 h-3" /> Mapeo 100%
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-amber-700 bg-amber-50 border border-amber-200 px-2 py-0.5 rounded-full">
                        <AlertTriangle className="w-3 h-3" /> {f.mapeoCompletado}%
                      </span>
                    )}
                  </div>
                </label>
              );
            })}
          </div>
          {alertas > 0 && (
            <div className="mx-5 mb-4 flex items-start gap-2 text-xs text-amber-700 bg-amber-50 border border-amber-200 rounded-lg p-3">
              <AlertTriangle className="w-4 h-4 shrink-0 mt-0.5" />
              <span>
                {alertas} dataset(s) tienen columnas sin mapear. Se incluirán en la fusión, pero esos campos
                quedarán vacíos en el maestro hasta completar el mapeo semántico.
              </span>
            </div>
          )}
        </div>

        {/* Resumen + estrategia */}
        <div className="space-y-6">
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
            <h2 className="text-sm font-bold text-slate-900 flex items-center gap-2 mb-4">
              <Layers className="w-4 h-4 text-slate-400" />
              Resumen de Fusión
            </h2>
            <dl className="space-y-3 text-xs">
              <div className="flex items-center justify-between">
                <dt className="text-slate-500">Fuentes seleccionadas</dt>
                <dd className="font-semibold text-slate-900">{totalSeleccionadas}</dd>
              </div>
              <div className="flex items-center justify-between">
                <dt className="text-slate-500">Filas de entrada (est.)</dt>
                <dd className="font-semibold text-slate-900">{totalFilas.toLocaleString('es-PE')}</dd>
              </div>
              <div className="flex items-center justify-between">
                <dt className="text-slate-500">Campos del esquema maestro</dt>
                <dd className="font-semibold text-slate-900">{mockEsquemaMaestro.length}</dd>
              </div>
            </dl>
          </div>

          <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
            <h2 className="text-sm font-bold text-slate-900 flex items-center gap-2 mb-1">
              <ShieldCheck className="w-4 h-4 text-slate-400" />
              Estrategia de Conflictos
            </h2>
            <p className="text-[11px] text-slate-500 mb-3">
              Cómo resolver valores distintos para un mismo registro entre fuentes.
            </p>
            <div className="space-y-2">
              {ESTRATEGIAS.map((e) => (
                <label
                  key={e.id}
                  className={`flex items-start gap-2.5 p-2.5 rounded-lg border cursor-pointer transition-colors ${
                    estrategia === e.id ? 'border-sky-500 bg-sky-50/60' : 'border-slate-200 hover:border-slate-300'
                  }`}
                >
                  <input
                    type="radio"
                    name="estrategia"
                    checked={estrategia === e.id}
                    onChange={() => setEstrategia(e.id)}
                    className="mt-0.5 w-3.5 h-3.5 text-sky-600 focus:ring-sky-500"
                  />
                  <div>
                    <p className="text-xs font-semibold text-slate-800">{e.label}</p>
                    <p className="text-[11px] text-slate-500">{e.desc}</p>
                  </div>
                </label>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Progreso / resultado */}
      {estado !== 'idle' && (
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
          <div className="flex items-center justify-between mb-2">
            <h2 className="text-sm font-bold text-slate-900 flex items-center gap-2">
              {estado === 'completado' ? (
                <CheckCircle2 className="w-4 h-4 text-emerald-600" />
              ) : (
                <Clock className="w-4 h-4 text-sky-600 animate-pulse" />
              )}
              {estado === 'completado' ? 'Dataset maestro generado' : 'Ejecutando fusión…'}
            </h2>
            <span className="text-xs font-semibold text-slate-600">{Math.min(100, Math.round(progreso))}%</span>
          </div>
          <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
            <div
              className={`h-full rounded-full transition-all duration-300 ${
                estado === 'completado' ? 'bg-emerald-500' : 'bg-sky-500'
              }`}
              style={{ width: `${Math.min(100, progreso)}%` }}
            />
          </div>
          {estado === 'completado' && (
            <div className="mt-4 flex flex-wrap items-center gap-3">
              <span className="text-xs text-slate-500">
                {totalFilas.toLocaleString('es-PE')} filas consolidadas · {mockEsquemaMaestro.length} columnas ·{' '}
                {Math.max(0, 8 - alertas)} duplicados resueltos
              </span>
              <button className="ml-auto flex items-center gap-1.5 px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-xs font-medium transition-colors">
                <Download className="w-3.5 h-3.5 text-sky-400" />
                Descargar Parquet
              </button>
            </div>
          )}
        </div>
      )}

      {/* Esquema maestro + preview */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm">
          <div className="px-5 py-4 border-b border-slate-100">
            <h2 className="text-sm font-bold text-slate-900">Esquema Canónico Maestro</h2>
          </div>
          <div className="divide-y divide-slate-100">
            {mockEsquemaMaestro.map((c) => (
              <div key={c.campo} className="flex items-center justify-between px-5 py-2.5 text-xs">
                <span className="font-mono text-slate-700">{c.campo}</span>
                <div className="flex items-center gap-2">
                  <span className="text-[10px] uppercase tracking-wide text-slate-400">{c.tipo}</span>
                  <span className="text-[10px] px-1.5 py-0.5 rounded bg-slate-100 text-slate-500">
                    {c.fuentes} fuentes
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="lg:col-span-2 bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="px-5 py-4 border-b border-slate-100 flex items-center justify-between">
            <h2 className="text-sm font-bold text-slate-900">Vista Previa — Dataset Maestro</h2>
            <span className="text-[11px] text-slate-400">
              {estado === 'completado' ? 'muestra de 5 filas' : 'ejemplo de columnas mapeadas'}
            </span>
          </div>
          <div className="overflow-x-auto custom-scrollbar">
            <table className="w-full text-xs">
              <thead>
                <tr className="bg-slate-50 text-slate-500 uppercase tracking-wide text-[10px]">
                  <th className="text-left font-semibold px-4 py-2">Fecha</th>
                  <th className="text-left font-semibold px-4 py-2">Sucursal</th>
                  <th className="text-left font-semibold px-4 py-2">Vendedor</th>
                  <th className="text-left font-semibold px-4 py-2">Producto</th>
                  <th className="text-left font-semibold px-4 py-2">Categoría</th>
                  <th className="text-right font-semibold px-4 py-2">Monto (S/)</th>
                  <th className="text-right font-semibold px-4 py-2">Cant.</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {mockPreviewMaestro.map((row, i) => (
                  <tr key={i} className="hover:bg-slate-50">
                    <td className="px-4 py-2 text-slate-600">{row.fecha_venta}</td>
                    <td className="px-4 py-2 text-slate-700 font-medium">{row.sucursal}</td>
                    <td className="px-4 py-2 text-slate-600">{row.vendedor}</td>
                    <td className="px-4 py-2 text-slate-600">{row.producto}</td>
                    <td className="px-4 py-2">
                      <span className="text-[10px] px-1.5 py-0.5 rounded bg-sky-50 text-sky-700 border border-sky-100">
                        {row.categoria}
                      </span>
                    </td>
                    <td className="px-4 py-2 text-right font-mono text-slate-700">
                      {row.monto_soles.toLocaleString('es-PE', { minimumFractionDigits: 2 })}
                    </td>
                    <td className="px-4 py-2 text-right text-slate-600">{row.cantidad}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
