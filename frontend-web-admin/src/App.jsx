import { BrowserRouter, Routes, Route } from 'react-router-dom';
import AdminLayout from './layout/AdminLayout';
import Dashboard from './pages/Dashboard';
import Animales from './pages/Animales';
import Usuarios from './pages/Usuarios';
import Refugios from './pages/Refugios';       
import Solicitudes from './pages/Solicitudes'; 
import Donaciones from './pages/Donaciones';   

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AdminLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="animales" element={<Animales />} />
          <Route path="usuarios" element={<Usuarios />} />
          <Route path="refugios" element={<Refugios />} />       
          <Route path="solicitudes" element={<Solicitudes />} /> 
          <Route path="donaciones" element={<Donaciones />} />   
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;