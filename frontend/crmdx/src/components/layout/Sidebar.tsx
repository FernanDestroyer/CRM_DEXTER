// src/components/layout/Sidebar.tsx
import React from 'react';
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
  Activity,
  LogOut,
} from 'lucide-react';
import { APP_ROUTES } from '@/router/routes';
import { useAuth } from '@/context/AuthContext';
import { useProjects } from '@/context/ProjectContext';

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
  const { logout } = useAuth();
  const { projects, activeProject, activeProjectId, selectProject } = useProjects();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <aside className="w-64 bg-slate-900 border-r border-slate-800 text-slate-200 flex flex-col h-screen shrink-0 select-none">
      <div className="p-4 border-b border-slate-800 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-lg bg-gradient-to-br from-indigo-500 to-sky-500 flex items-center justify-center shadow-md shadow-indigo-500/20">
            <Database className="w-5 h-5 text-white" />
          </div>
          <div>
            <h1 className="font-bold text-base tracking-tight text-white flex items-center gap-1.5">CRM DEXTER <span className="text-[10px] uppercase font-semibold px-1.5 py-0.5 bg-sky-500/20 text-sky-400 rounded border border-sky-500/30">PRO</span></h1>
            <p className="text-xs text-slate-400">Inteligencia de Datos</p>
          </div>
        </div>
      </div>

      {projects.length > 0 && <div className="p-3 border-b border-slate-800 bg-slate-950/40">
        <label htmlFor="project-selector" className="block mb-1.5 px-1 text-[11px] font-medium text-slate-400 uppercase tracking-wider">Proyecto</label>
        <select id="project-selector" value={activeProjectId ?? ''} onChange={(event) => selectProject(event.target.value)} className="w-full text-xs bg-slate-800 border border-slate-700 text-slate-200 rounded-md px-2.5 py-1.5 focus:outline-none focus:ring-1 focus:ring-sky-500 cursor-pointer truncate">
          {projects.map((project) => <option key={project.id} value={project.id}>{project.nombre}</option>)}
        </select>
        {activeProject && <div className="mt-2 px-1 text-[11px] text-slate-400"><span className="capitalize font-medium text-slate-300">Rubro: <span className="text-sky-400">{activeProject.rubro}</span></span></div>}
      </div>}

      <nav className="flex-1 overflow-y-auto p-3 space-y-1 custom-scrollbar">
        <div className="px-2 py-1 text-[10px] font-bold uppercase tracking-wider text-slate-500">Flujo de Procesamiento</div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return <NavLink key={item.to} to={item.to} className={({ isActive }) => `w-full flex items-center justify-between px-3 py-2 rounded-lg text-xs font-medium transition-all ${isActive ? 'bg-sky-600 text-white shadow-sm shadow-sky-600/30' : 'text-slate-300 hover:bg-slate-800 hover:text-white'}`}>
            {({ isActive }) => <><div className="flex items-center space-x-2.5 min-w-0"><Icon className={`w-4 h-4 shrink-0 ${isActive ? 'text-white' : 'text-slate-400'}`} /><span className="truncate">{item.label}</span></div>{item.stageNum && <span className={`text-[10px] px-1.5 py-0.5 rounded font-mono ${isActive ? 'bg-sky-700 text-sky-100' : 'bg-slate-800 text-slate-400'}`}>P{item.stageNum}</span>}</>}
          </NavLink>;
        })}
      </nav>

      <div className="p-3 border-t border-slate-800">
        <button onClick={() => void handleLogout()} className="w-full flex items-center gap-2.5 px-3 py-2 rounded-lg text-xs font-medium text-slate-300 hover:bg-rose-600/20 hover:text-rose-300 transition-colors" title="Cerrar sesión"><LogOut className="w-4 h-4" />Cerrar sesión</button>
      </div>
    </aside>
  );
};
