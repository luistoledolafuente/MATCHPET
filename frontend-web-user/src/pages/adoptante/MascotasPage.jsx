import React, { useState, useEffect, useMemo } from "react";
import animalService, { addFavorite, removeFavorite, getFavorites } from "../../services/animalService";
import { createSolicitud } from "../../services/solicitudService";
import { Loader2, XCircle, PawPrint, CheckCircle, Heart, MapPin, Search } from "lucide-react";
import { Link } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

const BACKEND_BASE_URL = "http://127.0.0.1:8081";

export default function MascotasPage() {
  const { token, authLoading, isAuthenticated } = useAuth();

  // --- Estados de datos ---
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // --- Favoritos ---
  const [favoritosIds, setFavoritosIds] = useState(new Set());
  const [favLoading, setFavLoading] = useState(false);

  // --- Modal de solicitud ---
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedAnimalId, setSelectedAnimalId] = useState(null);
  const [selectedAnimalNombre, setSelectedAnimalNombre] = useState('');
  const [mensajeAdoptante, setMensajeAdoptante] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [submissionStatus, setSubmissionStatus] = useState({ success: false, error: null });

  // --- Buscador ---
  const [searchTerm, setSearchTerm] = useState('');

  // --- Cargar datos ---
  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await animalService.getAnimalesPaginados(0, 20);
      setMascotas(data.content);
      if (isAuthenticated && token) {
        try {
          const misFavs = await getFavorites(token);
          setFavoritosIds(new Set(misFavs.map(a => a.animal_id)));
        } catch (favErr) {
          console.error("Error favoritos:", favErr);
        }
      }
    } catch (err) {
      console.error(err);
      setError("No se pudieron cargar las mascotas.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!authLoading) fetchData();
  }, [authLoading, isAuthenticated, token]);

  // --- Filtrado por buscador ---
  const filteredMascotas = useMemo(() => {
    if (!searchTerm) return mascotas;
    const term = searchTerm.toLowerCase();
    return mascotas.filter(a =>
      a.nombre.toLowerCase().includes(term) ||
      (a.raza || "").toLowerCase().includes(term) ||
      (a.refugioNombre || "").toLowerCase().includes(term)
    );
  }, [mascotas, searchTerm]);

  // --- Favoritos ---
  const handleToggleFavorite = async (animal) => {
    if (!isAuthenticated || favLoading) return;
    const id = animal.animal_id;
    const isFav = favoritosIds.has(id);

    setFavoritosIds(prev => {
      const newSet = new Set(prev);
      if (isFav) newSet.delete(id); else newSet.add(id);
      return newSet;
    });

    setFavLoading(true);
    try {
      if (isFav) await removeFavorite(id, token);
      else await addFavorite(id, token);
    } catch (err) {
      console.error(err);
      setFavoritosIds(prev => {
        const revertSet = new Set(prev);
        if (isFav) revertSet.add(id); else revertSet.delete(id);
        return revertSet;
      });
      alert("Error al guardar favorito.");
    } finally {
      setFavLoading(false);
    }
  };

  // --- Modal ---
  const openSolicitudModal = (id, nombre) => {
    setSelectedAnimalId(id);
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
  };
  const handleSolicitarAdopcion = async (e) => {
    e.preventDefault();
    if (!mensajeAdoptante.trim()) {
      setSubmissionStatus({ success: false, error: "El mensaje es obligatorio." });
      return;
    }
    setSubmitting(true);
    try {
      await createSolicitud({ animalId: selectedAnimalId, mensajeAdoptante: mensajeAdoptante.trim() }, token);
      setSubmissionStatus({ success: true, error: null });
      setTimeout(() => closeModal(), 1500);
    } catch (err) {
      console.error(err);
      setSubmissionStatus({ success: false, error: "Ocurrió un error al enviar la solicitud." });
    } finally { setSubmitting(false); }
  };

  // --- Render ---
  return (
    <div className="bg-gradient-to-b from-[#AEEAFD] to-[#FFF8F0] min-h-screen p-6 font-sans">
      <div className="max-w-7xl mx-auto space-y-6">
        <h1 className="text-3xl font-extrabold text-[#2B6777] flex items-center mb-4">
          <PawPrint className="w-8 h-8 mr-3 text-[#FFB6A3]" />
          Explorar Mascotas
        </h1>

        {/* Buscador */}
        <div className="relative w-full max-w-xl mb-6 ">
          <input
            type="text"
            placeholder="Buscar por nombre, raza o refugio..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full py-3 pl-10 pr-4 rounded-xl border border-gray-300 focus:ring-2 focus:ring-[#AEEAFD] focus:border-[#2B6777] transition"
          />
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
        </div>

        {/* Loading */}
        {(loading || authLoading) && (
          <div className="text-center py-8 text-[#2B6777]">
            <Loader2 className="w-8 h-8 animate-spin mx-auto mb-2" />
            Cargando mascotas...
          </div>
        )}

        {/* Error */}
        {error && (
          <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded-lg flex items-center">
            <XCircle className="w-5 h-5 mr-2" /> {error}
          </div>
        )}

        {/* Grid */}
        {!loading && !error && filteredMascotas.length === 0 && (
          <p className="text-center text-gray-600 py-20 bg-white/70 rounded-2xl">No hay mascotas que coincidan con tu búsqueda.</p>
        )}

        {!loading && !error && filteredMascotas.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredMascotas.map(animal => {
              const isFav = favoritosIds.has(animal.animal_id);
              return (
                <div key={animal.animal_id} className="bg-white rounded-2xl shadow-md overflow-hidden hover:shadow-2xl transition relative">
                  {/* Favorito */}
                  {isAuthenticated && (
                    <button
                      onClick={() => handleToggleFavorite(animal)}
                      className="absolute top-3 right-3 p-2 bg-white/90 rounded-full shadow-sm hover:scale-110 transition z-10"
                      title={isFav ? "Quitar favorito" : "Añadir favorito"}
                    >
                      <Heart className={`w-6 h-6 transition-colors ${isFav ? 'fill-red-500 text-red-500' : 'text-gray-400 hover:text-red-400'}`} />
                    </button>
                  )}

                  {/* Imagen */}
                  <div className="h-48 overflow-hidden">
                    <img
                      src={animal.fotos?.[0] ? `${BACKEND_BASE_URL}${animal.fotos[0]}` : "https://placehold.co/400x300/E0E0E0/2B6777?text=Mascota"}
                      alt={animal.nombre}
                      className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
                    />
                  </div>

                  {/* Info */}
                  {/* Info */}
                  <div className="p-4 flex flex-col space-y-2">
                    {/* Nombre + Estado */}
                    <div className="flex justify-between items-center">
                      <h2 className="text-xl font-bold text-[#2B6777]">{animal.nombre}</h2>
                      <span
                        className={`text-xs px-2 py-1 rounded-full font-medium ${animal.estadoAdopcion === 'Disponible'
                          ? 'bg-green-100 text-green-700'
                          : 'bg-gray-200 text-gray-600'
                          }`}
                      >
                        {animal.estadoAdopcion}
                      </span>
                    </div>
                    {/* Refugio */}
                    <div className="flex items-center text-xs text-gray-500 space-x-1">
                      <MapPin className="w-4 h-4" />
                      <span>{animal.refugioNombre} - {animal.refugioCiudad}</span>
                    </div>
                    {/* Raza */}
                    <div className="text-sm text-gray-500 font-medium">
                      Raza: {animal.raza || "Desconocida"}
                    </div>

                    {/* Género */}
                    <div className="text-sm text-gray-500 font-medium">
                      Género: {animal.genero || "?"}
                    </div>

                    {/* Botones */}
                    <div className="flex flex-col space-y-4 mt-2">
                      {/* Ver Detalles */}
                      <Link
                        to={`/dashboard/adoptante/mascotas/${animal.animal_id}`}
                        className="w-full py-2 border border-[#FDB2A0] text-[#FDB2A0] font-semibold rounded-lg text-center hover:bg-[#FDB2A0] hover:text-white transition"

                      >
                        Ver Detalles
                      </Link>
                      {/* Solicitar Adopción */}
                      <button
                        onClick={() => openSolicitudModal(animal.animal_id, animal.nombre)}
                        className="w-full py-2 bg-[#2B6777] text-white rounded-lg font-semibold hover:bg-[#1f4a56] transition disabled:opacity-50"
                        disabled={animal.estadoAdopcion !== 'Disponible' || !isAuthenticated}
                      >
                        {animal.estadoAdopcion === 'Disponible'
                          ? (isAuthenticated ? "Solicitar Adopción" : "Inicia sesión")
                          : "No disponible"}
                      </button>
                    </div>
                  </div>

                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* --- Modal --- */}
      {isModalOpen && selectedAnimalId && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4 backdrop-blur-sm">
          <div className="bg-white rounded-2xl shadow-2xl p-6 w-full max-w-md transform transition-all scale-100">
            {submissionStatus.success ? (
              <div className="text-center py-6 animate-fadeIn">
                <CheckCircle className="w-16 h-16 mx-auto text-green-500 mb-4" />
                <h3 className="text-2xl font-bold text-gray-800 mb-2">¡Solicitud enviada!</h3>
                <p className="text-gray-600 mb-6">Tu interés por <strong>{selectedAnimalNombre}</strong> ha sido comunicado al refugio.</p>
                <button onClick={closeModal} className="w-full py-2 bg-[#2B6777] text-white rounded-lg font-semibold hover:bg-[#1f4a56] transition">Entendido</button>
              </div>
            ) : (
              <>
                <h3 className="text-2xl font-bold mb-2 text-[#2B6777]">Adoptar a {selectedAnimalNombre}</h3>
                <p className="mb-4 text-gray-500 text-sm">Cuéntale al refugio por qué serías la familia ideal.</p>
                <form onSubmit={handleSolicitarAdopcion} className="space-y-3">
                  <textarea
                    value={mensajeAdoptante}
                    onChange={e => setMensajeAdoptante(e.target.value)}
                    rows="4"
                    placeholder="Hola, me gustaría adoptar a..."
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#2B6777] outline-none transition"
                    required
                    disabled={submitting}
                  />
                  {submissionStatus.error && (
                    <div className="bg-red-50 border-l-4 border-red-500 text-red-700 p-3 rounded flex items-start text-sm">
                      <XCircle className="w-5 h-5 mr-2 mt-0.5" /> {submissionStatus.error}
                    </div>
                  )}
                  <div className="flex justify-end space-x-3 pt-2">
                    <button type="button" onClick={closeModal} className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50" disabled={submitting}>Cancelar</button>
                    <button type="submit" className="px-4 py-2 bg-[#2B6777] text-white rounded-lg font-semibold hover:bg-[#1f4a56] flex items-center disabled:opacity-70" disabled={submitting}>
                      {submitting ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
                      {submitting ? "Enviando..." : "Confirmar Solicitud"}
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
