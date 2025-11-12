import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

export default function RefugioHeader() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="bg-[#BAE6FD] shadow-md">
      <nav className="container mx-auto px-6 py-4 flex justify-between items-center">
        <Link
          to="/dashboard/refugio"
          className="text-xl font-bold text-[#407581] flex items-center">
          <img src="/src/assets/images/logo_matchpet.png" 
              alt="Pata de mascota"
              className="w-26 h-12 mr-2" />
              MatchPet
        </Link>

        <div className="flex items-center space-x-6">
          <Link
            to="/dashboard/refugio/mis-mascotas"
            className="text-gray-600 hover:text-[#407581]"
          >
            Mis Mascotas
          </Link>
          <Link
            to="/dashboard/refugio/solicitudes"
            className="text-gray-600 hover:text-[#407581]"
          >
            Solicitudes
          </Link>
          <Link
            to="/dashboard/refugio/donaciones"
            className="text-gray-600 hover:text-[#407581]"
          >
            Donaciones
          </Link>
          <Link
            to="/dashboard/refugio/perfil"
            className="text-gray-600 hover:text-[#407581]"
          >
            Mi Perfil
          </Link>

          {/* Mostrar nombre del refugio o del usuario */}
          {user?.nombre_completo && (
            <span className="text-sm text-gray-700 font-medium">
              🏠 {user.nombre_completo}
            </span>
          )}

          <button
            onClick={handleLogout}
            className="bg-[#407581] text-white px-4 py-2 rounded-md hover:bg-[#5b8c99] transition-colors duration-150 font-semibold"
          >
            Cerrar Sesión
          </button>
        </div>
      </nav>
    </header>
  );
}
