import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AdminLayout from './layout/AdminLayout';  // Layout con Sidebar
import CreateAdoptante from './pages/adoptante/CreateAdoptante';
import AdoptantesList from './pages/adoptante/AdoptantesList';
import EditAdoptante from './pages/adoptante/EditAdoptante';


function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<AdminLayout />}>
          {/* Ruta para el Dashboard */}
          <Route index element={<h1 className="text-2xl font-bold text-gray-700">Bienvenido al Dashboard</h1>} />
          
          {/* Rutas de Adoptantes */}
          <Route path="create" element={<CreateAdoptante />} />
          <Route path="adoptantes" element={<AdoptantesList />} />
          <Route path="adoptantes/editar/:id" element={<EditAdoptante />} />

        </Route>
      </Routes>
    </Router>
  );
}

export default App;
