import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const ProtectedRoute = ({ children }) => {
  // Solo necesitamos 'loading' e 'isAuthenticated'
  const { isAuthenticated, loading } = useAuth(); 

  // CRÍTICO: Si está cargando, retornamos el indicador de carga.
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        {/* Spinner simple de Tailwind/CSS */}
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#407581]"></div>
        <span className="ml-4 text-gray-600">Verificando sesión...</span>
      </div>
    );
  }

  // Si no está autenticado (y ya terminó de cargar), redirigir a login
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Si está autenticado y cargado, renderizamos el contenido
  return children;
};

export default ProtectedRoute;