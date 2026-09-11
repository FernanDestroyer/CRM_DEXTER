import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import { projectService } from '@/services/project.service';
import { useAuth } from '@/context/AuthContext';
import type { Proyecto } from '@/types/project.types';

interface ProjectContextValue {
  projects: Proyecto[];
  activeProject: Proyecto | null;
  activeProjectId: string | null;
  isLoading: boolean;
  refresh: () => Promise<void>;
  selectProject: (id: string) => void;
}

const ProjectContext = createContext<ProjectContextValue | null>(null);

export function ProjectProvider({ children }: { children: ReactNode }) {
  const { user } = useAuth();
  const [projects, setProjects] = useState<Proyecto[]>([]);
  const [activeProjectId, setActiveProjectId] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const refresh = async () => {
    if (!user) {
      setProjects([]);
      setActiveProjectId(null);
      return;
    }
    setIsLoading(true);
    try {
      const { data } = await projectService.list();
      setProjects(data);
      setActiveProjectId((current) => data.some((project) => project.id === current) ? current : data[0]?.id ?? null);
    } catch {
      setProjects([]);
      setActiveProjectId(null);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => { void refresh(); }, [user]);

  const activeProject = projects.find((project) => project.id === activeProjectId) ?? null;
  return <ProjectContext.Provider value={{ projects, activeProject, activeProjectId, isLoading, refresh, selectProject: setActiveProjectId }}>{children}</ProjectContext.Provider>;
}

export function useProjects() {
  const context = useContext(ProjectContext);
  if (!context) throw new Error('useProjects debe utilizarse dentro de ProjectProvider');
  return context;
}
