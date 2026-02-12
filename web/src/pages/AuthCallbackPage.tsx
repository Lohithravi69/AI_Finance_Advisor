import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import toast from 'react-hot-toast';

export const AuthCallbackPage: React.FC = () => {
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const { login } = useAuthStore();

  useEffect(() => {
    handleAuthCallback();
  }, []);

  const handleAuthCallback = async () => {
    try {
      const urlParams = new URLSearchParams(window.location.search);
      const code = urlParams.get('code');
      const state = urlParams.get('state');
      const error = urlParams.get('error');
      const errorDescription = urlParams.get('error_description');

      if (error) {
        throw new Error(errorDescription || error);
      }

      if (!code) {
        throw new Error('No authorization code received');
      }

      // Verify state parameter for CSRF protection
      const storedState = sessionStorage.getItem('oauth_state');
      if (!state || state !== storedState) {
        throw new Error('Invalid state parameter');
      }

      // Clear stored state
      sessionStorage.removeItem('oauth_state');

      // Exchange authorization code for tokens
      const tokenResponse = await fetch('/api/auth/token', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          grant_type: 'authorization_code',
          code: code,
          redirect_uri: window.location.origin + '/auth/callback',
          client_id: 'aifa-web',
        }),
      });

      if (!tokenResponse.ok) {
        throw new Error('Failed to exchange authorization code for tokens');
      }

      const tokenData = await tokenResponse.json();
      const { access_token, refresh_token, id_token } = tokenData;

      // Decode JWT to get user info (simple decode, not secure validation)
      const payload = JSON.parse(atob(access_token.split('.')[1]));
      const user = {
        id: payload.sub,
        email: payload.email || '',
        fullName: payload.name || payload.preferred_username || '',
      };

      // Store tokens and user
      login(access_token, user);

      // Store refresh token securely
      localStorage.setItem('refresh_token', refresh_token);

      toast.success('Login successful!');
      navigate('/dashboard');
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Authentication failed';
      setError(message);
      toast.error(message);
      console.error('Auth callback error:', err);
    }
  };

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-red-50 to-red-100">
        <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-8">
          <div className="text-center">
            <div className="text-red-500 text-6xl mb-4">⚠️</div>
            <h1 className="text-2xl font-bold text-gray-900 mb-4">
              Authentication Failed
            </h1>
            <p className="text-gray-600 mb-6">{error}</p>
            <button
              onClick={() => navigate('/login')}
              className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg transition duration-200"
            >
              Try Again
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-8">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <h1 className="text-2xl font-bold text-gray-900 mb-4">
            Completing Login...
          </h1>
          <p className="text-gray-600">
            Please wait while we authenticate you.
          </p>
        </div>
      </div>
    </div>
  );
};
