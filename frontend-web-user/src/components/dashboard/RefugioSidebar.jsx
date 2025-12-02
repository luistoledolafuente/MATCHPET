import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { Home, Dog, FileText, Gift, User, LogOut, List } from "lucide-react";

export default function RefugioSidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const links = [
    { to: "/dashboard/refugio", icon: Home, label: "Home" },
    { to: "/dashboard/refugio/mis-mascotas", icon: Dog, label: "Mis Mascotas" },
    { to: "/dashboard/refugio/solicitudes", icon: FileText, label: "Solicitudes" },
    { to: "/dashboard/refugio/donaciones", icon: Gift, label: "Donaciones" },
    { to: "/dashboard/refugio/perfil", icon: User, label: "Mi Perfil" },
  ];

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
      <nav className="flex-1 px-6 py-6 space-y-3 overflow-y-auto" aria-label="Menú principal Refugio">
        {links.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            end={to === "/dashboard/refugio"} // Solo Home exacto
            className={({ isActive }) =>
              `flex items-center px-4 py-3 rounded-lg font-semibold transition-colors duration-200
              ${isActive ? "bg-[#B2EBF2] text-[#007C91]" : "text-[#007C91] hover:bg-[#E0F7FA] hover:text-[#007C91]"}`
            }
          >
            <Icon className="w-5 h-5 mr-3" />
            {label}
          </NavLink>
        ))}
      </nav>

      {/* Footer con logout */}
      <div className="px-6 py-4 border-t border-[#407581]">
        {user && (
          <span className="flex items-center text-[#407581] font-semibold mb-3 truncate">
            <User className="w-5 h-5 mr-2" />
            {`${user.nombre} ${user.apellidoPaterno || ""} ${user.apellidoMaterno || ""}`.trim()}
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
