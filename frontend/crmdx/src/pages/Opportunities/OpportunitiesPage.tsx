// src/pages/Opportunities/OpportunitiesPage.tsx
import { Lightbulb } from 'lucide-react';
import { PendingPage } from '@/components/common/PendingPage';

export default function OpportunitiesPage() {
  return (
    <PendingPage
      title="Oportunidades"
      description="Oportunidades de negocio detectadas automáticamente."
      icon={Lightbulb}
    />
  );
}
