// src/pages/Analytics/AnalyticsPage.tsx
import React from 'react';
import {
  BarChart3,
  TrendingUp,
  TrendingDown,
  Lightbulb,
  AlertTriangle,
  Info,
  Trophy,
} from 'lucide-react';
import { BarChartMini, LineChartMini, DonutChartMini } from '@/components/charts/MiniCharts';
import {
  mockKpis,
  mockVentasPorSucursal,
  mockTendenciaMensual,
  mockDistribucionCategoria,
  mockInsights,
  mockTopVendedores,
} from '@/services/mockData';

const insightStyles: Record<string, { icon: React.ElementType; classes: string }> = {
  positivo: { icon: TrendingUp, classes: 'text-emerald-700 bg-emerald-50 border-emerald-200' },
  alerta: { icon: AlertTriangle, classes: 'text-amber-700 bg-amber-50 border-amber-200' },
  neutral: { icon: Info, classes: 'text-sky-700 bg-sky-50 border-sky-200' },
};

export default function AnalyticsPage() {
  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
        <div>
          <h1 className="text-xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <BarChart3 className="w-5 h-5 text-sky-600" />
            Panel Analítico
          </h1>
          <p className="text-xs text-slate-500 mt-1">KPIs, gráficos e insights automáticos del dataset maestro.</p>
        </div>
        <span className="text-[11px] font-medium text-slate-500 bg-slate-50 border border-slate-200 px-2.5 py-1 rounded-full">
          Basado en el dataset maestro · actualizado hace 12 min
        </span>
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {mockKpis.map((kpi) => (
          <div key={kpi.label} className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
            <p className="text-[11px] font-medium text-slate-500 uppercase tracking-wide">{kpi.label}</p>
            <p className="text-2xl font-bold text-slate-900 mt-1.5 tracking-tight">{kpi.value}</p>
            <div
              className={`inline-flex items-center gap-1 mt-2 text-[11px] font-semibold px-1.5 py-0.5 rounded ${
                kpi.positive ? 'text-emerald-700 bg-emerald-50' : 'text-rose-700 bg-rose-50'
              }`}
            >
              {kpi.positive ? <TrendingUp className="w-3 h-3" /> : <TrendingDown className="w-3 h-3" />}
              {kpi.delta}
              <span className="text-slate-400 font-normal ml-0.5">vs. trim. anterior</span>
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Tendencia mensual */}
        <div className="lg:col-span-2 bg-white rounded-xl border border-slate-200 shadow-sm p-5">
          <h2 className="text-sm font-bold text-slate-900 mb-1">Tendencia de Ingresos</h2>
          <p className="text-[11px] text-slate-500 mb-4">Ingreso consolidado por mes (S/), últimos 7 meses.</p>
          <LineChartMini data={mockTendenciaMensual} />
        </div>

        {/* Distribución por categoría */}
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
          <h2 className="text-sm font-bold text-slate-900 mb-1">Distribución por Categoría</h2>
          <p className="text-[11px] text-slate-500 mb-4">Participación sobre el total fusionado.</p>
          <DonutChartMini data={mockDistribucionCategoria} />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Ventas por sucursal */}
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
          <h2 className="text-sm font-bold text-slate-900 mb-1">Ingreso por Sucursal</h2>
          <p className="text-[11px] text-slate-500 mb-4">Comparativo entre fuentes fusionadas (S/).</p>
          <BarChartMini
            data={mockVentasPorSucursal}
            formatValue={(v) => `S/ ${(v / 1000).toFixed(0)}k`}
          />
        </div>

        {/* Insights automáticos */}
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
          <h2 className="text-sm font-bold text-slate-900 flex items-center gap-2 mb-4">
            <Lightbulb className="w-4 h-4 text-amber-500" />
            Insights Automáticos
          </h2>
          <ul className="space-y-2.5">
            {mockInsights.map((insight, i) => {
              const style = insightStyles[insight.tipo] ?? insightStyles.neutral;
              const Icon = style.icon;
              return (
                <li key={i} className={`flex items-start gap-2 text-xs p-2.5 rounded-lg border ${style.classes}`}>
                  <Icon className="w-3.5 h-3.5 shrink-0 mt-0.5" />
                  <span>{insight.texto}</span>
                </li>
              );
            })}
          </ul>
        </div>

        {/* Top vendedores */}
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="px-5 py-4 border-b border-slate-100">
            <h2 className="text-sm font-bold text-slate-900 flex items-center gap-2">
              <Trophy className="w-4 h-4 text-amber-500" />
              Top Vendedores
            </h2>
          </div>
          <div className="divide-y divide-slate-100">
            {mockTopVendedores.map((v, i) => (
              <div key={v.vendedor} className="flex items-center gap-3 px-5 py-3">
                <span
                  className={`w-6 h-6 rounded-full flex items-center justify-center text-[11px] font-bold shrink-0 ${
                    i === 0
                      ? 'bg-amber-100 text-amber-700'
                      : i === 1
                      ? 'bg-slate-200 text-slate-600'
                      : i === 2
                      ? 'bg-orange-100 text-orange-700'
                      : 'bg-slate-100 text-slate-500'
                  }`}
                >
                  {i + 1}
                </span>
                <div className="min-w-0 flex-1">
                  <p className="text-xs font-semibold text-slate-800 truncate">{v.vendedor}</p>
                  <p className="text-[10px] text-slate-400">
                    {v.sucursal} · {v.ventas} ventas
                  </p>
                </div>
                <span className="text-xs font-mono font-semibold text-slate-700 shrink-0">
                  S/ {v.monto.toLocaleString('es-PE')}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
