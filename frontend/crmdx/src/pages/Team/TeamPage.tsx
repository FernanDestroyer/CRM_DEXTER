// src/pages/Team/TeamPage.tsx
import { Users } from 'lucide-react';
import { PendingPage } from '@/components/common/PendingPage';

export default function TeamPage() {
  return (
    <PendingPage
      title="Equipo & Permisos"
      description="Gestión de usuarios, roles y permisos granulares."
      icon={Users}
    />
  );
}
