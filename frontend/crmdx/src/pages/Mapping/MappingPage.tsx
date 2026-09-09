// src/pages/Mapping/MappingPage.tsx
import { Sparkles } from 'lucide-react';
import { PendingPage } from '@/components/common/PendingPage';

export default function MappingPage() {
  return (
    <PendingPage
      title="Mapeo Semántico"
      description="Compara y mapea columnas entre datasets."
      icon={Sparkles}
    />
  );
}
