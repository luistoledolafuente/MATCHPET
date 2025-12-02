import React, { useEffect, useState } from "react";
import { useAuth } from "../../contexts/AuthContext";
import animalService, { getFavorites } from "../../services/animalService";
import { getMisSolicitudes } from "../../services/solicitudService";

import { Loader2, MapPin, PawPrint, UserCheck, Cpu, Heart } from "lucide-react";
import { useNavigate } from "react-router-dom";
import homeFamiliaImg from "../../assets/images/home_Familia.png";


const BACKEND_BASE_URL = "http://127.0.0.1:8081";

const PetCard = ({ animal, onClick }) => {
  const imageUrl = animal.fotos?.[0]
    ? `${BACKEND_BASE_URL}${animal.fotos[0]}`
    : "https://placehold.co/400x300/AEEAFD/2B6777?text=Mascota"; 
  const secondaryText = animal.refugio || animal.estadoAdopcion || "Ver Detalles";

  return (
    <div
      className="w-full bg-white rounded-xl shadow-lg overflow-hidden cursor-pointer hover:shadow-xl transition-shadow border-2 border-[#AEEAFD]"
      onClick={onClick}
    >
      <div className="w-full aspect-[4/3] bg-gray-100 flex items-center justify-center">
        <img
          src={imageUrl}
          alt={animal.nombre}
          className="w-full h-full object-cover rounded-t-xl"
        />
      </div>
      <div className="p-4">
        <h3 className="text-xl font-bold text-[#2B6777] leading-tight">{animal.nombre}</h3>
        <p className="text-sm text-[#5E5E5E] mt-1 leading-snug">
          {animal.raza || "Raza desconocida"} • {animal.genero || "Sin género"}
        </p>
        <div className="text-xs text-[#2B6777] font-medium mt-1">
          {secondaryText}
        </div>
      </div>
    </div>
  );
};

const ApplicationCard = ({ solicitud, onClick }) => {
  const animal = solicitud.animal || {}; 
  const estado = solicitud.estadoSolicitud?.nombre || "Desconocido";

  const imageUrl = animal.fotos?.[0]
    ? `${BACKEND_BASE_URL}${animal.fotos[0]}`
    : "https://placehold.co/400x300/FFF8F0/5E5E5E?text=Solicitud"; 
  const getStateStyle = (estado) => {
    switch (estado) {
      case "Aprobada": return "bg-green-100 text-green-700";
      case "Rechazada": return "bg-red-100 text-red-700";
      case "En Proceso": return "bg-blue-100 text-blue-700";
      default: return "bg-yellow-100 text-yellow-700";
    }
  };

  return (
    <div
      className="w-full bg-white rounded-xl shadow-lg overflow-hidden cursor-pointer hover:shadow-xl transition-shadow border-2 border-[#FFB6A3]"
      onClick={onClick}
    >
      {/* Área de la imagen */}
      <div className="w-full aspect-[4/3] bg-gray-100 flex items-center justify-center">
        <img
          src={imageUrl}
          alt={animal.nombre}
          className="w-full h-full object-cover rounded-t-xl"
        />
      </div>
      <div className="p-4">
        <h3 className="text-xl font-bold text-[#2B6777] leading-tight">{animal.nombre || "Mascota"}</h3>
        <p className="text-sm text-[#5E5E5E] mt-1 leading-snug">{animal.raza || "Mestizo"}</p>
        <span className={`text-xs font-semibold px-2 py-1 rounded-full mt-2 inline-block ${getStateStyle(estado)}`}>
          {estado}
        </span>
      </div>
    </div>
  );
};



export default function DashboardHome() {
  const { user, token, isAuthenticated, authLoading } = useAuth();
  const navigate = useNavigate();

  const [mascotas, setMascotas] = useState([]); 
  const [solicitudes, setSolicitudes] = useState([]); 
  const [favoritos, setFavoritos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      const mascotasData = await animalService.getAnimalesPaginados(0, 6);
      setMascotas(mascotasData.content);

      if (isAuthenticated && token) {
        const favs = await getFavorites(token);
        setFavoritos(favs);

        const solicitudesData = await getMisSolicitudes(token);
        setSolicitudes(solicitudesData.slice(0, 6));
      }
    } catch (err) {
      console.error("Error cargando dashboard:", err);
      setError("No se pudo cargar toda la información del dashboard.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!authLoading) fetchData();
  }, [authLoading, isAuthenticated, token]);

  const adoptadas = mascotas.filter(m => m.estadoAdopcion === "Adoptada").length;
  const enProceso = mascotas.filter(m => m.estadoAdopcion === "En Proceso").length;


  return (
    <div className="w-full min-h-screen bg-gradient-to-b from-[#AEEAFD] to-[#FFF8F0] flex flex-col items-center pb-20 relative">
      {user?.direccion && (
        <div className="absolute top-4 right-6 flex items-center text-[#5E5E5E] text-sm font-medium">
          <MapPin className="w-5 h-5 mr-1 text-[#2B6777]" />
          {user.direccion}
        </div>
      )}
      <br />
      <br />

      <h1 className="text-4xl md:text-5xl font-bold text-[#2B6777] text-center mt-10 flex flex-wrap items-center justify-center gap-2 px-4">
        Bienvenido a <span className="text-[#2B6777]">MatchPet,</span>{" "}
        {user && (
          <>
            <span className="text-[#5E5E5E]">{`${user.nombre} ${user.apellidoPaterno || ""}`.trim()}</span>
            <PawPrint className="w-10 h-10 text-[#2B6777] animate-bounce" />
          </>
        )}
      </h1>
      {loading && (
        <div className="text-center p-8 text-[#2B6777]">
          <Loader2 className="w-8 h-8 animate-spin mx-auto mb-3" />
          Cargando información...
        </div>
      )}
      {error && (
        <div className="text-center p-4 mt-8 text-red-700 bg-red-100 border border-red-400 rounded-lg">
          {error}
        </div>
      )}
      {!loading && !error && (
        <section
          onClick={() => navigate("/recomendaciones")}
          className="mt-12 w-full max-w-7xl px-4 cursor-pointer select-none"
        >
          <div className="bg-[#2B6777] rounded-xl shadow-lg p-8 flex flex-col md:flex-row items-center gap-8 text-white hover:bg-[#245a63] transition">
            <Cpu className="w-20 h-20 text-[#AEEAFD]" />
            <div className="flex-1">
              <h2 className="text-3xl font-bold mb-4">
                Encuentra el Match Perfecto para tu Familia
              </h2>
              <p className="mb-6 text-base md:text-lg max-w-xl text-[#AEEAFD]">
                Conectamos mascotas con familias amorosas para crear lazos que duran para siempre.
              </p>
              <button
                type="button"
                className="bg-[#AEEAFD] text-[#2B6777] px-6 py-3 rounded-full font-semibold hover:bg-[#93d3fa] transition"
                onClick={(e) => {
                  e.stopPropagation();
                  navigate("/dashboard/adoptante/recomendaciones");
                }}
              >
                Comenzar mi Búsqueda
              </button>
            </div>
            <div className="flex-1 hidden md:block">
              <img
                src={homeFamiliaImg}
                alt="Mascotas felices"
                className="rounded-lg shadow-lg w-full max-w-lg object-cover"
              />
            </div>
          </div>
        </section>
      )}
      {!loading && !error && (
        <div className="w-full max-w-7xl px-4 mt-12">
          <h2 className="text-3xl font-bold text-[#2B6777] mb-8 text-center">Tus Estadísticas</h2>
          <div className="flex flex-wrap justify-center gap-10"> {/* más espacio entre cards */}

            {/* Mascotas recientes */}
            <div className="bg-white rounded-xl w-64 h-40 flex flex-col items-center justify-center shadow-md hover:shadow-xl transition-shadow duration-300 border-2 border-[#FFB6A3] p-4">
              <PawPrint className="w-12 h-12 mb-2 text-[#FFB6A3]" />
              <p className="text-4xl text-[#2B6777]">{mascotas.length}</p>
              <p className="mt-1 text-center text-sm text-[#5E5E5E]">Mascotas recientes</p>
            </div>

            {/* Tus favoritos */}
            <div className="bg-white rounded-xl w-64 h-40 flex flex-col items-center justify-center shadow-md hover:shadow-xl transition-shadow duration-300 border-2 border-[#2B6777] p-4">
              <Heart className="w-12 h-12 mb-2 text-[#2B6777]" />
              <p className="text-4xl text-[#2B6777]">{favoritos.length}</p>
              <p className="mt-1 text-center text-sm text-[#5E5E5E]">Tus mascotas favoritas</p>
            </div>

            {/* Mascotas adoptadas */}
            <div className="bg-white rounded-xl w-64 h-40 flex flex-col items-center justify-center shadow-md hover:shadow-xl transition-shadow duration-300 border-2 border-[#AEEAFD] p-4">
              <UserCheck className="w-12 h-12 mb-2 text-[#AEEAFD]" />
              <p className="text-4xl text-[#2B6777]">{adoptadas}</p>
              <p className="mt-1 text-center text-sm text-[#5E5E5E]">Mascotas adoptadas</p>
            </div>

            {/* En proceso */}
            <div className="bg-white rounded-xl w-64 h-40 flex flex-col items-center justify-center shadow-md hover:shadow-xl transition-shadow duration-300 border-2 border-[#5E5E5E] p-4">
              <Cpu className="w-12 h-12 mb-2 text-[#5E5E5E]" />
              <p className="text-4xl text-[#2B6777]">{enProceso}</p>
              <p className="mt-1 text-center text-sm text-[#5E5E5E]">En proceso de adopción</p>
            </div>

          </div>
        </div>
      )}
      <br />
      <br />
      {!loading && !error && mascotas.length > 0 && (
        <div className="mt-10 w-full max-w-7xl px-4">
          <h2 className="text-2xl font-bold text-[#2B6777] mb-4">Mascotas que recien fueron añadidas</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-3 xl:grid-cols-3 gap-4">
            {mascotas.map((animal) => (
              <PetCard
                key={animal.animal_id}
                animal={animal}
                onClick={() => navigate(`/perfil-animal/${animal.animal_id}`)}
              />
            ))}
          </div>
        </div>
      )}
      {!loading && !error && solicitudes.length > 0 && (
        <div className="mt-10 w-full max-w-7xl px-4">
          <h2 className="text-2xl font-bold text-[#2B6777] mb-4">Tus Solicitudes Recientes</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-3 xl:grid-cols-3 gap-4">
            {solicitudes.map((solicitud) => (
              <ApplicationCard
                key={solicitud.id}
                solicitud={solicitud}
                onClick={() => navigate(`/dashboard/solicitudes/${solicitud.id}`)}
              />
            ))}
          </div>
        </div>
      )}
      <br />
      <br />
      {!loading && !error && mascotas.length === 0 && (
        <div className="text-center p-12 mt-10 bg-white rounded-xl shadow-lg w-full max-w-xl border-t-4 border-[#FFB6A3]">
          <p className="text-xl font-semibold text-[#2B6777]">¡Parece que no hay mascotas nuevas cerca!</p>
          <p className="text-[#5E5E5E] mt-2">Usa el botón "Comenzar mi Búsqueda" para encontrar tu match perfecto con nuestra IA.</p>
        </div>
      )}

    </div>
  );
}