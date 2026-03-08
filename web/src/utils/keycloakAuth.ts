type AuthMode = 'login' | 'signup'

type AuthState = {
  nonce: string
  redirect: string
  mode: AuthMode
}

const buildState = (redirect: string, mode: AuthMode) => {
  const payload: AuthState = {
    nonce: Math.random().toString(36).slice(2),
    redirect,
    mode
  }

  return btoa(JSON.stringify(payload))
}

export const startKeycloakAuth = (mode: AuthMode, redirect = '/dashboard') => {
  // Use relative URL - nginx will proxy to Keycloak
  const keycloakUrl = '/kc'
  const realm = 'aifa'
  const clientId = 'aifa-web'
  const redirectUri = encodeURIComponent(window.location.origin + '/auth/callback')
  const state = buildState(redirect, mode)

  sessionStorage.setItem('oauth_state', state)

  const endpoint = mode === 'signup' ? 'registrations' : 'auth'
  const authUrl = `${keycloakUrl}/realms/${realm}/protocol/openid-connect/${endpoint}?` +
    `client_id=${clientId}&` +
    `redirect_uri=${redirectUri}&` +
    `response_type=code&` +
    `scope=openid profile email&` +
    `state=${encodeURIComponent(state)}`

  window.location.href = authUrl
}
