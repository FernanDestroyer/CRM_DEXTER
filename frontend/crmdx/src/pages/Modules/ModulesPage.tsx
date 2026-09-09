// src/pages/Modules/ModulesPage.tsx
import { Table2 } from 'lucide-react';
import { PendingPage } from '@/components/common/PendingPage';

export default function ModulesPage() {
  return (
    <PendingPage
      title="Tablas Adaptativas"
      description="Módulos operativos configurables por rubro."
      icon={Table2}
    />
  );
}
