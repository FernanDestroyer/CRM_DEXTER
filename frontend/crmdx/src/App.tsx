// src/App.tsx
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import { routes } from './router/AppRouter';

const router = createBrowserRouter(routes);

function App() {
  return <RouterProvider router={router} />;
}

export default App;
