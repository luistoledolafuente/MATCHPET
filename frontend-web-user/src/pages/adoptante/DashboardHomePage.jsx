import React from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext"; // Ajusta la ruta según tu proyecto

export default function DashboardHomePage() {
  const { user } = useAuth(); // Supongamos que user tiene { name }

  return (
    <div className="bg-gradient-to-b from-[#BAE6FD] via-[#FFF7E6] to-[#FFF7E6] min-h-screen p-8">
      {/* Bienvenida */}
      <div className="text-center mb-12">
        <h1 className="text-5xl font-extrabold text-[#316B7A] mb-4">
          Bienvenido a MatchPet{user?.name ? `, ${user.name}` : ""}
        </h1>
        <p className="text-gray-700 text-lg max-w-2xl mx-auto">
          Encuentra a tu compañero ideal. Nuestra misión es conectar a las mascotas necesitadas con hogares amorosos a través de la tecnología. 
          Creemos que cada mascota merece una segunda oportunidad y que cada persona merece un compañero leal.
        </p>
      </div>

      {/* Accesos rápidos */}
      <div className="flex justify-center gap-6 mb-16 flex-wrap">
        <Link
          to="/dashboard/mascotas"
          className="px-6 py-3 bg-[#316B7A] text-white rounded-xl hover:bg-teal-800 transition-colors"
        >
          Ver Mascotas
        </Link>
        <Link
          to="/dashboard/refugios"
          className="px-6 py-3 bg-[#FDB2A0] text-white rounded-xl hover:bg-coral-600 transition-colors"
        >
          Ver Refugios
        </Link>
        <Link
          to="/dashboard/perfil"
          className="px-6 py-3 bg-white text-[#316B7A] border border-[#316B7A] rounded-xl hover:bg-teal-80 transition-colors"
        >
          Mi Perfil
        </Link>
        <Link
          to="/donar"
          className="px-6 py-3 bg-[#FDE68A] text-[#316B7A] rounded-xl hover:bg-yellow-400 transition-colors"
        >
          Donar
        </Link>
      </div>

      {/* Información / Features */}
      <div className="grid md:grid-cols-3 gap-10 max-w-7xl mx-auto">
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-xl font-semibold mb-2">Búsqueda Inteligente</h3>
          <p className="text-gray-600">
            Nuestro sistema de recomendación por IA te ayuda a encontrar la mascota perfecta para tu estilo de vida.
          </p>
        </div>
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-xl font-semibold mb-2">Refugios Verificados</h3>
          <p className="text-gray-600">
            Trabajamos con refugios de confianza para garantizar el bienestar de todas las mascotas.
          </p>
        </div>
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-xl font-semibold mb-2">Adopción Responsable</h3>
          <p className="text-gray-600">
            Promovemos la adopción responsable para asegurar que cada mascota encuentre un hogar para siempre.
          </p>
        </div>
      </div>

      {/* Extra info */}
      <div className="mt-16 text-center text-gray-500 text-sm">
        <p>Términos de Servicio | Política de Privacidad | Contacto</p>
        <p>© 2025 MatchPet. Todos los derechos reservados.</p>
      </div>
    </div>
  );
}
