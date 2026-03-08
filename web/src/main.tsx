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
import Budgets from './ui/pages/Budgets'
import { LoginPage } from './pages/LoginPage'
import { AuthCallbackPage } from './pages/AuthCallbackPage'
import { ProfilePage } from './pages/ProfilePage'
import { CategoriesPage } from './pages/CategoriesPage'
import AccountsPage from './pages/AccountsPage'
import IncomeSourcesPage from './pages/IncomeSourcesPage'
import { ProtectedRoute } from './components/ProtectedRoute'

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/auth/callback',
    element: <AuthCallbackPage />,
  },
  {
    path: '/profile',
    element: <ProtectedRoute><ProfilePage /></ProtectedRoute>,
  },
  {
    path: '/',
    element: <App />,
    children: [
      { index: true, element: <Dashboard /> },
      { path: 'dashboard', element: <Dashboard /> },
      { path: 'onboarding', element: <Onboarding /> },
      { path: 'expenses', element: <ProtectedRoute><Expenses /></ProtectedRoute> },
      { path: 'budgets', element: <ProtectedRoute><Budgets /></ProtectedRoute> },
      { path: 'goals', element: <ProtectedRoute><Goals /></ProtectedRoute> },
      { path: 'categories', element: <ProtectedRoute><CategoriesPage /></ProtectedRoute> },
      { path: 'accounts', element: <ProtectedRoute><AccountsPage /></ProtectedRoute> },
      { path: 'income-sources', element: <ProtectedRoute><IncomeSourcesPage /></ProtectedRoute> },
      { path: 'chat', element: <ProtectedRoute><Chat /></ProtectedRoute> },
    ]
  }
])

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
)
