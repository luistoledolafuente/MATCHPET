import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

export default function AdoptanteHeader() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="bg-white shadow-md">
      <nav className="container mx-auto px-6 py-4 flex justify-between items-center">
        <Link
          to="/dashboard/adoptante"
          className="text-xl font-bold text-[#407581]"
        >
          🐾 MatchPet
        </Link>

        <div className="flex items-center space-x-6">
          <Link
            to="/dashboard/adoptante/mascotas"
            className="text-gray-600 hover:text-[#407581]"
          >
            Mascotas
          </Link>
          <Link
            to="/dashboard/adoptante/favoritos"
            className="text-gray-600 hover:text-[#407581]"
          >
            Favoritos
          </Link>
          <Link
            to="/dashboard/adoptante/donaciones"
            className="text-gray-600 hover:text-[#407581]"
          >
            Donaciones
          </Link>
          <Link
            to="/dashboard/adoptante/perfil"
            className="text-gray-600 hover:text-[#407581]"
          >
            Mi Perfil
          </Link>

          {/* Mostrar nombre si está disponible */}
          {user?.nombre_completo && (
            <span className="text-sm text-gray-700 font-medium">
              👤 {user.nombre_completo}
            </span>
          )}

          <button
            onClick={handleLogout}
            className="bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600"
          >
            Cerrar Sesión
          </button>
        </div>
      </nav>
    </header>
  );
}
