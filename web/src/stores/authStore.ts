import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface User {
  id: number;
  email: string;
  fullName: string;
}

interface AuthStore {
  token: string | null;
  refreshToken: string | null;
  user: User | null;
  isAuthenticated: boolean;
  setToken: (token: string) => void;
  setRefreshToken: (token: string) => void;
  setUser: (user: User) => void;
  logout: () => void;
  login: (token: string, refreshToken: string, user: User) => void;
  refreshAccessToken: () => Promise<boolean>;
  isTokenExpired: (token: string) => boolean;
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      token: null,
      refreshToken: null,
      user: null,
      isAuthenticated: false,

      setToken: (token: string) => set({ token }),

      setRefreshToken: (refreshToken: string) => set({ refreshToken }),

      setUser: (user: User) => set({ user, isAuthenticated: true }),

      login: (token: string, refreshToken: string, user: User) =>
        set({
          token,
          refreshToken,
          user,
          isAuthenticated: true,
        }),

      logout: () => {
        // Clear local storage
        localStorage.removeItem('refresh_token');
        // Clear session storage
        sessionStorage.removeItem('oauth_state');
        // Reset state
        set({
          token: null,
          refreshToken: null,
          user: null,
          isAuthenticated: false,
        });
      },

      isTokenExpired: (token: string) => {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          const currentTime = Date.now() / 1000;
          return payload.exp < currentTime;
        } catch {
          return true;
        }
      },

      refreshAccessToken: async () => {
        const { refreshToken } = get();
        if (!refreshToken) return false;

        try {
          const response = await fetch('/api/auth/token', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
              grant_type: 'refresh_token',
              refresh_token: refreshToken,
              client_id: 'aifa-web',
            }),
          });

          if (!response.ok) {
            // Refresh token expired, logout
            get().logout();
            return false;
          }

          const tokenData = await response.json();
          const { access_token } = tokenData;

          set({ token: access_token });
          return true;
        } catch {
          get().logout();
          return false;
        }
      },
    }),
    {
      name: 'auth-storage',
      // Only persist token and user, not refresh token (stored in localStorage separately)
      partialize: (state) => ({
        token: state.token,
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);
