import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AdminLayout from './layout/AdminLayout';

// Adoptantes
import CreateAdoptante from './pages/adoptante/CreateAdoptante';
import AdoptantesList from './pages/adoptante/AdoptantesList';
import EditAdoptante from './pages/adoptante/EditAdoptante';

// Refugios (Importa tus nuevos componentes)
import CreateRefugio from './pages/refugio/CreateRefugio';
import RefugiosList from './pages/refugio/RefugiosList';
import EditRefugio from './pages/refugio/EditRefugio';
import AnimalesList from './pages/animal/AnimalesList';
import CreateAnimal from './pages/animal/CreateAnimal';
import EditAnimal from './pages/animal/EditAnimal';

import Dashboard from './pages/Dashboard';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<AdminLayout />}>
          <Route index element={<h1 className="text-2xl font-bold text-gray-700">Dashboard General</h1>} />
          
          <Route path="dashboard" element={<Dashboard />} />

          {/* Rutas Adoptantes */}
          <Route path="adoptantes" element={<AdoptantesList />} />
          <Route path="create" element={<CreateAdoptante />} /> {/* Ruta legacy que tenías */}
          <Route path="adoptantes/create" element={<CreateAdoptante />} /> {/* Ruta recomendada */}
          <Route path="adoptantes/editar/:id" element={<EditAdoptante />} />

          {/* RUTAS REFUGIOS */}
          <Route path="refugios" element={<RefugiosList />} />
          <Route path="refugios/create" element={<CreateRefugio />} />
          <Route path="refugios/editar/:id" element={<EditRefugio />} />

          <Route path="animales" element={<AnimalesList />} />
          <Route path="animales/create" element={<CreateAnimal />} />
          <Route path="animales/editar/:id" element={<EditAnimal />} />

        </Route>
      </Routes>
    </Router>
  );
}

export default App;