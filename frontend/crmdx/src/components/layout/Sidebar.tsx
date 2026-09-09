// src/components/layout/Sidebar.tsx
import React, { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
  Database,
  Layers,
  Sparkles,
  GitMerge,
  BarChart3,
  Lightbulb,
  Table2,
  Users,
  ShieldCheck,
  FolderPlus,
  HelpCircle,
  Activity,
} from 'lucide-react';
import { APP_ROUTES } from '@/router/routes';
import { mockUsuario, mockProyectos } from '@/services/mockData';

const navItems: { to: string; label: string; icon: React.ElementType; stageNum?: number }[] = [
  { to: APP_ROUTES.PROYECTOS, label: 'Proyectos', icon: Layers, stageNum: 1 },
  { to: APP_ROUTES.DATASETS, label: 'Carga de Datasets', icon: Database, stageNum: 2 },
  { to: APP_ROUTES.MAPEO, label: 'Mapeo Semántico', icon: Sparkles, stageNum: 3 },
  { to: APP_ROUTES.LIMPIEZA, label: 'Limpieza de Datos', icon: Activity, stageNum: 4 },
  { to: APP_ROUTES.FUSION, label: 'Fusión & Master', icon: GitMerge, stageNum: 5 },
  { to: APP_ROUTES.DASHBOARD, label: 'Dashboard Analítico', icon: BarChart3, stageNum: 6 },
  { to: APP_ROUTES.OPORTUNIDADES, label: 'Oportunidades', icon: Lightbulb, stageNum: 7 },
  { to: APP_ROUTES.MODULOS_OPERATIVOS, label: 'Tablas Adaptativas', icon: Table2 },
  { to: APP_ROUTES.USUARIOS, label: 'Equipo & Permisos', icon: Users },
  { to: APP_ROUTES.AUDITORIA, label: 'Auditoría', icon: ShieldCheck },
];

export const Sidebar: React.FC = () => {
  const navigate = useNavigate();
  const [activeProjectId, setActiveProjectId] = useState(mockProyectos[0]?.id ?? '');
  const activeProject = mockProyectos.find((p) => p.id === activeProjectId);

  return (
    <aside className="w-64 bg-slate-900 border-r border-slate-800 text-slate-200 flex flex-col h-screen shrink-0 select-none">
      {/* Brand Header */}
      <div className="p-4 border-b border-slate-800 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-lg bg-gradient-to-br from-indigo-500 to-sky-500 flex items-center justify-center shadow-md shadow-indigo-500/20">
            <Database className="w-5 h-5 text-white" />
          </div>
          <div>
            <h1 className="font-bold text-base tracking-tight text-white flex items-center gap-1.5">
              CRM DEXTER
              <span className="text-[10px] uppercase font-semibold px-1.5 py-0.5 bg-sky-500/20 text-sky-400 rounded border border-sky-500/30">
                PRO
              </span>
            </h1>
            <p className="text-xs text-slate-400">Inteligencia de Datos</p>
          </div>
        </div>
      </div>

      {/* Active Project Selector (visual, mock) */}
      <div className="p-3 border-b border-slate-800 bg-slate-950/40">
        <div className="flex items-center justify-between mb-1.5 px-1">
          <span className="text-[11px] font-medium text-slate-400 uppercase tracking-wider">
            Proyecto Activo
          </span>
          <button
            onClick={() => navigate(APP_ROUTES.PROYECTOS)}
            className="text-xs text-sky-400 hover:text-sky-300 flex items-center gap-1 transition-colors"
            title="Crear nuevo proyecto"
          >
            <FolderPlus className="w-3.5 h-3.5" />
            <span>Nuevo</span>
          </button>
        </div>

        <div className="relative">
          <select
            value={activeProjectId}
            onChange={(e) => setActiveProjectId(e.target.value)}
            className="w-full text-xs bg-slate-800 border border-slate-700 text-slate-200 rounded-md px-2.5 py-1.5 pr-8 focus:outline-none focus:ring-1 focus:ring-sky-500 cursor-pointer truncate"
          >
            {mockProyectos.map((p) => (
              <option key={p.id} value={p.id}>
                {p.nombre} ({p.rubro})
              </option>
            ))}
          </select>
        </div>

        {activeProject && (
          <div className="mt-2 px-1 flex items-center justify-between text-[11px] text-slate-400">
            <span className="capitalize font-medium text-slate-300">
              Rubro: <span className="text-sky-400">{activeProject.rubro}</span>
            </span>
            <span className="text-[10px] px-1.5 py-0.2 rounded bg-slate-800 text-slate-400 border border-slate-700">
              {activeProject.estado}
            </span>
          </div>
        )}
      </div>

      {/* Navigation Links */}
      <nav className="flex-1 overflow-y-auto p-3 space-y-1 custom-scrollbar">
        <div className="px-2 py-1 text-[10px] font-bold uppercase tracking-wider text-slate-500">
          Flujo de Procesamiento
        </div>

        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `w-full flex items-center justify-between px-3 py-2 rounded-lg text-xs font-medium transition-all ${
                  isActive
                    ? 'bg-sky-600 text-white shadow-sm shadow-sky-600/30'
                    : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                }`
              }
            >
              {({ isActive }) => (
                <>
                  <div className="flex items-center space-x-2.5 min-w-0">
                    <Icon className={`w-4 h-4 shrink-0 ${isActive ? 'text-white' : 'text-slate-400'}`} />
                    <span className="truncate">{item.label}</span>
                  </div>
                  {item.stageNum && (
                    <span
                      className={`text-[10px] px-1.5 py-0.5 rounded font-mono ${
                        isActive ? 'bg-sky-700 text-sky-100' : 'bg-slate-800 text-slate-400'
                      }`}
                    >
                      P{item.stageNum}
                    </span>
                  )}
                </>
              )}
            </NavLink>
          );
        })}
      </nav>

      {/* Quick Role Switcher (solo visual, no cambia permisos reales todavía) */}
      <div className="p-3 border-t border-slate-800 bg-slate-950/60">
        <div className="text-[10px] text-slate-500 uppercase font-semibold mb-1.5 flex items-center justify-between">
          <span>Rol Demo</span>
          <HelpCircle className="w-3 h-3 text-slate-600" />
        </div>
        <div className="grid grid-cols-2 gap-1 text-[11px]">
          {(['administrador', 'propietario_empresa', 'analista', 'usuario_operativo'] as const).map((rol) => (
            <button
              key={rol}
              className={`px-2 py-1 rounded text-left truncate transition-colors ${
                mockUsuario.rol === rol
                  ? 'bg-purple-600/30 text-purple-300 border border-purple-500/50 font-semibold'
                  : 'bg-slate-800/80 text-slate-400 hover:bg-slate-800'
              }`}
            >
              {rol.replace('_', ' ')}
            </button>
          ))}
        </div>
      </div>

      {/* User Footer Profile */}
      <div className="p-3 border-t border-slate-800 flex items-center justify-between">
        <div className="flex items-center space-x-2.5 min-w-0">
          <div className="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center font-bold text-xs text-sky-400">
            {mockUsuario.nombre.charAt(0)}
          </div>
          <div className="min-w-0">
            <p className="text-xs font-medium text-slate-200 truncate">{mockUsuario.nombre}</p>
            <p className="text-[10px] text-slate-400 capitalize truncate">{mockUsuario.rol.replace('_', ' ')}</p>
          </div>
        </div>
      </div>
    </aside>
  );
};
