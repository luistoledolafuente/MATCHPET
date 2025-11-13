import { Routes, Route, Navigate } from "react-router-dom";
// Note: Elimino el useEffect sin usar, ya que no se utiliza en este componente
// import { useEffect } from "react"; 

// --- Páginas Públicas ---
import HomePage from "./pages/HomePage.jsx";
import NosotrosPage from "./pages/StaticPages/NosotrosPage.jsx";
import Blog from "./pages/StaticPages/Blog.jsx";
import Ayuda from "./pages/StaticPages/Ayuda.jsx";
import TerminosServicio from "./pages/StaticPages/TerminosServicio.jsx";
import PoliticaPrivacidad from "./pages/StaticPages/PoliticaPrivacidad.jsx";
import UnderConstructionPage from "./pages/UnderConstructionPage.jsx";

// --- Autenticación ---
import LoginPage from "./pages/auth/LoginPage.jsx";
import RegisterPage from "./pages/auth/RegisterPage.jsx";

// --- Dashboards Adoptante ---
import DashboardHomePage from "./pages/adoptante/DashboardHomePage.jsx";
import MascotasPage from "./pages/adoptante/MascotasPage.jsx";
import Favoritos from "./pages/adoptante/Favoritos.jsx";
import PerfilAdoptante from "./pages/adoptante/Perfil.jsx";
import Donaciones from "./pages/adoptante/Donaciones.jsx";

// --- Dashboards Refugio ---
import DashboardHomeRefugio from "./pages/refugio/DashboardHome.jsx";
import MisMascotas from "./pages/refugio/MisMascotas.jsx";
import DonacionesRecibidas from "./pages/refugio/DonacionesRecibidas.jsx";
import PerfilRefugio from "./pages/refugio/Perfil.jsx";
import NuevaMascota from "./pages/refugio/NuevaMascota.jsx";

// --- Layouts ---
import MainLayout from "./layouts/MainLayout.jsx";
import DashboardLayout from "./layouts/DashboardLayout.jsx";

// --- Componentes de Seguridad ---
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import { useAuth } from "./contexts/AuthContext.jsx";


// ------------------ DASHBOARD ROUTES ------------------
function DashboardRoutes() {
  const { user, userType, loading } = useAuth();

  // El cálculo de la ruta se mantiene
  const navigateTo = userType === "refugio" ? "refugio" : "adoptante";

  // ❌ ELIMINADO: Se quitó el useEffect que generaba el bucle infinito ❌

  if (loading) {
    return <p className="text-center mt-10 text-gray-600">Cargando tu perfil...</p>;
  }

  if (!user) {
    // Si no hay usuario, redirigir a login
    return <Navigate to="/login" replace />;
  }

  return (
    <Routes>
      {/*         Esta es la ruta base /dashboard/*
        El ProtectedRoute envuelve el layout y protege las subrutas.
      */}
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        {/*           ✨ CAMBIO CLAVE: Usamos el índice para redirigir 
          Si el usuario visita /dashboard (la ruta index), es inmediatamente redirigido 
          a /dashboard/refugio o /dashboard/adoptante. 
        */}
        <Route index element={<Navigate to={navigateTo} replace />} />


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
          <Route path="mascotas/nueva" element={<NuevaMascota />} />
        </Route>
      </Route>
    </Routes>
  );
}


// ------------------ APP PRINCIPAL ------------------
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