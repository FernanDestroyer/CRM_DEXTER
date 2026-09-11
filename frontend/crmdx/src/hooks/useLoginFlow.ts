import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { APP_ROUTES } from '@/router/routes';
import { authService } from '@/services/auth.service';
import { useAuth } from '@/context/AuthContext';

export function useLoginFlow() {
  const navigate = useNavigate();
  const { refresh } = useAuth();
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [step, setStep] = useState<'email' | 'otp' | 'pending'>('email');

  const submit = async () => {
    setIsLoading(true);
    setError('');
    try {
      const { data } = step === 'otp'
        ? await authService.verifyOtp(email, otp)
        : await authService.login(email);
      if (data.status === 'AUTHENTICATED') {
        await refresh();
        navigate(APP_ROUTES.PROYECTOS);
      } else if (data.status === 'OTP_REQUIRED') {
        setStep('otp');
      } else {
        setStep('pending');
      }
      setError(data.message || '');
    } catch (requestError: any) {
      setError(requestError.response?.data?.message || 'No se pudo conectar con el backend.');
    } finally {
      setIsLoading(false);
    }
  };

  const reset = () => { setStep('email'); setOtp(''); setError(''); };
  return { email, setEmail, otp, setOtp, error, isLoading, step, submit, reset };
}
