// src/components/layout/Header.tsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { FileDown, LogOut, Building2, CheckCircle2, Circle } from 'lucide-react';
import { APP_ROUTES } from '@/router/routes';
import { useAuth } from '@/context/AuthContext';
import { useProjects } from '@/context/ProjectContext';

const stages = [
  { name: 'Carga', to: APP_ROUTES.DATASETS, completed: true },
  { name: 'Mapeo', to: APP_ROUTES.MAPEO, completed: true },
  { name: 'Fusión', to: APP_ROUTES.FUSION, completed: false },
  { name: 'Dashboard', to: APP_ROUTES.DASHBOARD, completed: false },
];

export const Header: React.FC = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const { activeProject } = useProjects();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <header className="h-14 bg-white border-b border-slate-200 px-6 flex items-center justify-between z-10 select-none">
      {/* Left Breadcrumbs & Project Status */}
      <div className="flex items-center space-x-4 min-w-0">
        <div className="flex items-center space-x-2 text-sm">
          <div className="flex items-center gap-1.5 text-slate-500 font-medium">
            <Building2 className="w-4 h-4 text-slate-400" />
            <span className="truncate max-w-[140px]">CRM Dexter</span>
          </div>
          <span className="text-slate-300">/</span>
          {activeProject ? (
            <div className="flex items-center space-x-2">
              <span className="font-semibold text-slate-900 truncate max-w-[200px]">
                {activeProject.nombre}
              </span>
              <span className="text-[11px] px-2 py-0.5 rounded-full font-medium bg-sky-50 text-sky-700 border border-sky-200">
                {activeProject.rubro.toUpperCase()}
              </span>
            </div>
          ) : (
            <span className="text-slate-400 italic">Sin proyecto activo</span>
          )}
        </div>

        {/* Visual Workflow Steps Bar */}
        <div className="hidden lg:flex items-center space-x-1 pl-4 border-l border-slate-200">
          {stages.map((st, idx) => (
            <button
              key={st.name}
              onClick={() => navigate(st.to)}
              className={`flex items-center space-x-1.5 px-2.5 py-1 rounded-md text-xs font-medium transition-colors ${
                st.completed
                  ? 'text-emerald-700 bg-emerald-50 hover:bg-emerald-100'
                  : 'text-slate-500 hover:text-slate-800'
              }`}
              title={`Ir a etapa: ${st.name}`}
            >
              {st.completed ? (
                <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
              ) : (
                <Circle className="w-3.5 h-3.5 text-slate-300" />
              )}
              <span>{st.name}</span>
              {idx < stages.length - 1 && <span className="text-slate-300 font-light ml-1">→</span>}
            </button>
          ))}
        </div>
      </div>

      {/* Right Action Buttons */}
      <div className="flex items-center space-x-3">
        <button
          onClick={() => navigate(APP_ROUTES.DASHBOARD)}
          className="flex items-center space-x-1.5 px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-xs font-medium transition-colors shadow-sm"
        >
          <FileDown className="w-4 h-4 text-sky-400" />
          <span>Exportar Informe PDF</span>
        </button>

        <div className="h-5 w-px bg-slate-200" />

        <div className="flex items-center space-x-2">
          <div className="text-right hidden sm:block">
            <p className="text-xs font-medium text-slate-900 leading-none">{user?.nombre}</p>
            <p className="text-[10px] text-slate-500 capitalize leading-tight mt-0.5">
              {user?.rol.replace('_', ' ')}
            </p>
          </div>

          <button
            onClick={handleLogout}
            className="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition-colors"
            title="Cerrar Sesión"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </header>
  );
};
