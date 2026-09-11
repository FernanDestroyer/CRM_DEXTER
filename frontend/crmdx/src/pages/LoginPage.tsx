// src/pages/LoginPage.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Database, CheckCircle2, Mail, ShieldCheck } from 'lucide-react';
import { APP_ROUTES } from '@/router/routes';
import { apiClient } from '@/services/api.config';

const stages = ['Carga', 'Mapeo', 'Fusión', 'Dashboard'];

export default function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    try {
      const { data } = await apiClient.post<{ token?: string; message: string }>('/auth/login', { email });
      if (!data.token) {
        setError(data.message || 'Tu correo todavía no tiene acceso aprobado.');
        return;
      }
      localStorage.setItem('crm_dexter_token', data.token);
      navigate(APP_ROUTES.PROYECTOS);
    } catch {
      setError('No se pudo conectar con el backend. Verifica que esté ejecutándose.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex bg-white">
      {/* Left brand panel */}
      <div className="hidden lg:flex lg:w-[420px] xl:w-[460px] shrink-0 bg-slate-900 text-slate-200 flex-col justify-between p-10">
        <div>
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-indigo-500 to-sky-500 flex items-center justify-center shadow-md shadow-indigo-500/20">
              <Database className="w-5 h-5 text-white" />
            </div>
            <div>
              <h1 style={{ fontFamily: 'Space Grotesk, sans-serif' }} className="font-bold text-lg tracking-tight text-white flex items-center gap-1.5">
                CRM DEXTER
                <span className="text-[10px] uppercase font-semibold px-1.5 py-0.5 bg-sky-500/20 text-sky-400 rounded border border-sky-500/30">
                  PRO
                </span>
              </h1>
              <p className="text-xs text-slate-400">Inteligencia de Datos</p>
            </div>
          </div>

          <h2 style={{ fontFamily: 'Space Grotesk, sans-serif' }} className="mt-16 text-3xl font-semibold text-white leading-snug max-w-[22ch]">
            Panel de proyectos, mapeo y análisis en un solo lugar.
          </h2>
          <p className="mt-4 text-sm text-slate-400 leading-relaxed max-w-[38ch]">
            Ingresa con tu correo para cargar datasets, fusionarlos y detectar oportunidades sobre tu proyecto.
          </p>

          <div className="mt-12 space-y-1">
            {stages.map((s, idx) => (
              <div key={s} className="flex items-center space-x-2.5 text-sm text-slate-400 py-1">
                <CheckCircle2 className="w-4 h-4 text-sky-500" />
                <span>{s}</span>
                {idx < stages.length - 1 && <span className="text-slate-600 font-light">→</span>}
              </div>
            ))}
          </div>
        </div>

        <div className="flex items-center space-x-2 text-xs text-slate-500">
          <ShieldCheck className="w-4 h-4 text-slate-600" />
          <span>Acceso protegido</span>
        </div>
      </div>

      {/* Right panel — login form */}
      <div className="flex-1 flex items-center justify-center bg-slate-50 px-5 py-10">
        <div className="w-full max-w-[400px]">
          {/* Mobile-only brand mark */}
          <div className="lg:hidden flex items-center space-x-3 mb-9">
            <div className="w-9 h-9 rounded-lg bg-gradient-to-br from-indigo-500 to-sky-500 flex items-center justify-center shadow-md shadow-indigo-500/20">
              <Database className="w-5 h-5 text-white" />
            </div>
            <h1 style={{ fontFamily: 'Space Grotesk, sans-serif' }} className="font-bold text-base tracking-tight text-slate-900 flex items-center gap-1.5">
              CRM DEXTER
              <span className="text-[10px] uppercase font-semibold px-1.5 py-0.5 bg-sky-50 text-sky-700 rounded border border-sky-200">
                PRO
              </span>
            </h1>
          </div>

          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-7">
            <h2 style={{ fontFamily: 'Space Grotesk, sans-serif' }} className="text-2xl font-semibold text-slate-900 mb-2">
              Ingresa con tu correo
            </h2>
            <p className="text-slate-500 text-sm leading-relaxed mb-7 max-w-[34ch]">
              Accede al panel para cargar datasets, fusionarlos y detectar oportunidades sobre tu proyecto.
            </p>

            <form onSubmit={handleSubmit}>
              <label htmlFor="email-input" className="block text-xs font-medium text-slate-600 mb-1.5">
                Correo electrónico
              </label>
              <div className="relative mb-1.5">
                <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
                <input
                  id="email-input"
                  type="email"
                  autoComplete="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="nombre@correo.com"
                  className="w-full text-sm pl-9 pr-3.5 py-2.5 border rounded-lg text-slate-900 focus:outline-none focus:ring-2 transition-colors border-slate-300 focus:ring-sky-500"
                />
              </div>
              <div className="text-xs text-slate-400 mb-6 mt-1.5">
                Ingresa tu correo para continuar al panel.
              </div>
              {error && <div className="text-xs text-rose-600 bg-rose-50 border border-rose-200 rounded-lg p-2 mb-4">{error}</div>}

              <button
                type="submit"
                disabled={isLoading || !email}
                className="w-full flex items-center justify-center px-5 py-2.5 bg-sky-600 hover:bg-sky-500 text-white text-sm font-semibold rounded-lg shadow-sm shadow-sky-600/20 transition-colors disabled:opacity-50"
              >
                {isLoading ? 'Ingresando...' : 'Ingresar'}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
