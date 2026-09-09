// src/router/AppRouter.tsx
import { Navigate } from 'react-router-dom';
import AppLayout from '@/components/layout/AppLayout';

import LoginPage from '@/pages/LoginPage';
import ProjectsPage from '@/pages/Projects/ProjectsPage';
import DatasetsPage from '@/pages/Datasets/DatasetsPage';
import MappingPage from '@/pages/Mapping/MappingPage';
import CleaningPage from '@/pages/Cleaning/CleaningPage';
import FusionPage from '@/pages/Fusion/FusionPage';
import AnalyticsPage from '@/pages/Analytics/AnalyticsPage';
import OpportunitiesPage from '@/pages/Opportunities/OpportunitiesPage';
import ModulesPage from '@/pages/Modules/ModulesPage';
import TeamPage from '@/pages/Team/TeamPage';
import AuditPage from '@/pages/Audit/AuditPage';

// Rutas exportadas para fácil acceso (usar siempre estas constantes, no strings sueltos)
export const ROUTES = {
  LOGIN: '/',
  APP: {
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
  },
} as const;

// eslint-disable-next-line react-refresh/only-export-components
export const routes = [
  {
    path: ROUTES.LOGIN,
    element: <LoginPage />,
  },
  {
    path: ROUTES.APP.ROOT,
    element: <AppLayout />,
    children: [
      { index: true, element: <Navigate to="proyectos" replace /> },
      { path: 'proyectos', element: <ProjectsPage /> },
      { path: 'datasets', element: <DatasetsPage /> },
      { path: 'mapeo', element: <MappingPage /> },
      { path: 'limpieza', element: <CleaningPage /> },
      { path: 'fusion', element: <FusionPage /> },
      { path: 'dashboard', element: <AnalyticsPage /> },
      { path: 'oportunidades', element: <OpportunitiesPage /> },
      { path: 'modulos-operativos', element: <ModulesPage /> },
      { path: 'usuarios', element: <TeamPage /> },
      { path: 'auditoria', element: <AuditPage /> },
    ],
  },
  {
    path: '*',
    element: <Navigate to={ROUTES.LOGIN} replace />,
  },
];
