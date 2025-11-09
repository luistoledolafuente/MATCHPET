import { Routes, Route, Navigate } from "react-router-dom";

// --- Páginas Públicas ---
import HomePage from "./pages/HomePage";
import NosotrosPage from "./pages/StaticPages/NosotrosPage";
import Blog from "./pages/StaticPages/Blog";
import Ayuda from "./pages/StaticPages/Ayuda";
import TerminosServicio from "./pages/StaticPages/TerminosServicio";
import PoliticaPrivacidad from "./pages/StaticPages/PoliticaPrivacidad";
import UnderConstructionPage from "./pages/UnderConstructionPage";

// --- Autenticación ---
import LoginPage from "./pages/auth/LoginPage";
import RegisterPage from "./pages/auth/RegisterPage";

// --- Dashboards Adoptante ---
import DashboardHomePage from "./pages/adoptante/DashboardHomePage";
import MascotasPage from "./pages/adoptante/MascotasPage";
import Favoritos from "./pages/adoptante/Favoritos";
import PerfilAdoptante from "./pages/adoptante/Perfil";
import Donaciones from "./pages/adoptante/Donaciones";

// --- Dashboards Refugio ---
import DashboardHomeRefugio from "./pages/refugio/DashboardHome";
import MisMascotas from "./pages/refugio/MisMascotas";
import DonacionesRecibidas from "./pages/refugio/DonacionesRecibidas";
import PerfilRefugio from "./pages/refugio/Perfil";

// --- Layouts ---
import MainLayout from "./layouts/MainLayout";
import DashboardLayout from "./layouts/DashboardLayout";

// --- Componentes de Seguridad ---
import ProtectedRoute from "./components/ProtectedRoute";
import { useAuth } from "./contexts/AuthContext";

function DashboardRoutes() {
  // ✅ CORRECCIÓN: Usamos userType y loading
  const { user, userType, loading } = useAuth(); 

  if (loading) {
    return <p className="text-center mt-10 text-gray-600">Cargando tu perfil...</p>;
  }

  // Si no hay usuario después de cargar, redirigir
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // El rol ya viene limpio ('adoptante' o 'refugio') desde AuthContext
  const role = userType; 

  return (
    <Routes>
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        {/* Redirigir según el tipo de usuario */}
        <Route
          index
          element={
            // ✅ Redirige usando el 'role' simplificado
            role === "refugio" ? (
              <Navigate to="/dashboard/refugio" replace />
            ) : (
              <Navigate to="/dashboard/adoptante" replace />
            )
          }
        />

        {/* Adoptante */}
        <Route path="adoptante">
          <Route index element={<DashboardHomePage />} />
          <Route path="mascotas" element={<MascotasPage />} />
          <Route path="favoritos" element={<Favoritos />} />
          <Route path="perfil" element={<PerfilAdoptante />} />
          <Route path="donaciones" element={<Donaciones />} />
        </Route>

        {/* Refugio */}
        <Route path="refugio">
          <Route index element={<DashboardHomeRefugio />} />
          <Route path="mis-mascotas" element={<MisMascotas />} />
          <Route path="donaciones" element={<DonacionesRecibidas />} />
          <Route path="perfil" element={<PerfilRefugio />} />
        </Route>
      </Route>
    </Routes>
  );
}

export default function App() {
  return (
    <Routes>
      {/* Rutas Públicas */}
      <Route element={<MainLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/sobre-nosotros" element={<NosotrosPage />} />
        <Route path="/nosotros" element={<NosotrosPage />} />
        <Route path="/blog" element={<Blog />} />
        <Route path="/ayuda" element={<Ayuda />} />
        <Route path="/politica-privacidad" element={<PoliticaPrivacidad />} />
        <Route path="/terminos-servicio" element={<TerminosServicio />} />
        <Route path="/adoptar" element={<UnderConstructionPage />} />
        <Route path="/donar" element={<UnderConstructionPage />} />
        <Route path="/refugio" element={<UnderConstructionPage />} />
      </Route>

      {/* Autenticación */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/registro" element={<RegisterPage />} />

      {/* Dashboard */}
      <Route path="/dashboard/*" element={<DashboardRoutes />} />

      {/* Catch-all */}
      <Route
        path="*"
        element={
          <MainLayout>
            <UnderConstructionPage />
          </MainLayout>
        }
      />
    </Routes>
  );
}