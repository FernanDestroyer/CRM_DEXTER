import { apiClient } from '@/services/api.config';
import type { Proyecto } from '@/types/project.types';

export const projectService = {
  list: () => apiClient.get<Proyecto[]>('/proyectos'),
};
