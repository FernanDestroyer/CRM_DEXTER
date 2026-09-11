import { Check, ShieldX, Users } from 'lucide-react';
import { useAccessRequests } from '@/hooks/useAccessRequests';

export default function TeamPage() {
  const { requests, roles, setRoles, error, loading, decide } = useAccessRequests();
  return <section className="max-w-5xl mx-auto">
    <div className="flex items-center gap-3 mb-6"><Users className="text-sky-600" /><div><h1 className="text-2xl font-semibold text-slate-900">Equipo y permisos</h1><p className="text-sm text-slate-500">Aprueba accesos y asigna el rol inicial del usuario.</p></div></div>
    {error && <p className="mb-4 rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700">{error}</p>}
    <div className="rounded-xl border border-slate-200 bg-white shadow-sm overflow-hidden">
      {loading ? <p className="p-6 text-slate-500">Cargando solicitudes...</p> : requests.length === 0 ? <p className="p-6 text-slate-500">No hay solicitudes pendientes.</p> : requests.map((request) => <div key={request.id} className="flex flex-wrap items-center justify-between gap-4 border-b border-slate-100 p-5 last:border-0"><div><p className="font-medium text-slate-800">{request.email}</p><p className="text-xs text-slate-400">{new Date(request.creadoEn).toLocaleString()}</p></div><div className="flex flex-wrap gap-2"><select value={roles[request.id] || 'ANALISTA'} onChange={(event) => setRoles((current) => ({ ...current, [request.id]: event.target.value }))} className="rounded-lg border border-slate-300 px-2 py-2 text-sm"><option value="PROPIETARIO">Propietario</option><option value="ANALISTA">Analista</option><option value="OPERATIVO">Operativo</option></select><button onClick={() => void decide(request.id, 'rechazar')} className="inline-flex items-center gap-1 rounded-lg border border-rose-200 px-3 py-2 text-sm text-rose-700 hover:bg-rose-50"><ShieldX size={16} /> Rechazar</button><button onClick={() => void decide(request.id, 'aprobar')} className="inline-flex items-center gap-1 rounded-lg bg-sky-600 px-3 py-2 text-sm text-white hover:bg-sky-500"><Check size={16} /> Aprobar</button></div></div>)}
    </div>
  </section>;
}
