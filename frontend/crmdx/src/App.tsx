// src/App.tsx
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import { routes } from './router/AppRouter';
import { AuthProvider } from './context/AuthContext';
import { ProjectProvider } from './context/ProjectContext';

const router = createBrowserRouter(routes);

function App() {
  return <AuthProvider><ProjectProvider><RouterProvider router={router} /></ProjectProvider></AuthProvider>;
}

export default App;
