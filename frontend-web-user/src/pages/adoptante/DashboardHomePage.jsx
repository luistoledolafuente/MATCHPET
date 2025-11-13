import React from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { Search, MapPin, Gift, Heart, Users } from "lucide-react"; 

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPaw } from '@fortawesome/free-solid-svg-icons'; 

export default function DashboardHome() {
  const { user } = useAuth(); 
  const userName = user?.nombreCompleto || "Adoptante";

  return (
    <div className="bg-[#FFF7E6] min-h-screen p-8"> 
      <div className="bg-white rounded-xl p-8 shadow-xl max-w-7xl mx-auto">
        <div className="flex justify-between items-center mb-6 border-b pb-4 border-gray-100">
          <h2 className="text-3xl font-extrabold text-[#316B7A]">
            ¡Hola, {userName}! 🐾
          </h2>
          <div className="flex items-center space-x-4">
            <p className="text-sm text-gray-500 flex items-center">
                <MapPin className="w-4 h-4 mr-1 text-[#FDB2A0]" />
                Tu Ubicación
            </p>
          </div>
        </div>

        {/* Barra de Búsqueda */}
        <div className="mb-8">
            <div className="relative">
                <Search className="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                <input
                    type="text"
                    placeholder="Buscar mascota por tipo, refugio o raza..."
                    className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-[#316B7A] focus:border-[#316B7A] transition-colors bg-[#BAE6FD]/30"
                />
            </div>
        </div>

        {/* 2. Banner de Promoción/Destacado (Similar al "Get free one large popcorn...") */}
        <div className="bg-[#BAE6FD] p-6 rounded-xl flex justify-between items-center mb-10 shadow-md">
          <div>
            <h3 className="text-xl font-bold text-[#316B7A] mb-2">
              ¡Tu Match Perfecto te Espera!
            </h3>
            <p className="text-gray-700 max-w-lg">
              Te presentamos las mascotas más compatibles contigo según tus preferencias de búsqueda.
            </p>
            <Link
                to="/dashboard/mascotas"
                className="inline-block mt-4 px-5 py-2 bg-[#316B7A] text-white rounded-lg hover:bg-teal-800 transition-colors font-semibold text-sm"
            >
                Ver Recomendaciones
            </Link>
          </div>
          <FontAwesomeIcon icon={faPaw} className="text-5xl text-[#316B7A]/50 rotate-12" />
        </div>

        {/* 3. Secciones Destacadas (Popular this week) */}
        <h3 className="text-2xl font-bold text-gray-800 mb-6">
          Nuevas Mascotas Cerca de Ti
        </h3>
        
        {/* Tarjetas de Mascotas (Ejemplo) */}
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-6 mb-12">
            
            {/* Tarjeta 1 */}
            <div className="bg-[#FFF7E6] p-4 rounded-xl shadow-md hover:shadow-lg transition-shadow border border-[#316B7A]/20">
                <img src="/src/assets/images/abrazo.png" alt="Mascota 1" className="w-full h-32 object-cover rounded-lg mb-3" />
                <h4 className="font-semibold text-[#316B7A]">Max (Perro)</h4>
                <p className="text-xs text-gray-600 mb-2">Refugio San Roque</p>
                <div className="flex justify-between items-center">
                    <span className="text-sm font-bold text-[#FDB2A0]">3 Años</span>
                    <Heart className="w-5 h-5 text-gray-400 hover:text-[#FDB2A0] transition-colors cursor-pointer" />
                </div>
            </div>

            {/* Tarjeta 2 */}
            <div className="bg-[#FFF7E6] p-4 rounded-xl shadow-md hover:shadow-lg transition-shadow border border-[#316B7A]/20">
                <img src="/src/assets/images/cat1.webp" alt="Mascota 2" className="w-full h-32 object-cover rounded-lg mb-3" />
                <h4 className="font-semibold text-[#316B7A]">Luna (Gato)</h4>
                <p className="text-xs text-gray-600 mb-2">Asociación Felina</p>
                <div className="flex justify-between items-center">
                    <span className="text-sm font-bold text-[#FDB2A0]">1 Año</span>
                    <Heart className="w-5 h-5 text-gray-400 hover:text-[#FDB2A0] transition-colors cursor-pointer" />
                </div>
            </div>

            {/* Tarjeta 3 */}
            <div className="bg-[#FFF7E6] p-4 rounded-xl shadow-md hover:shadow-lg transition-shadow border border-[#316B7A]/20">
                <img src="/src/assets/images/perro_dash.jpg" alt="Mascota 3" className="w-full h-32 object-cover rounded-lg mb-3" />
                <h4 className="font-semibold text-[#316B7A]">Rocky (Perro)</h4>
                <p className="text-xs text-gray-600 mb-2">Rescate Patitas</p>
                <div className="flex justify-between items-center">
                    <span className="text-sm font-bold text-[#FDB2A0]">5 Meses</span>
                    <Heart className="w-5 h-5 text-red-500 transition-colors cursor-pointer fill-red-500" />
                </div>
            </div>
             
            {/* Tarjeta 4: Botón para ver más */}
            <Link to="/dashboard/mascotas" className="flex flex-col items-center justify-center p-4 rounded-xl shadow-md bg-[#BAE6FD]/50 hover:bg-[#BAE6FD] transition-colors border-2 border-dashed border-[#316B7A]/50 group">
                <Users className="w-8 h-8 text-[#316B7A] mb-2 group-hover:scale-110 transition-transform" />
                <span className="font-semibold text-[#316B7A] text-center">Ver todas las Mascotas</span>
            </Link>
        </div>

        {/* 4. Restaurantes Favoritos -> Refugios Favoritos/Destacados */}
        <h3 className="text-2xl font-bold text-gray-800 mb-6 mt-8">
          Refugios Destacados
        </h3>

        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-6">
            
            {/* Refugio 1 */}
            <div className="bg-[#FFF7E6] p-4 rounded-xl shadow-md border border-[#FDB2A0]/30 hover:shadow-lg transition-shadow cursor-pointer">
                <h4 className="font-semibold text-[#316B7A]">Refugio Esperanza</h4>
                <p className="text-xs text-gray-600 mb-2 flex items-center"><MapPin className="w-3 h-3 mr-1"/> Lima, Perú</p>
                <div className="flex items-center text-yellow-500 text-sm">
                    {/* Estrellas */}
                    ★ ★ ★ ★ ☆
                </div>
                <Link to="/dashboard/refugios/1" className="text-xs text-[#FDB2A0] hover:underline mt-1 block">Ver perfil</Link>
            </div>
            
            {/* Refugio 2 */}
            <div className="bg-[#FFF7E6] p-4 rounded-xl shadow-md border border-[#FDB2A0]/30 hover:shadow-lg transition-shadow cursor-pointer">
                <h4 className="font-semibold text-[#316B7A]">Patitas Felices</h4>
                <p className="text-xs text-gray-600 mb-2 flex items-center"><MapPin className="w-3 h-3 mr-1"/> Arequipa, Perú</p>
                <div className="flex items-center text-yellow-500 text-sm">
                    ★ ★ ★ ★ ★
                </div>
                <Link to="/dashboard/refugios/2" className="text-xs text-[#FDB2A0] hover:underline mt-1 block">Ver perfil</Link>
            </div>
            
             {/* Refugio 3 */}
            <div className="bg-[#FFF7E6] p-4 rounded-xl shadow-md border border-[#FDB2A0]/30 hover:shadow-lg transition-shadow cursor-pointer">
                <h4 className="font-semibold text-[#316B7A]">Casa Rescate</h4>
                <p className="text-xs text-gray-600 mb-2 flex items-center"><MapPin className="w-3 h-3 mr-1"/> Cusco, Perú</p>
                <div className="flex items-center text-yellow-500 text-sm">
                    ★ ★ ★ ☆ ☆
                </div>
                <Link to="/dashboard/refugios/3" className="text-xs text-[#FDB2A0] hover:underline mt-1 block">Ver perfil</Link>
            </div>

            {/* Ver Todos los Refugios */}
            <Link to="/dashboard/refugios" className="flex flex-col items-center justify-center p-4 rounded-xl shadow-md bg-[#FDB2A0]/50 hover:bg-[#FDB2A0] transition-colors border-2 border-dashed border-[#FDB2A0]/80 group">
                <Users className="w-8 h-8 text-[#316B7A] mb-2 group-hover:scale-110 transition-transform" />
                <span className="font-semibold text-[#316B7A] text-center">Ver todos los Refugios</span>
            </Link>
        </div>
      </div>
    </div>
  );
}