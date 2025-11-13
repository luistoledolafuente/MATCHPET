import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import {
  Home,
  Heart,
  Gift,
  User,
  LogOut
} from "lucide-react";

export default function AdoptanteHeader() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <aside className="bg-[#BAE6FD] w-64 min-h-screen shadow-md flex flex-col">
      {/* Logo */}
      <div className="flex items-center px-6 py-4 border-b border-[#407581]">
        <img
          src="/src/assets/images/logo_matchpet.png"
          alt="Pata de mascota"
          className="w-10 h-10 mr-2"
        />
        <span className="text-xl font-bold text-[#407581]">MatchPet</span>
      </div>

      {/* Links de navegación */}
      <nav className="flex-1 px-4 py-6 space-y-2">
        <Link
          to="/dashboard/adoptante"
          className="flex items-center px-3 py-2 text-gray-700 rounded-md hover:bg-[#407581] hover:text-white transition-colors duration-150"
        >
          <Home className="w-5 h-5 mr-3" />
          Dashboard
        </Link>

        <Link
          to="/dashboard/adoptante/mascotas"
          className="flex items-center px-3 py-2 text-gray-700 rounded-md hover:bg-[#407581] hover:text-white transition-colors duration-150"
        >
          <Gift className="w-5 h-5 mr-3" />
          Mascotas
        </Link>

        <Link
          to="/dashboard/adoptante/favoritos"
          className="flex items-center px-3 py-2 text-gray-700 rounded-md hover:bg-[#407581] hover:text-white transition-colors duration-150"
        >
          <Heart className="w-5 h-5 mr-3" />
          Favoritos
        </Link>

        <Link
          to="/dashboard/adoptante/donaciones"
          className="flex items-center px-3 py-2 text-gray-700 rounded-md hover:bg-[#407581] hover:text-white transition-colors duration-150"
        >
          <Gift className="w-5 h-5 mr-3" />
          Donaciones
        </Link>

        <Link
          to="/dashboard/adoptante/perfil"
          className="flex items-center px-3 py-2 text-gray-700 rounded-md hover:bg-[#407581] hover:text-white transition-colors duration-150"
        >
          <User className="w-5 h-5 mr-3" />
          Mi Perfil
        </Link>
      </nav>

      {/* Usuario y Logout */}
      <div className="px-6 py-4 border-t border-[#407581]">
        {user?.nombre_completo && (
          <span className="block text-gray-700 font-medium mb-2">
            👤 {user.nombre_completo}
          </span>
        )}
        <button
          onClick={handleLogout}
          className="w-full flex items-center justify-center bg-[#407581] text-white px-4 py-2 rounded-md hover:bg-[#5b8c99] transition-colors duration-150 font-semibold"
        >
          <LogOut className="w-5 h-5 mr-2" />
          Cerrar Sesión
        </button>
      </div>
    </aside>
  );
}
