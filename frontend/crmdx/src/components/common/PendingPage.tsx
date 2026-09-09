// src/components/common/PendingPage.tsx
import React from 'react';
import type { LucideIcon } from 'lucide-react';

interface PendingPageProps {
  title: string;
  description: string;
  icon: LucideIcon;
}

export const PendingPage: React.FC<PendingPageProps> = ({ title, description, icon: Icon }) => {
  return (
    <div className="bg-white rounded-xl border border-dashed border-slate-300 p-12 text-center max-w-xl mx-auto">
      <div className="w-14 h-14 bg-sky-50 text-sky-600 rounded-2xl flex items-center justify-center mx-auto mb-4">
        <Icon className="w-7 h-7" />
      </div>
      <h1 className="text-lg font-bold text-slate-900 mb-1">{title}</h1>
      <p className="text-sm text-slate-500">{description}</p>
      <p className="text-xs text-slate-400 mt-4">Diseño pendiente de portar desde el prototipo original.</p>
    </div>
  );
};
