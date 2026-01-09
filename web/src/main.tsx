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

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      { index: true, element: <Dashboard /> },
      { path: 'onboarding', element: <Onboarding /> },
      { path: 'expenses', element: <Expenses /> },
      { path: 'goals', element: <Goals /> },
      { path: 'chat', element: <Chat /> },
    ]
  }
])

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
)
