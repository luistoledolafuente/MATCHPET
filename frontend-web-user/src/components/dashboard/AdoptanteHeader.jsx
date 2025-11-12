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
    <header className="bg-[#BAE6FD] shadow-md">
      <nav className="container mx-auto px-6 py-2 flex justify-between items-center">
        <Link
          to="/dashboard/adoptante"
          className="text-xl font-bold text-[#407581] flex items-center"
        >
          <img
            src="/src/assets/images/logo_matchpet.png" 
            alt="Pata de mascota"
            className="w-26 h-12 mr-2"
          />
          MatchPet
        </Link>


        <div className="flex items-center space-x-6">
          <Link
            to="/dashboard/adoptante/mascotas"
            className="text-gray-600 hover:text-[#407581] transition-colors duration-150"
          >
            Mascotas
          </Link>
          <Link
            to="/dashboard/adoptante/favoritos"
            className="text-gray-600 hover:text-[#407581] transition-colors duration-150"
          >
            Favoritos
          </Link>
          <Link
            to="/dashboard/adoptante/donaciones"
            className="text-gray-600 hover:text-[#407581] transition-colors duration-150"
          >
            Donaciones
          </Link>
          <Link
            to="/dashboard/adoptante/perfil"
            className="text-gray-600 hover:text-[#407581] transition-colors duration-150"
          >
            Mi Perfil
          </Link>

          {/* Mostrar nombre si está disponible */}
          {user?.nombre_completo && (
            <span className="text-sm text-gray-700 font-medium hidden sm:inline">
              👤 {user.nombre_completo}
            </span>
          )}

          <button
            onClick={handleLogout}
            // CLASES CORREGIDAS: Usamos el color de marca (azul verdoso)
            className="bg-[#407581] text-white px-4 py-2 rounded-md hover:bg-[#5b8c99] transition-colors duration-150 font-semibold"
          >
            Cerrar Sesión
          </button>
        </div>
      </nav>
    </header>
  );
}