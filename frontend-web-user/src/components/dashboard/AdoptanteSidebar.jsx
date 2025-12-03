import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { Home, Heart, Gift, User, LogOut, Zap, ListOrdered } from "lucide-react"; 

export default function AdoptanteSidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const links = [
    { to: "/dashboard/adoptante", icon: Home, label: "Home" },
    { 
      to: "/dashboard/adoptante/recomendaciones", 
      icon: Zap, 
      label: "Match IA",
      style: "bg-yellow-100 text-yellow-800 hover:bg-yellow-200" // Estilo para destacarlo
    },
    { to: "/dashboard/adoptante/mascotas", icon: Gift, label: "Mascotas" },
    { to: "/dashboard/adoptante/favoritos", icon: Heart, label: "Favoritos" },
    { to: "/dashboard/adoptante/donaciones", icon: Gift, label: "Donaciones" },
    {to: "/dashboard/adoptante/mis-solicitudes", icon: ListOrdered, label: "Mis Solicitudes"},
    { to: "/dashboard/adoptante/perfil", icon: User, label: "Perfil" },
  ];

  return (
    <aside className="bg-white w-64 min-h-screen shadow-lg flex flex-col fixed left-0 top-0 h-full z-20" aria-label="Sidebar de navegación">
      <div className="flex items-center px-6 py-5 border-b border-[#407581]">
        <img
          src="/src/assets/images/logo_matchpet.png"
          alt="Pata de mascota"
          className="w-16 h-10 mr-3"
        />
        <span className="text-2xl font-extrabold text-[#407581] tracking-wide">MatchPet</span>
      </div>

      <nav className="flex-1 px-6 py-6 space-y-3 overflow-y-auto" aria-label="Menú principal">
        {links.map(({ to, icon: Icon, label, style }) => (
          <NavLink
            key={to}
            to={to}
            end={to === "/dashboard/adoptante"} 
            className={({ isActive }) =>
              `flex items-center px-4 py-3 rounded-lg font-semibold transition-colors duration-200
                ${isActive 
                  ? `bg-[#d0f0fd] text-[#407581] ${style}` // Estilo base si está activo
                  : `${style ? style : 'text-[#407581] hover:bg-[#e0f7ff]'}`}` // Estilo normal
            }
          >
            <Icon className="w-5 h-5 mr-3" />
            {label}
          </NavLink>
        ))}
      </nav>

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
