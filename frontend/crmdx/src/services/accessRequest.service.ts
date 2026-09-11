import { apiClient } from '@/services/api.config';
import type { AccessRequest } from '@/types/auth.types';

export const accessRequestService = {
  list: () => apiClient.get<AccessRequest[]>('/admin/solicitudes'),
  approve: (id: string, rol: string) => apiClient.post<AccessRequest>(`/admin/solicitudes/${id}/aprobar`, { rol }),
  reject: (id: string) => apiClient.post<AccessRequest>(`/admin/solicitudes/${id}/rechazar`),
};
