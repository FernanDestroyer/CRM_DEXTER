// src/pages/Analytics/AnalyticsPage.tsx
import { BarChart3 } from 'lucide-react';
import { PendingPage } from '@/components/common/PendingPage';

export default function AnalyticsPage() {
  return (
    <PendingPage
      title="Dashboard Analítico"
      description="KPIs, gráficos e insights automáticos del proyecto."
      icon={BarChart3}
    />
  );
}
