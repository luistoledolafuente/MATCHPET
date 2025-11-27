import React, { useState, useEffect } from "react";
// Importar solo animalService (asumiendo que ya tiene getAnimalesPaginados)
import animalService from "../../services/animalService"; 
import { Loader2, XCircle, PawPrint } from "lucide-react";
import { useAuth } from "../../contexts/AuthContext"; // Mantener por si se usa isAuthenticated/authLoading

const BACKEND_BASE_URL = "http://127.0.0.1:8081";
// NO NECESITAMOS API_URL NI AXIOS AQUÍ.

export default function MascotasPage() {
  const { authLoading, isAuthenticated } = useAuth(); // Mantener para el botón de adopción
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // FUNCIÓN PARA CARGAR EL FEED GLOBAL
  const fetchGlobalMascotas = async () => {
    setLoading(true);
    setError(null);
    try {
      // LLAMADA AL ENDPOINT PÚBLICO: GET /api/animales
      const data = await animalService.getAnimalesPaginados(0, 10); 
      setMascotas(data.content); // data.content es la lista de AnimalDTOs
    } catch (err) {
      console.error("Error al cargar animales:", err);
      const errMsg = err.response?.data?.message || err.message || "Error de conexión al cargar el feed.";
      setError(`No se pudieron cargar las mascotas: ${errMsg}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // Solo cargamos el feed cuando la autenticación haya terminado de cargar.
    if (!authLoading) {
      fetchGlobalMascotas();
    }
  }, [authLoading]);


  // OMITIMOS handleSolicitarAdopcion por ahora, pero se puede añadir fácilmente

  return (
    <div className="bg-[#FFF7E6] min-h-screen p-8 font-sans">
      <div className="max-w-7xl mx-auto space-y-8">
        <h1 className="text-3xl font-bold text-[#316B7A] flex items-center mb-4">
          <PawPrint className="w-8 h-8 mr-3 text-[#FDB2A0]" />
          Explorar Mascotas Disponibles 
        </h1>

        {(loading || authLoading) && (
          <div className="text-center p-8 text-[#316B7A]">
            <Loader2 className="w-8 h-8 animate-spin mx-auto mb-3" />
            Cargando mascotas...
          </div>
        )}

        {error && (
          <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded-lg flex items-center">
            <XCircle className="w-5 h-5 mr-3" />
            {error}
          </div>
        )}

        {!loading && !error && mascotas.length === 0 && (
          <div className="text-center p-16 bg-white rounded-2xl shadow-xl">
            <PawPrint className="w-12 h-12 mx-auto text-[#316B7A] opacity-50 mb-4" />
            <p className="text-xl text-gray-600">
              No hay mascotas disponibles para adopción en este momento.
            </p>
          </div>
        )}

        {!loading && !error && mascotas.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {mascotas.map((animal) => (
              <div key={animal.animal_id} className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-2xl transition">
                <img
                  // 2. MODIFICACIÓN CRÍTICA AQUÍ: Concatenar la URL base
                  src={
                    animal.fotos?.[0]
                      ? `${BACKEND_BASE_URL}${animal.fotos[0]}` // Ejemplo: http://127.0.0.1:8081/api/animales/files/xyz.jpg
                      : "https://placehold.co/400x300/a8d8e0/316B7A?text=No+Photo"
                  }
                  alt={`Foto de ${animal.nombre}`}
                  className="w-full h-48 object-cover"
                />
                <div className="p-4">
                  <h2 className="text-2xl font-bold text-[#316B7A] mb-1">{animal.nombre}</h2>
                  <p className="text-sm text-gray-500 mb-1">{animal.raza || "Raza desconocida"} ({animal.genero || "Género desconocido"})</p>
                  <p className="text-sm text-gray-700">{animal.descripcionPersonalidad || "Sin descripción"}</p>
                  <p className="text-sm text-gray-700"><strong>Refugio:</strong> {animal.refugioNombre} - {animal.refugioCiudad}</p>
                  
                  {/* Botón de Solicitud (Requiere isAuthenticated) */}
                  <div className="mt-4 flex justify-end">
                    <button
                      // onClick={() => handleSolicitarAdopcion(animal.animal_id)} // Función aún no implementada
                      className="bg-[#316B7A] hover:bg-[#316B7A]/90 text-white font-bold py-2 px-4 rounded-lg transition duration-150"
                      disabled={!isAuthenticated} 
                    >
                      {isAuthenticated ? "Solicitar Adopción" : "Inicia sesión para adoptar"}
                    </button>
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