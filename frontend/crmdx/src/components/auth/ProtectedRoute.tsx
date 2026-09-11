import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';

export default function ProtectedRoute() {
  const { user, isLoading } = useAuth();
  if (isLoading) return <div className="min-h-screen grid place-items-center text-slate-500">Verificando sesión...</div>;
  return user ? <Outlet /> : <Navigate to="/" replace />;
}
