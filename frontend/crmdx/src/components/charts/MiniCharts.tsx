// src/components/charts/MiniCharts.tsx
// Gráficos SVG livianos, sin dependencias externas, alineados a la paleta
// de diseño del proyecto (slate / sky). Pensados para KPIs y paneles.
import React from 'react';

interface BarDatum {
  label: string;
  value: number;
}

export const BarChartMini: React.FC<{ data: BarDatum[]; color?: string; formatValue?: (v: number) => string }> = ({
  data,
  color = '#0284c7',
  formatValue = (v) => v.toLocaleString('es-PE'),
}) => {
  const max = Math.max(...data.map((d) => d.value), 1);
  return (
    <div className="space-y-3">
      {data.map((d) => (
        <div key={d.label} className="flex items-center gap-3">
          <span className="w-20 shrink-0 text-xs text-slate-500 truncate">{d.label}</span>
          <div className="flex-1 h-3 bg-slate-100 rounded-full overflow-hidden">
            <div
              className="h-full rounded-full transition-all"
              style={{ width: `${(d.value / max) * 100}%`, backgroundColor: color }}
            />
          </div>
          <span className="w-20 shrink-0 text-right text-xs font-semibold text-slate-700">
            {formatValue(d.value)}
          </span>
        </div>
      ))}
    </div>
  );
};

export const LineChartMini: React.FC<{ data: BarDatum[]; color?: string; height?: number }> = ({
  data,
  color = '#0284c7',
  height = 160,
}) => {
  const width = 560;
  const padding = 24;
  const max = Math.max(...data.map((d) => d.value), 1);
  const min = Math.min(...data.map((d) => d.value), 0);
  const range = max - min || 1;
  const stepX = (width - padding * 2) / Math.max(data.length - 1, 1);

  const points = data.map((d, i) => {
    const x = padding + i * stepX;
    const y = height - padding - ((d.value - min) / range) * (height - padding * 2);
    return { x, y, ...d };
  });

  const linePath = points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ');
  const areaPath = `${linePath} L ${points[points.length - 1].x} ${height - padding} L ${points[0].x} ${height - padding} Z`;

  return (
    <svg viewBox={`0 0 ${width} ${height}`} className="w-full h-auto" preserveAspectRatio="xMidYMid meet">
      <defs>
        <linearGradient id="lineFillGradient" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor={color} stopOpacity="0.25" />
          <stop offset="100%" stopColor={color} stopOpacity="0" />
        </linearGradient>
      </defs>
      {[0.25, 0.5, 0.75].map((f) => (
        <line
          key={f}
          x1={padding}
          x2={width - padding}
          y1={padding + f * (height - padding * 2)}
          y2={padding + f * (height - padding * 2)}
          stroke="#e2e8f0"
          strokeWidth={1}
        />
      ))}
      <path d={areaPath} fill="url(#lineFillGradient)" />
      <path d={linePath} fill="none" stroke={color} strokeWidth={2.5} strokeLinejoin="round" strokeLinecap="round" />
      {points.map((p) => (
        <circle key={p.label} cx={p.x} cy={p.y} r={3.5} fill="white" stroke={color} strokeWidth={2} />
      ))}
      {points.map((p) => (
        <text key={`${p.label}-lbl`} x={p.x} y={height - 4} textAnchor="middle" fontSize={10} fill="#64748b">
          {p.label}
        </text>
      ))}
    </svg>
  );
};

interface DonutDatum {
  label: string;
  value: number;
  color: string;
}

export const DonutChartMini: React.FC<{ data: DonutDatum[]; size?: number }> = ({ data, size = 150 }) => {
  const total = data.reduce((sum, d) => sum + d.value, 0) || 1;
  const radius = size / 2;
  const strokeWidth = size * 0.22;
  const innerRadius = radius - strokeWidth / 2;
  const circumference = 2 * Math.PI * innerRadius;

  let offsetAcc = 0;
  return (
    <div className="flex items-center gap-5">
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} className="shrink-0 -rotate-90">
        <circle cx={radius} cy={radius} r={innerRadius} fill="none" stroke="#f1f5f9" strokeWidth={strokeWidth} />
        {data.map((d) => {
          const fraction = d.value / total;
          const dash = fraction * circumference;
          const gap = circumference - dash;
          const el = (
            <circle
              key={d.label}
              cx={radius}
              cy={radius}
              r={innerRadius}
              fill="none"
              stroke={d.color}
              strokeWidth={strokeWidth}
              strokeDasharray={`${dash} ${gap}`}
              strokeDashoffset={-offsetAcc}
              strokeLinecap="butt"
            />
          );
          offsetAcc += dash;
          return el;
        })}
      </svg>
      <ul className="space-y-1.5 text-xs">
        {data.map((d) => (
          <li key={d.label} className="flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: d.color }} />
            <span className="text-slate-600">{d.label}</span>
            <span className="font-semibold text-slate-900">{Math.round((d.value / total) * 100)}%</span>
          </li>
        ))}
      </ul>
    </div>
  );
};
