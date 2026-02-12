import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import toast from 'react-hot-toast';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const { login } = useAuthStore();

  const handleLogin = async () => {
    setIsLoading(true);
    setError('');

    try {
      // Production OAuth2 flow with Keycloak
      const keycloakUrl = 'http://localhost:8888';
      const realm = 'aifa';
      const clientId = 'aifa-web';
      const redirectUri = encodeURIComponent(window.location.origin + '/auth/callback');

      // Generate state for CSRF protection
      const state = btoa(JSON.stringify({
        nonce: Math.random().toString(36).substring(2),
        redirect: '/dashboard'
      }));

      // Store state in sessionStorage
      sessionStorage.setItem('oauth_state', state);

      // Redirect to Keycloak login
      const loginUrl = `${keycloakUrl}/realms/${realm}/protocol/openid-connect/auth?` +
        `client_id=${clientId}&` +
        `redirect_uri=${redirectUri}&` +
        `response_type=code&` +
        `scope=openid profile email&` +
        `state=${encodeURIComponent(state)}`;

      window.location.href = loginUrl;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Login failed';
      setError(message);
      toast.error(message);
      setIsLoading(false);
    }
  };

  const handleSignup = async () => {
    setIsLoading(true);
    setError('');

    try {
      // Production OAuth2 flow with Keycloak - registration
      const keycloakUrl = 'http://localhost:8888';
      const realm = 'aifa';
      const clientId = 'aifa-web';
      const redirectUri = encodeURIComponent(window.location.origin + '/auth/callback');

      // Generate state for CSRF protection
      const state = btoa(JSON.stringify({
        nonce: Math.random().toString(36).substring(2),
        redirect: '/dashboard'
      }));

      // Store state in sessionStorage
      sessionStorage.setItem('oauth_state', state);

      // Redirect to Keycloak registration
      const signupUrl = `${keycloakUrl}/realms/${realm}/protocol/openid-connect/registrations?` +
        `client_id=${clientId}&` +
        `redirect_uri=${redirectUri}&` +
        `response_type=code&` +
        `scope=openid profile email&` +
        `state=${encodeURIComponent(state)}`;

      window.location.href = signupUrl;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Signup failed';
      setError(message);
      toast.error(message);
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2 text-center">
          AI Finance Advisor
        </h1>
        <p className="text-gray-600 text-center mb-8">
          Login to manage your finances
        </p>

        {error && (
          <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded text-red-700">
            {error}
          </div>
        )}

        <div className="space-y-4">
          <button
            onClick={handleLogin}
            disabled={isLoading}
            className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white font-bold py-2 px-4 rounded-lg transition duration-200"
          >
            {isLoading ? 'Redirecting...' : 'Login with Keycloak'}
          </button>

          <button
            onClick={handleSignup}
            disabled={isLoading}
            className="w-full bg-green-600 hover:bg-green-700 disabled:bg-gray-400 text-white font-bold py-2 px-4 rounded-lg transition duration-200"
          >
            {isLoading ? 'Redirecting...' : 'Sign Up with Keycloak'}
          </button>
        </div>

        <div className="mt-6 text-center text-sm text-gray-600">
          <p>
            Secure login powered by Keycloak OAuth2
          </p>
        </div>

        <div className="mt-6 pt-6 border-t border-gray-200">
          <div className="bg-green-50 p-4 rounded-lg text-sm text-gray-700">
            <p className="font-semibold mb-2">✅ Production Features:</p>
            <ul className="list-disc list-inside space-y-1">
              <li>OAuth2 via Keycloak</li>
              <li>JWT token validation</li>
              <li>Secure session management</li>
              <li>Automatic token refresh</li>
              <li>Protected API endpoints</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};
