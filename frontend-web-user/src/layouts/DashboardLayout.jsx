import React from "react";
import { Outlet } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

import AdoptanteHeader from "../components/dashboard/AdoptanteHeader";
import RefugioHeader from "../components/dashboard/RefugioHeader";


// Loader opcional mientras se verifica el tipo de usuario
const DashboardLayout = () => {
const { user, userType, profileLoading } = useAuth();

// 1. Muestra un loader mientras se carga el perfil (Necesario, ya que ProtectedRoute podría haber retornado null)
if (profileLoading) {
return (
<div className="flex items-center justify-center min-h-screen bg-gray-50">
<div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#407581]"></div>
</div>
);
}

// 2. Si no se pudo determinar el tipo de usuario (aunque AuthContext ya debe manejar esto)
if (!userType) {
return (
<div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 text-gray-700">
<p className="text-lg">Error de sesión. Por favor, inicia sesión nuevamente.</p>
</div>
);
}

return (
  <div className="min-h-screen flex flex-col">
    {/* Header según el tipo de usuario */}
    {userType === "refugio" ? (
      <RefugioHeader user={user} /> 
    ) : (
      <AdoptanteHeader user={user} />
    )}

    {/* Contenido principal */}
    <main className="flex-1 w-full bg-transparent">
      <div className="w-full">
        <Outlet />
      </div>
    </main>
  </div>
);
}
export default DashboardLayout;