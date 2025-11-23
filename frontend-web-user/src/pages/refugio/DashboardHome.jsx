import React, { useState, useEffect } from "react";
import { PawPrint, BarChart3, Clock, CheckCircle, Gift, List, UserCheck } from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";
import { getMisAnimales, getBitacora } from "../../services/animalService";

const StatCard = ({ title, value, Icon, unit, colorClass, gradientClass }) => (
  <div className={`p-3 rounded-xl shadow-md border border-white/30 backdrop-blur-sm ${gradientClass} transition-all hover:shadow-lg h-28 flex flex-col justify-between`}>
    <div className="flex items-center gap-2">
      <Icon className={`w-7 h-7 ${colorClass} drop-shadow-md`} />
      <p className={`text-sm font-semibold text-[#316B7A] text-opacity-90`}>{title}</p>
    </div>
    <div className="mt-1 flex items-baseline gap-1">
      <span className={`text-xl font-extrabold ${colorClass} drop-shadow-md`}>{value}</span>
      <span className="text-sm font-semibold text-[#407581]">{unit}</span>
    </div>
  </div>
);

const LastAnimalCard = ({ animal }) => (
  <div className="bg-white/90 p-3 rounded-2xl shadow-md border border-gray-100 transition-all hover:shadow-lg cursor-pointer">
    <img
      src={animal.fotos?.[0] || "https://placehold.co/200x150?text=Pet"}
      alt={animal.nombre}
      className="w-full h-24 object-cover rounded-xl mb-3 shadow-inner"
    />
    <h3 className="text-base font-bold text-[#007C91] truncate">{animal.nombre}</h3>
    <p className="text-xs text-gray-500">{animal.raza || "Desconocida"}</p>
  </div>
);

export default function DashboardHome() {
  const { user, token, isAuthenticated } = useAuth();
  const [animalCount, setAnimalCount] = useState(0);
  const [loadingAnimals, setLoadingAnimals] = useState(true);
  const [errorAnimals, setErrorAnimals] = useState(null);
  const [lastAnimals, setLastAnimals] = useState([]);
  const [bitacora, setBitacora] = useState([]);

  useEffect(() => {
    const fetchAnimals = async () => {
      if (!token || !isAuthenticated) return;
      try {
        const data = await getMisAnimales(token);
        setAnimalCount(data.length);
        setLastAnimals(data.slice(-4).reverse());
      } catch (err) {
        console.error("Error cargando mascotas:", err);
        setErrorAnimals("No se pudo cargar el número de mascotas.");
      } finally {
        setLoadingAnimals(false);
      }
    };

    const fetchBitacora = async () => {
      if (!token || !isAuthenticated) return;
      try {
        const data = await getBitacora(token);
        // Ordenar por fecha descendente y tomar últimos 5
        setBitacora(data.sort((a, b) => new Date(b.fecha) - new Date(a.fecha)).slice(0, 5));
      } catch (err) {
        console.error("Error cargando bitácora:", err);
      }
    };

    fetchAnimals();
    fetchBitacora();
  }, [token, isAuthenticated]);

  const fullName = `${user?.nombre || ""} ${user?.apellidoPaterno || ""} ${user?.apellidoMaterno || ""}`.trim();

  const PRIMARY_COLOR = "text-[#007C91]";
  const SECONDARY_COLOR = "text-[#407581]";
  const ACCENT_COLOR = "text-[#FDB2A0]";

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#dff3ff] to-[#e8e8e8] p-10">
      <div className="max-w-[1700px] min-h-[80vh] mx-auto bg-white/90 backdrop-blur-2xl shadow-[0_10px_60px_rgba(0,0,0,0.15)] border border-white rounded-3xl overflow-hidden p-8 space-y-8">
        <header className="bg-gradient-to-r from-[#dff3ff]/80 to-[#a8d8e0]/80 border border-white/70 shadow-xl rounded-3xl p-8 flex flex-col md:flex-row items-center justify-between relative overflow-hidden h-auto min-h-[200px]">
          <div className="z-10 flex-grow">
            <h1 className="text-4xl font-extrabold text-[#007C91]">
              ¡Bienvenido, {fullName}!
            </h1>
            <p className="text-[#407581] text-lg mt-2 font-medium">
              Dashboard de Gestión del Refugio
            </p>
          </div>
          <div className="absolute -top-10 -right-10 w-56 h-56 bg-[#B2EBF2] rounded-full opacity-30 blur-3xl"></div>
          <div className="absolute -bottom-10 -left-10 w-72 h-72 bg-[#007C91] rounded-full opacity-10 blur-3xl"></div>
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-4 xl:grid-cols-5 gap-8">

          {/* Estadísticas y Últimas Mascotas */}
          <section className="lg:col-span-3 xl:col-span-4 space-y-8">
            <h2 className="text-2xl font-bold text-[#007C91] flex items-center gap-2">
              <BarChart3 className={`w-6 h-6 ${ACCENT_COLOR}`} />
              Resumen de Actividad
            </h2>

            <div className="grid grid-cols-2 gap-6">
              <StatCard
                title="Mascotas Disponibles"
                value={loadingAnimals ? "..." : animalCount}
                unit="animales"
                Icon={PawPrint}
                colorClass={PRIMARY_COLOR}
                gradientClass="bg-gradient-to-br from-[#FFF7E6] to-[#FFE0B2]"
              />
              <StatCard
                title="Solicitudes Pendientes"
                value={0}
                unit="pendientes"
                Icon={Clock}
                colorClass={SECONDARY_COLOR}
                gradientClass="bg-gradient-to-br from-[#D6F0E0] to-[#407581]/30"
              />
              <StatCard
                title="Adopciones (Mes)"
                value={0}
                unit="éxitos"
                Icon={CheckCircle}
                colorClass={PRIMARY_COLOR}
                gradientClass="bg-gradient-to-br from-[#DFF3FF] to-[#A8D8E0]"
              />
              <StatCard
                title="Donaciones (Semana)"
                value={0}
                unit="$"
                Icon={Gift}
                colorClass={SECONDARY_COLOR}
                gradientClass="bg-gradient-to-br from-[#FFE0E0] to-[#FDB2A0]"
              />
            </div>

            <div className="bg-white/80 p-6 rounded-3xl shadow-lg border border-white/50">
              <h2 className="text-xl font-bold text-[#007C91] mb-4">Últimas Mascotas Agregadas</h2>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {lastAnimals.length > 0 ? (
                  lastAnimals.map((animal) => (
                    <LastAnimalCard key={animal.animal_id} animal={animal} />
                  ))
                ) : (
                  <p className="text-gray-500 col-span-full">Aún no hay mascotas registradas.</p>
                )}
              </div>
            </div>
          </section>

          {/* Bitácora */}
          <section className="lg:col-span-1 xl:col-span-1 space-y-6">
            <div className="bg-white/90 p-6 rounded-3xl shadow-lg border border-white/70 h-full">
              <h3 className="text-xl font-bold text-[#007C91] mb-4 flex items-center gap-2">
                <List className={`w-5 h-5 ${ACCENT_COLOR}`} />
                Bitácora de Actividad Reciente
              </h3>
              <ul className="space-y-4 text-sm">
                {bitacora.length > 0 ? bitacora.map((activity, index) => (
                  <li key={index} className="flex items-start gap-3 border-b border-gray-100 pb-3 last:border-b-0 last:pb-0">
                    <PawPrint className={`w-4 h-4 mt-1 ${ACCENT_COLOR}`} />
                    <div>
                      <p className="font-semibold text-[#407581]">{activity.descripcion}</p>
                      <p className="text-xs text-gray-500">{new Date(activity.fecha).toLocaleString()}</p>
                    </div>
                  </li>
                )) : (
                  <p className="text-gray-500">No hay actividad reciente.</p>
                )}
              </ul>
              <button className="mt-6 w-full bg-[#007C91] text-white py-2 rounded-xl font-semibold hover:bg-[#007C9180] transition">
                Ver Historial Completo
              </button>
            </div>
          </section>

        </div>
      </div>
    </div>
  );
}
