// src/pages/Audit/AuditPage.tsx
import { ShieldCheck } from 'lucide-react';
import { PendingPage } from '@/components/common/PendingPage';

export default function AuditPage() {
  return (
    <PendingPage
      title="Auditoría"
      description="Registro inmutable de acciones del sistema."
      icon={ShieldCheck}
    />
  );
}
