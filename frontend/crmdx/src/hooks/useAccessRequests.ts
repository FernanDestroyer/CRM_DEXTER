import { useEffect, useState } from 'react';
import { accessRequestService } from '@/services/accessRequest.service';
import type { AccessRequest } from '@/types/auth.types';

export function useAccessRequests() {
  const [requests, setRequests] = useState<AccessRequest[]>([]);
  const [roles, setRoles] = useState<Record<string, string>>({});
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try { setRequests((await accessRequestService.list()).data); }
    catch (requestError: any) { setError(requestError.response?.data?.message || 'No se pudieron cargar las solicitudes.'); }
    finally { setLoading(false); }
  };

  useEffect(() => { void load(); }, []);

  const decide = async (id: string, action: 'aprobar' | 'rechazar') => {
    try {
      if (action === 'aprobar') await accessRequestService.approve(id, roles[id] || 'ANALISTA');
      else await accessRequestService.reject(id);
      setRequests((current) => current.filter((request) => request.id !== id));
    } catch (requestError: any) { setError(requestError.response?.data?.message || 'No se pudo procesar la solicitud.'); }
  };

  return { requests, roles, setRoles, error, loading, decide };
}
