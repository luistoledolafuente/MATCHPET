import React, { useState, useEffect } from "react";
import { getFavorites, removeFavorite } from "../../services/animalService";
import { useAuth } from "../../contexts/AuthContext";
import { Loader2, Heart, PawPrint, MapPin, Trash2, User } from "lucide-react";
import { Link } from "react-router-dom";

const BACKEND_BASE_URL = "http://127.0.0.1:8081";

export default function Favoritos() {
  const { token, isAuthenticated } = useAuth();
  const [favoritos, setFavoritos] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchFavoritos = async () => {
    setLoading(true);
    try {
      if (token && isAuthenticated) {
        const data = await getFavorites(token);
        setFavoritos(data);
      }
    } catch (error) {
      console.error("Error al cargar favoritos", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFavoritos();
  }, [token, isAuthenticated]);

  const handleRemove = async (animalId) => {
    const backup = [...favoritos];
    setFavoritos((prev) => prev.filter((a) => a.animal_id !== animalId));

    try {
      await removeFavorite(animalId, token);
    } catch (error) {
      console.error("Error al eliminar favorito", error);
      setFavoritos(backup);
      alert("No se pudo eliminar de favoritos. Inténtalo de nuevo.");
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col justify-center items-center min-h-screen bg-[#EAF7FD]">
        <Loader2 className="animate-spin w-12 h-12 text-[#316B7A] mb-4" />
        <p className="text-[#316B7A] text-lg font-medium">
          Cargando tus favoritos...
        </p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#EAF7FD] p-8 font-sans">
      <div className="max-w-7xl mx-auto">

        {/* Título */}
        <h1 className="text-3xl font-bold text-[#316B7A] flex items-center mb-8">
          <Heart className="w-8 h-8 mr-3" />
          Mis Mascotas Favoritas
        </h1>

        {/* Estado vacío */}
        {favoritos.length === 0 ? (
          <div className="text-center bg-white p-12 rounded-2xl shadow-md max-w-2xl mx-auto mt-10">
            <div className="bg-gray-100 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-6">
              <PawPrint className="w-12 h-12 text-gray-400" />
            </div>
            <h2 className="text-2xl font-bold text-gray-700 mb-2">
              Aún no tienes favoritos
            </h2>
            <p className="text-gray-500 mb-8 max-w-md mx-auto">
              ¡Parece que aún no te has enamorado! Explora las mascotas disponibles y guarda las que roben tu corazón.
            </p>
            <Link
              to="/dashboard/adoptante/mascotas"
              className="inline-flex items-center px-6 py-3 bg-[#316B7A] text-white font-bold rounded-lg hover:bg-[#265a66] transition shadow-md hover:shadow-lg"
            >
              <PawPrint className="w-5 h-5 mr-2" />
              Explorar Mascotas
            </Link>
          </div>
        ) : (
          /* Grid de favoritos */
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {favoritos.map((animal) => (
              <div
                key={animal.animal_id}
                className="bg-white rounded-xl border border-gray-400 overflow-hidden hover:shadow-xl transition flex flex-col relative group"
              >
                <button
                  onClick={() => handleRemove(animal.animal_id)}
                  className="absolute top-3 right-3 z-10 p-2 bg-white/90 backdrop-blur-sm rounded-full shadow-sm text-gray-400 hover:text-red-500 hover:bg-white transition"
                  title="Eliminar de favoritos"
                >
                  <Trash2 className="w-5 h-5" />
                </button>
                <div
                  key={animal.animal_id}
                  className="bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-xl transition flex flex-col relative group"
                >
                  {/* Imagen más grande */}
                  <div className="h-72 overflow-hidden bg-gray-100">
                    <img
                      src={animal.fotos?.[0] ? `${BACKEND_BASE_URL}${animal.fotos[0]}` : "https://placehold.co/400x300/e2e8f0/cbd5e1?text=Sin+Foto"}
                      alt={animal.nombre}
                      className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
                    />
                  </div>

                  {/* Info compacta */}
                  <div className="p-4 flex-1 flex flex-col">
                    <div className="flex justify-between items-start mb-1">
                      <h2 className="text-xl font-bold text-[#316B7A]">{animal.nombre}</h2>
                      <span className={`text-xs px-2 py-1 rounded-full font-medium ${animal.estadoAdopcion === "Disponible" ? "bg-green-100 text-green-700" : "bg-orange-100 text-orange-700"}`}>
                        {animal.estadoAdopcion}
                      </span>
                    </div>

                    <div className="flex items-center text-gray-600 text-sm mb-1">
                      <PawPrint className="w-4 h-4 mr-1 text-gray-500" />
                      {animal.raza || "Raza desconocida"}
                    </div>

                    <div className="flex items-center text-gray-500 text-sm mb-2">
                      <User className="w-4 h-4 mr-1 text-gray-500" />
                      {animal.genero || "?"}
                    </div>

                    <div className="flex items-center text-gray-500 text-xs mb-3 mt-auto">
                      <MapPin className="w-3 h-3 mr-1" />
                      {animal.refugioNombre} • {animal.refugioCiudad}
                    </div>

                    <Link
                      to={`/dashboard/adoptante/mascotas/${animal.animal_id}`}
                      className="block text-center w-full py-2 border border-gray-300 text-[#316B7A] font-semibold rounded-lg hover:bg-[#316B7A] hover:text-white transition text-sm"
                    >
                      Ver Detalles
                    </Link>
                  </div>
                </div>

              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
