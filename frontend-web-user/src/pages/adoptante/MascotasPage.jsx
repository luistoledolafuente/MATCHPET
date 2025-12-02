import React, { useState, useEffect } from "react";
// Importamos el servicio por defecto y las funciones específicas de favoritos
import animalService, { addFavorite, removeFavorite, getFavorites } from "../../services/animalService";
import { createSolicitud } from "../../services/solicitudService"; 
// Importamos los iconos necesarios, incluyendo el 'Heart' para favoritos
import { Loader2, XCircle, PawPrint, CheckCircle, Heart } from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";

const BACKEND_BASE_URL = "http://127.0.0.1:8081";

export default function MascotasPage() {
  const { token, authLoading, isAuthenticated } = useAuth();
  
  // Estados de datos
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // --- Estados para Favoritos ---
  const [favoritosIds, setFavoritosIds] = useState(new Set()); // Usamos un Set para búsqueda rápida O(1)
  const [favLoading, setFavLoading] = useState(false);

  // --- Estados para el Modal de Solicitud ---
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedAnimalId, setSelectedAnimalId] = useState(null);
  const [selectedAnimalNombre, setSelectedAnimalNombre] = useState('');
  const [mensajeAdoptante, setMensajeAdoptante] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [submissionStatus, setSubmissionStatus] = useState({ 
    success: false, 
    error: null 
  });

  // 1. CARGAR DATOS (Mascotas + Favoritos si hay login)
  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      // a) Cargar el feed de mascotas (público)
      const data = await animalService.getAnimalesPaginados(0, 10);
      setMascotas(data.content);

      // b) Si el usuario está logueado, cargar sus favoritos para pintar los corazones rojos
      if (isAuthenticated && token) {
        try {
          const misFavs = await getFavorites(token);
          // Guardamos solo los IDs en un Set para saber rápido si un animal es fav
          const ids = new Set(misFavs.map(animal => animal.animal_id));
          setFavoritosIds(ids);
        } catch (favErr) {
          console.error("No se pudieron cargar favoritos (posiblemente token expirado o error red)", favErr);
          // No bloqueamos la página si fallan los favoritos, solo no se mostrarán rojos
        }
      }
    } catch (err) {
      console.error("Error al cargar datos:", err);
      const errMsg = err.response?.data?.message || err.message || "Error al conectar con el servidor.";
      setError(`No se pudieron cargar las mascotas: ${errMsg}`);
    } finally {
      setLoading(false);
    }
  };

  // Ejecutar fetchData cuando termina de cargar la sesión (authLoading) o cambia el token
  useEffect(() => {
    if (!authLoading) {
      fetchData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [authLoading, isAuthenticated, token]);


  // 2. MANEJAR CLIC EN FAVORITO
  const handleToggleFavorite = async (animal) => {
    if (!isAuthenticated) return; // Si no hay sesión, no hace nada (o podrías abrir login)
    if (favLoading) return;       // Evitar doble clic rápido

    const animalId = animal.animal_id;
    const isFav = favoritosIds.has(animalId);

    // --- Actualización Optimista (UI First) ---
    // Cambiamos el estado visualmente ANTES de llamar a la API para que se sienta instantáneo
    setFavoritosIds((prev) => {
      const newSet = new Set(prev);
      if (isFav) newSet.delete(animalId);
      else newSet.add(animalId);
      return newSet;
    });

    setFavLoading(true);

    try {
      if (isFav) {
        // Estaba en favoritos -> Eliminar
        await removeFavorite(animalId, token);
      } else {
        // No estaba -> Agregar
        await addFavorite(animalId, token);
      }
    } catch (err) {
      console.error("Error al actualizar favorito en servidor:", err);
      // Si falla, REVERTIMOS el cambio visual
      setFavoritosIds((prev) => {
        const revertSet = new Set(prev);
        if (isFav) revertSet.add(animalId); // Lo volvemos a poner
        else revertSet.delete(animalId);    // Lo volvemos a quitar
        return revertSet;
      });
      alert("Hubo un problema al guardar tu favorito. Intenta de nuevo.");
    } finally {
      setFavLoading(false);
    }
  };


  // --- Lógica del Modal (Igual que antes) ---
  const openSolicitudModal = (animalId, nombre) => {
    setSelectedAnimalId(animalId);
    setSelectedAnimalNombre(nombre);
    setMensajeAdoptante('');
    setSubmissionStatus({ success: false, error: null });
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedAnimalId(null);
    setSelectedAnimalNombre('');
    setMensajeAdoptante('');
    setSubmissionStatus({ success: false, error: null });
  };

  const handleSolicitarAdopcion = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setSubmissionStatus({ success: false, error: null });

    if (!mensajeAdoptante.trim()) {
      setSubmissionStatus({ success: false, error: "El mensaje es obligatorio." });
      setSubmitting(false);
      return;
    }

    try {
      await createSolicitud({
        animalId: selectedAnimalId,
        mensajeAdoptante: mensajeAdoptante.trim(),
      }, token);
      
      setSubmissionStatus({ success: true, error: null });
      setTimeout(() => {
        setIsModalOpen(false);
      }, 1500); 

    } catch (err) {
      console.error("Error solicitud:", err);
      const errMsg = err.response?.data?.message || "Ocurrió un error al enviar tu solicitud.";
      setSubmissionStatus({ success: false, error: errMsg });
    } finally {
      setSubmitting(false);
    }
  };


  // --- RENDERIZADO ---
  return (
    <div className="bg-[#FFF7E6] min-h-screen p-8 font-sans">
      <div className="max-w-7xl mx-auto space-y-8">
        <h1 className="text-3xl font-bold text-[#316B7A] flex items-center mb-4">
          <PawPrint className="w-8 h-8 mr-3 text-[#FDB2A0]" />
          Explorar Mascotas Disponibles
        </h1>

        {/* Loading Global */}
        {(loading || authLoading) && (
          <div className="text-center p-8 text-[#316B7A]">
            <Loader2 className="w-8 h-8 animate-spin mx-auto mb-3" />
            Cargando mascotas...
          </div>
        )}

        {/* Error Global */}
        {error && (
          <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded-lg flex items-center">
            <XCircle className="w-5 h-5 mr-3" />
            {error}
          </div>
        )}

        {/* Estado Vacío */}
        {!loading && !error && mascotas.length === 0 && (
          <div className="text-center p-16 bg-white rounded-2xl shadow-xl">
            <PawPrint className="w-12 h-12 mx-auto text-[#316B7A] opacity-50 mb-4" />
            <p className="text-xl text-gray-600">
              No hay mascotas disponibles para adopción en este momento.
            </p>
          </div>
        )}

        {/* Grid de Mascotas */}
        {!loading && !error && mascotas.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {mascotas.map((animal) => {
              // Verificamos si este animal específico está en el Set de favoritos
              const isFavorite = favoritosIds.has(animal.animal_id);

              return (
                <div key={animal.animal_id} className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-2xl transition relative group">
                  
                  {/* BOTÓN DE FAVORITOS (Solo si está logueado) */}
                  {isAuthenticated && (
                    <button
                      onClick={() => handleToggleFavorite(animal)}
                      className="absolute top-3 right-3 p-2 bg-white/90 rounded-full shadow-sm hover:bg-white hover:scale-110 transition z-10 cursor-pointer"
                      title={isFavorite ? "Quitar de favoritos" : "Añadir a favoritos"}
                    >
                      <Heart 
                        className={`w-6 h-6 transition-colors duration-300 ${
                          isFavorite 
                            ? "fill-red-500 text-red-500" // Rojo si es fav
                            : "text-gray-400 hover:text-red-400" // Gris si no
                        }`} 
                      />
                    </button>
                  )}

                  {/* Imagen */}
                  <div className="h-48 overflow-hidden">
                    <img
                      src={
                        animal.fotos?.[0]
                          ? `${BACKEND_BASE_URL}${animal.fotos[0]}`
                          : "https://placehold.co/400x300/a8d8e0/316B7A?text=No+Photo"
                      }
                      alt={`Foto de ${animal.nombre}`}
                      className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
                    />
                  </div>

                  {/* Info */}
                  <div className="p-4">
                    <div className="flex justify-between items-start">
                      <h2 className="text-2xl font-bold text-[#316B7A] mb-1">{animal.nombre}</h2>
                      {/* Badge de estado */}
                      <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                        animal.estadoAdopcion === 'Disponible' 
                          ? 'bg-green-100 text-green-700' 
                          : 'bg-gray-100 text-gray-600'
                      }`}>
                        {animal.estadoAdopcion}
                      </span>
                    </div>

                    <p className="text-sm text-gray-500 mb-1">{animal.raza || "Raza desconocida"} ({animal.genero || "?"})</p>
                    <p className="text-sm text-gray-700 line-clamp-2 min-h-[40px]">{animal.descripcionPersonalidad || "Sin descripción"}</p>
                    <p className="text-xs text-gray-500 mt-2">
                        📍 {animal.refugioNombre} - {animal.refugioCiudad}
                    </p>
                    
                    {/* Botón Acción */}
                    <div className="mt-4 flex justify-end">
                      <button
                        onClick={() => openSolicitudModal(animal.animal_id, animal.nombre)}
                        className="bg-[#316B7A] hover:bg-[#2A5C68] text-white font-bold py-2 px-4 rounded-lg transition duration-150 disabled:opacity-50 disabled:cursor-not-allowed text-sm"
                        disabled={!isAuthenticated || animal.estadoAdopcion !== 'Disponible'} 
                      >
                        {animal.estadoAdopcion !== 'Disponible' 
                           ? `No disponible` 
                           : isAuthenticated 
                              ? "Solicitar Adopción" 
                              : "Inicia sesión para adoptar"}
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* --- Modal de Solicitud (Overlay) --- */}
      {isModalOpen && selectedAnimalId && (
        <div className="fixed inset-0 bg-gray-900/50 backdrop-blur-sm flex items-center justify-center z-50 p-4 transition-opacity">
          <div className="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-md transform transition-all scale-100">
            
            {submissionStatus.success ? (
              // Vista Éxito
              <div className="text-center py-6 animate-fadeIn">
                <CheckCircle className="w-16 h-16 mx-auto text-green-500 mb-4" />
                <h3 className="text-2xl font-bold text-gray-800 mb-2">¡Solicitud Enviada!</h3>
                <p className="text-gray-600 mb-6">
                  Tu interés por <strong>{selectedAnimalNombre}</strong> ha sido comunicado al refugio.
                </p>
                <button
                    onClick={closeModal}
                    className="w-full px-4 py-2 bg-[#316B7A] text-white font-semibold rounded-lg hover:bg-[#2A5C68] transition"
                >
                    Entendido
                </button>
              </div>
            ) : (
              // Formulario
              <>
                <h3 className="text-2xl font-bold mb-2 text-[#316B7A]">Adoptar a {selectedAnimalNombre}</h3>
                <p className="mb-6 text-gray-500 text-sm">
                  Cuéntale al refugio por qué serías la familia ideal.
                </p>
                
                <form onSubmit={handleSolicitarAdopcion}>
                  <div className="mb-4">
                    <label htmlFor="mensaje" className="block text-sm font-medium text-gray-700 mb-1">
                      Mensaje de presentación
                    </label>
                    <textarea
                        id="mensaje"
                        rows="4"
                        placeholder="Hola, me gustaría adoptar a..."
                        className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#316B7A] focus:border-transparent outline-none transition"
                        value={mensajeAdoptante}
                        onChange={(e) => setMensajeAdoptante(e.target.value)}
                        disabled={submitting}
                        required
                    ></textarea>
                  </div>
                  
                  {submissionStatus.error && (
                    <div className="bg-red-50 border-l-4 border-red-500 text-red-700 p-3 mb-4 rounded text-sm flex items-start">
                        <XCircle className="w-5 h-5 mr-2 flex-shrink-0 mt-0.5" />
                        <span>{submissionStatus.error}</span>
                    </div>
                  )}

                  <div className="flex justify-end space-x-3 pt-2">
                    <button
                      type="button"
                      onClick={closeModal}
                      className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition"
                      disabled={submitting}
                    >
                      Cancelar
                    </button>
                    <button
                      type="submit"
                      className="px-4 py-2 bg-[#316B7A] text-white font-semibold rounded-lg hover:bg-[#2A5C68] transition disabled:opacity-70 flex items-center"
                      disabled={submitting}
                    >
                      {submitting ? (
                        <>
                          <Loader2 className="w-4 h-4 animate-spin mr-2" />
                          Enviando...
                        </>
                      ) : (
                        "Confirmar Solicitud"
                      )}
                    </button>
                  </div>
                </form>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}