// src/pages/Cleaning/CleaningPage.tsx
import { Activity } from 'lucide-react';
import { PendingPage } from '@/components/common/PendingPage';

export default function CleaningPage() {
  return (
    <PendingPage
      title="Limpieza de Datos"
      description="Aplica transformaciones de limpieza al dataset."
      icon={Activity}
    />
  );
}
