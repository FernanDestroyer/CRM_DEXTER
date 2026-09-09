// src/pages/Datasets/DatasetsPage.tsx
import { Database } from 'lucide-react';
import { PendingPage } from '@/components/common/PendingPage';

export default function DatasetsPage() {
  return (
    <PendingPage
      title="Carga de Datasets"
      description="Sube archivos CSV/XLSX para este proyecto."
      icon={Database}
    />
  );
}
