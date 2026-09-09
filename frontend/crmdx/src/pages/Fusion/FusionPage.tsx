// src/pages/Fusion/FusionPage.tsx
import { GitMerge } from 'lucide-react';
import { PendingPage } from '@/components/common/PendingPage';

export default function FusionPage() {
  return (
    <PendingPage
      title="Fusión & Master"
      description="Consolida los datasets en un dataset maestro."
      icon={GitMerge}
    />
  );
}
