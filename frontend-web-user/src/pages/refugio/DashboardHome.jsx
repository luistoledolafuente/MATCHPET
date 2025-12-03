import React, { useState, useEffect } from "react";
import { PawPrint, BarChart3, UserCheck, Clock } from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";
import { getMisAnimales } from "../../services/animalService";
import { Bar, Doughnut } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  Tooltip,
  Legend
} from "chart.js";

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend);

const BACKEND_BASE_URL = "http://localhost:8081";

const StatCard = ({ title, value, Icon, colorClass, gradientClass }) => (
  <div className={`p-4 rounded-xl shadow-md border border-white/30 backdrop-blur-sm ${gradientClass} transition-all hover:shadow-lg flex flex-col justify-between`}>
    <div className="flex items-center gap-2">
      <Icon className={`w-7 h-7 ${colorClass} drop-shadow-md`} />
      <p className={`text-sm font-semibold text-[#316B7A] text-opacity-90`}>{title}</p>
    </div>
    <div className="mt-2 flex items-baseline gap-1">
      <span className={`text-xl font-extrabold ${colorClass} drop-shadow-md`}>{value}</span>
    </div>
  </div>
);

const LastAnimalCard = ({ animal }) => (
  <div className="bg-white/90 p-3 rounded-2xl shadow-md border border-gray-100 transition-all hover:shadow-lg cursor-pointer">
    <img
      src={animal.fotos?.[0]?.startsWith('/') ? `${BACKEND_BASE_URL}${animal.fotos[0]}` : animal.fotos?.[0] || "https://placehold.co/200x150?text=Pet"}
      alt={animal.nombre}
      className="w-full h-28 md:h-36 object-cover rounded-xl mb-3 shadow-inner"
    />
    <h3 className="text-base font-bold text-[#007C91] truncate">{animal.nombre}</h3>
    <p className="text-xs text-gray-500">{animal.raza || "Desconocida"}</p>
    <p className="text-xs text-gray-400 mt-1">{animal.estadoAdopcion || "Sin estado"}</p>
  </div>
);

export default function DashboardHome() {
  const { user, token, isAuthenticated } = useAuth();
  const [animals, setAnimals] = useState([]);
  const [loadingAnimals, setLoadingAnimals] = useState(true);

  useEffect(() => {
    const fetchAnimals = async () => {
      if (!token || !isAuthenticated) return;
      try {
        const data = await getMisAnimales(token);
        setAnimals(data);
      } catch (err) {
        console.error("Error cargando mascotas:", err);
      } finally {
        setLoadingAnimals(false);
      }
    };
    fetchAnimals();
  }, [token, isAuthenticated]);

  // Estadísticas calculadas dinámicamente, con filtro insensible a mayúsculas y espacios
  const totalAnimals = animals.length;
  const males = animals.filter(a => a.genero?.trim().toLowerCase() === "macho").length;
  const females = animals.filter(a => a.genero?.trim().toLowerCase() === "hembra").length;
  const estadoDisponible = animals.filter(a =>
    a.estadoAdopcion?.trim().toLowerCase() === "disponible"
  ).length;
  const estadoProceso = animals.filter(a =>
    a.estadoAdopcion?.trim().toLowerCase() === "en proceso"
  ).length;
  const estadoAdoptada = animals.filter(a =>
    a.estadoAdopcion?.trim().toLowerCase() === "adoptada"
  ).length;

  // Datos para gráficos
  const barData = {
    labels: ["Disponible", "En Proceso", "Adoptada"],
    datasets: [
      {
        label: "Cantidad de Mascotas",
        data: [estadoDisponible, estadoProceso, estadoAdoptada],
        backgroundColor: ["#00A8CC", "#FFB400", "#28B463"]
      }
    ]
  };

  const doughnutData = {
    labels: ["Machos", "Hembras"],
    datasets: [
      {
        data: [males, females],
        backgroundColor: ["#007C91", "#FDB2A0"]
      }
    ]
  };

  // Últimas mascotas (más recientes)
  const lastAnimals = animals.slice(-6).reverse();

  const PRIMARY_COLOR = "text-[#007C91]";
  const SECONDARY_COLOR = "text-[#407581]";
  const ACCENT_COLOR = "text-[#FDB2A0]";

  const fullName = `${user?.nombre || ""} ${user?.apellidoPaterno || ""}`.trim();

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#dff3ff] to-[#e8e8e8] p-8">
      <div className="max-w-[1700px] mx-auto space-y-8">

        {/* Header */}
        <header className="bg-gradient-to-r from-[#dff3ff]/80 to-[#a8d8e0]/80 border border-white/70 shadow-xl rounded-3xl p-8 flex flex-col md:flex-row items-center justify-between relative overflow-hidden min-h-[180px]">
          <div className="z-10 flex-grow">
            <h1 className="text-4xl font-extrabold text-[#007C91]">¡Bienvenido, {fullName}!</h1>
            <p className="text-[#407581] text-lg mt-2 font-medium">Dashboard de Gestión del Refugio</p>
          </div>
          <div className="absolute -top-10 -right-10 w-56 h-56 bg-[#B2EBF2] rounded-full opacity-30 blur-3xl"></div>
          <div className="absolute -bottom-10 -left-10 w-72 h-72 bg-[#007C91] rounded-full opacity-10 blur-3xl"></div>
        </header>

        {/* Estadísticas */}
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
          <StatCard title="Total Mascotas" value={totalAnimals} Icon={PawPrint} colorClass={PRIMARY_COLOR} gradientClass="bg-gradient-to-br from-[#FFF7E6] to-[#FFE0B2]" />
          <StatCard title="Machos" value={males} Icon={UserCheck} colorClass={PRIMARY_COLOR} gradientClass="bg-gradient-to-br from-[#D6F0E0] to-[#407581]/30" />
          <StatCard title="Hembras" value={females} Icon={UserCheck} colorClass={ACCENT_COLOR} gradientClass="bg-gradient-to-br from-[#FFD6D6] to-[#FDB2A0]" />
          <StatCard title="Disponibles" value={estadoDisponible} Icon={Clock} colorClass={PRIMARY_COLOR} gradientClass="bg-gradient-to-br from-[#DFF3FF] to-[#A8D8E0]" />
          <StatCard title="En Proceso" value={estadoProceso} Icon={Clock} colorClass={ACCENT_COLOR} gradientClass="bg-gradient-to-br from-[#FFF0B2] to-[#FFB400]" />
          <StatCard title="Adoptadas" value={estadoAdoptada} Icon={Clock} colorClass={PRIMARY_COLOR} gradientClass="bg-gradient-to-br from-[#D6F0E0] to-[#28B463]/50" />
        </div>

        {/* Últimas mascotas */}
        <div className="bg-white/90 p-6 rounded-3xl shadow-lg border border-white/50">
          <h2 className="text-2xl font-bold text-[#007C91] mb-4">Últimas Mascotas Añadidas</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {lastAnimals.length > 0 ? lastAnimals.map(a => <LastAnimalCard key={a.animal_id} animal={a} />) : <p className="text-gray-500 col-span-full">No hay mascotas registradas.</p>}
          </div>
        </div>

        {/* Gráficos */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div className="bg-white p-6 rounded-3xl shadow-lg border border-gray-200 flex flex-col items-center justify-center" style={{ height: 400 }}>
            <h3 className="text-lg font-semibold text-[#007C91] mb-6 self-start">Mascotas por Estado</h3>
            <Bar
              data={barData}
              options={{
                maintainAspectRatio: false,
                plugins: {
                  legend: {
                    display: true,
                    position: "top",
                    labels: { font: { size: 14 }, color: '#316B7A' }
                  },
                  tooltip: { enabled: true }
                },
                scales: {
                  y: { beginAtZero: true, ticks: { stepSize: 1 } },
                  x: { ticks: { color: '#316B7A', font: { size: 14 } } }
                }
              }}
              style={{ width: "100%", height: "calc(100% - 40px)" }}
            />
          </div>

          <div className="bg-white p-6 rounded-3xl shadow-lg border border-gray-200 flex flex-col items-center justify-center" style={{ height: 400 }}>
            <h3 className="text-lg font-semibold text-[#007C91] mb-6 self-start">Distribución por Género</h3>
            <Doughnut
              data={doughnutData}
              options={{
                maintainAspectRatio: false,
                plugins: {
                  legend: {
                    display: true,
                    position: "top",
                    labels: {
                      font: { size: 14 },
                      color: ['#007C91', '#FDB2A0'],
                      generateLabels: (chart) => {
                        const data = chart.data;
                        if (data.labels.length && data.datasets.length) {
                          return data.labels.map((label, i) => ({
                            text: label,
                            fillStyle: data.datasets[0].backgroundColor[i],
                            strokeStyle: "#000",
                            index: i,
                          }));
                        }
                        return [];
                      }
                    }
                  },
                  tooltip: { enabled: true }
                }
              }}
              style={{ width: "100%", height: "calc(100% - 40px)" }}
            />
          </div>
        </div>

      </div>
    </div>
  );
}
