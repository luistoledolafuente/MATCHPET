import React from "react";
import { 
  PawPrint, ClipboardList, Gift, BarChart3, Clock, CheckCircle, MessageSquare 
} from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";

const mockStats = {
  mascotasEnAdopcion: 18,
  solicitudesPendientes: 5,
  adopcionesEsteMes: 3,
  donacionesUltimaSemana: 450,
};

const navCards = [
  { title: "Gestionar Mascotas", description: "Agrega, edita o elimina perfiles de mascotas.", Icon: PawPrint, color: "from-[#FDB2A0] to-[#FD7E6A]" },
  { title: "Revisar Solicitudes", description: "Evalúa procesos de adopción pendientes.", Icon: ClipboardList, color: "from-[#407581] to-[#316B7A]" },
  { title: "Control de Donaciones", description: "Administra las donaciones recibidas.", Icon: Gift, color: "from-[#BAE6FD] to-[#407581]" },
  { title: "Comunidad y Contacto", description: "Responde mensajes de adoptantes.", Icon: MessageSquare, color: "from-[#FFD6BA] to-[#FDB2A0]" },
];

const StatCard = ({ title, value, Icon, unit }) => (
  <div className="p-6 rounded-2xl shadow-md flex flex-col justify-between bg-white hover:scale-[1.03] transition-transform">
    <div className="flex items-center justify-between">
      <Icon className="w-10 h-10 text-[#316B7A]" />
      <span className="text-3xl md:text-4xl font-extrabold text-[#316B7A]">
        {value} <span className="text-base font-semibold">{unit}</span>
      </span>
    </div>
    <p className="mt-4 text-[#407581] font-semibold">{title}</p>
  </div>
);

const NavCard = ({ title, description, Icon, color }) => (
  <div className={`flex flex-col md:flex-row justify-between rounded-2xl overflow-hidden shadow-lg cursor-pointer hover:scale-[1.02] transition-transform bg-gradient-to-br ${color}`}>
    <div className="p-6 md:w-2/3 text-white">
      <h3 className="text-2xl font-bold mb-2">{title}</h3>
      <p className="opacity-90">{description}</p>
      <button className="mt-4 py-2 px-4 rounded-full bg-white bg-opacity-20 hover:bg-opacity-30 transition-colors text-sm font-bold">
        Ir a {title}
      </button>
    </div>
    <div className="md:w-1/3 flex justify-center items-center p-4 bg-white/10">
      <Icon className="w-16 h-16 opacity-90 text-white" />
    </div>
  </div>
);

export default function DashboardHome() {
  const { user } = useAuth();

  return (
    <div className="bg-gradient-to-b from-[#FFF7E6] to-[#FFE8C4] min-h-screen md:pl-72 p-8 font-sans">
      {/* Contenedor blanco central */}
      <div className="max-w-7xl mx-auto bg-white rounded-3xl shadow-xl p-8 space-y-12">

        {/* HEADER */}
        <header className="relative bg-[#316B7A] rounded-2xl overflow-hidden p-8 flex flex-col md:flex-row items-center justify-between text-white">
          <div>
            <h2 className="text-4xl md:text-5xl font-extrabold mb-2">¡Bienvenido, {user.nombreCompleto}!</h2>
            <p className="text-lg md:text-xl opacity-90">Gestiona tus mascotas, adopciones y donaciones desde un solo lugar.</p>
          </div>
          <img src="https://images.unsplash.com/photo-1592194996308-7b43878e84a6?auto=format&fit=crop&w=400&q=80" 
               alt="Mascotas felices" 
               className="mt-6 md:mt-0 w-64 h-40 object-cover rounded-xl shadow-lg"/>
          <div className="absolute -top-10 -right-10 w-48 h-48 bg-[#FDB2A0] rounded-full opacity-30 blur-3xl"></div>
          <div className="absolute -bottom-10 -left-10 w-72 h-72 bg-[#407581] rounded-full opacity-30 blur-3xl"></div>
        </header>

        {/* ESTADÍSTICAS */}
        <section>
          <h2 className="text-3xl font-bold text-[#407581] mb-6 flex items-center">
            <BarChart3 className="w-7 h-7 mr-3 text-[#FDB2A0]" /> Resumen de Actividad
          </h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            <StatCard title="Mascotas Disponibles" value={mockStats.mascotasEnAdopcion} unit="animales" Icon={PawPrint} />
            <StatCard title="Solicitudes Pendientes" value={mockStats.solicitudesPendientes} unit="pendientes" Icon={Clock} />
            <StatCard title="Adopciones (Mes)" value={mockStats.adopcionesEsteMes} unit="éxitos" Icon={CheckCircle} />
            <StatCard title="Donaciones (Semana)" value={mockStats.donacionesUltimaSemana} unit="$" Icon={Gift} />
          </div>
        </section>

        {/* NAV CARDS */}
        <section>
          <h2 className="text-3xl font-bold text-[#407581] mb-6">Funciones Principales</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
            {navCards.map((card, idx) => (
              <NavCard key={idx} {...card} />
            ))}
          </div>
        </section>

      </div>
    </div>
  );
}
