import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import animalService from '../../services/animalService';
import { Loader2, XCircle, Zap, Heart, Info } from 'lucide-react';
import animalWalking from "../../assets/gif/dog_gif.gif";

const BACKEND_BASE_URL = "http://127.0.0.1:8081"; // Ajusta esta URL si es necesario

const RecomendacionesPage = () => {
    const { token, isAuthenticated } = useAuth();
    const [recomendaciones, setRecomendaciones] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchRecomendaciones = async () => {
            setLoading(true);
            setError(null);

            if (!token || !isAuthenticated) {
                // Esto debería ser manejado por ProtectedRoute, pero es un buen fallback
                setError("Debes iniciar sesión como Adoptante para ver tus recomendaciones.");
                setLoading(false);
                return;
            }

            try {
                // Llamada al endpoint: GET /api/animales/recomendados
                const data = await animalService.getRecomendaciones(token);
                setRecomendaciones(data);
            } catch (err) {
                console.error("Error al cargar recomendaciones:", err);
                const errMsg = err.response?.data?.message || err.message || "Error al obtener las recomendaciones. Asegúrate de tener tu perfil de Adoptante completo.";
                setError(errMsg);
            } finally {
                setLoading(false);
            }
        };

        fetchRecomendaciones();
    }, [token, isAuthenticated]);

    const handleSolicitar = (animalId, nombre) => {
        alert(`Preparando solicitud para ${nombre} (ID: ${animalId}).`);
    };



    if (loading) {
    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-[#AEEAFD] p-6">
            {/* Contenedor del GIF + barra */}
            <div className="relative w-72 h-16">

                {/* GIF sobre la barra */}
                <img
                    src={animalWalking}
                    alt="animal walking"
                    className="w-80 h-auto absolute"
                    style={{ bottom: "0", left: "-40px", animation: "walk 1.8s linear infinite" }}
                />

                {/* Barra de carga */}
                <div className="absolute bottom-0 w-full h-4 bg-gray-200 rounded-full overflow-hidden">
                    <div className="absolute left-0 top-0 h-full bg-[#2B6777] animate-[loadBar_1.8s_linear_infinite]"></div>
                </div>
            </div>

            {/* Texto */}
            <p className="mt-6 text-[#2B6777] text-lg font-semibold animate-pulse">
                Buscando tu match perfecto...
            </p>

            <style>{`
                @keyframes walk {
                    0% { transform: translateX(0); }
                    100% { transform: translateX(120px); }
                }
                @keyframes loadBar {
                    0% { width: 0%; }
                    100% { width: 100%; }
                }
            `}</style>
        </div>
    );
}



    
    if (error) {
        return (
            <div className="text-center py-10 bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded-lg flex items-center justify-center">
                <XCircle className="w-5 h-5 mr-3" />
                {error}
            </div>
        );
    }

    return (
  <div className="w-full min-h-screen bg-gradient-to-b from-[#AEEAFD] to-[#FFF8F0] p-6 flex flex-col items-center font-sans">

    <div className="max-w-7xl w-full">

      {/* Título principal */}
      <h1 className="text-4xl font-extrabold mb-6 text-[#2B6777] flex items-center">
        <Zap className="w-8 h-8 mr-3 text-yellow-500" />
        Tu Match Perfecto
      </h1>

      {/* Información inicial */}
      <div className="bg-white shadow-md p-4 mb-8 rounded-3xl border-l-4 border-[#2B6777] flex items-start">
        <Info className="w-5 h-5 mt-1 mr-3 text-[#2B6777] flex-shrink-0" />
        <p className="text-sm text-[#5E5E5E]">
          Estas mascotas han sido seleccionadas por nuestra Inteligencia Artificial según tu perfil de Adoptante, estilo de vida y preferencias. ¡Tu compañero ideal podría estar aquí!
        </p>
      </div>

      {/* Si no hay recomendaciones */}
      {recomendaciones.length === 0 ? (
        <div className="text-center py-20 px-6 bg-white/70 backdrop-blur rounded-3xl shadow-xl border border-gray-200">
          <Heart className="w-12 h-12 mx-auto text-red-400 mb-4" />
          <p className="font-bold text-2xl text-[#2B6777] mb-3">No encontramos coincidencias perfectas</p>
          <p className="text-gray-600 max-w-md mx-auto">
            Asegúrate de que tu perfil esté actualizado para mejorar los resultados.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
          {recomendaciones.map((animal) => (
            <div key={animal.animal_id} className="bg-white rounded-3xl shadow-xl hover:shadow-2xl border border-gray-200 transition duration-300 overflow-hidden">

              <img
                src={animal.fotos?.[0] ? `${BACKEND_BASE_URL}${animal.fotos[0]}` : "https://placehold.co/400x300/a8d8e0/316B7A?text=Match+IA"}
                alt={`Foto de ${animal.nombre}`}
                className="w-full h-56 object-cover"
              />

              <div className="p-6">
                <h2 className="text-2xl font-bold text-[#2B6777] mb-1">{animal.nombre}</h2>
                <p className="text-sm text-[#5E5E5E] mb-3">{animal.especie} - {animal.raza}</p>

                <div className="space-y-1 text-sm text-[#5E5E5E]">
                  <p><span className="font-semibold">Energía:</span> {animal.nivelEnergia}</p>
                  <p><span className="font-semibold">Tamaño:</span> {animal.tamano}</p>
                  <p><span className="font-semibold">Compatible con Niños:</span> {animal.compatibleNiños ? 'Sí' : 'No'}</p>
                  <p><span className="font-semibold">Refugio:</span> {animal.refugioCiudad}</p>
                </div>

                <button
                  onClick={() => handleSolicitar(animal.animal_id, animal.nombre)}
                  className={`mt-6 w-full py-3 rounded-2xl font-semibold text-white transition ${
                    animal.estadoAdopcion === 'Disponible' ? 'bg-[#2B6777] hover:bg-[#1f4a56]' : 'bg-gray-400 cursor-not-allowed'
                  }`}
                >
                  {animal.estadoAdopcion === 'Disponible' ? 'Solicitar Adopción' : 'No Disponible'}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  </div>
);




};

export default RecomendacionesPage;