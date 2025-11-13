import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import {
  Home,
  PawPrint,
  FileText,
  Gift,
  User,
  LogOut
} from "lucide-react";

export default function RefugioSidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <aside
      className="bg-white w-64 min-h-screen shadow-lg flex flex-col fixed left-0 top-0 h-full z-20"
      aria-label="Sidebar Refugio"
    >
      {/* Logo */}
      <div className="flex items-center px-6 py-5 border-b border-[#007C91]">
        <img
          src="/src/assets/images/logo_matchpet.png"
          alt="Logo MatchPet"
          className="w-16 h-10 mr-3"
        />
        <span className="text-2xl font-extrabold text-[#007C91] tracking-wide">
          Refugio
        </span>
      </div>

      {/* Links de navegación */}
      <nav
        className="flex-1 px-6 py-6 space-y-3 overflow-y-auto"
        aria-label="Menú principal Refugio"
      >
        {[
          { to: "/dashboard/refugio", icon: Home, label: "Home" },
          { to: "/dashboard/refugio/mis-mascotas", icon: PawPrint, label: "Mis Mascotas" },
          { to: "/dashboard/refugio/solicitudes", icon: FileText, label: "Solicitudes" },
          { to: "/dashboard/refugio/donaciones", icon: Gift, label: "Donaciones" },
          { to: "/dashboard/refugio/perfil", icon: User, label: "Mi Perfil" }
        ].map(({ to, icon: Icon, label }) => (
          <Link
            key={to}
            to={to}
            className="flex items-center px-4 py-3 text-[#007C91] rounded-lg hover:bg-[#B2EBF2] hover:text-white transition-colors duration-200 font-semibold shadow-sm"
          >
            <Icon className="w-5 h-5 mr-3" />
            {label}
          </Link>
        ))}
      </nav>

      <div className="px-6 py-4 border-t border-[#407581]">
        {user?.nombreCompleto && (
          <span className="flex items-center text-[#407581] font-semibold mb-3 truncate">
            <User className="w-5 h-5 mr-2" />
            {user.nombreCompleto}
          </span>
        )}
        <button
          onClick={handleLogout}
          className="w-full flex items-center justify-center bg-[#407581] text-white px-4 py-2 rounded-lg hover:bg-[#2e5d6e] transition duration-200 font-semibold shadow-md"
        >
          <LogOut className="w-5 h-5 mr-2" />
          Cerrar Sesión
        </button>
      </div>
    </aside>
  );
}
