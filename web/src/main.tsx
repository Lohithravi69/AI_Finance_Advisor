import React from 'react'
import ReactDOM from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import './index.css'
import App from './ui/App'
import Dashboard from './ui/pages/Dashboard'
import Onboarding from './ui/pages/Onboarding'
import Expenses from './ui/pages/Expenses'
import Goals from './ui/pages/Goals'
import Chat from './ui/pages/Chat'
import { LoginPage } from './pages/LoginPage'
import { ProfilePage } from './pages/ProfilePage'
import { ProtectedRoute } from './components/ProtectedRoute'

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/profile',
    element: <ProtectedRoute><ProfilePage /></ProtectedRoute>,
  },
  {
    path: '/',
    element: <App />,
    children: [
      { index: true, element: <ProtectedRoute><Dashboard /></ProtectedRoute> },
      { path: 'onboarding', element: <Onboarding /> },
      { path: 'expenses', element: <ProtectedRoute><Expenses /></ProtectedRoute> },
      { path: 'goals', element: <ProtectedRoute><Goals /></ProtectedRoute> },
      { path: 'chat', element: <ProtectedRoute><Chat /></ProtectedRoute> },
    ]
  }
])

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
)
