import React, { useState, useEffect } from "react";
import animalService from "../../services/animalService";
import { createSolicitud } from "../../services/solicitudService"; // Importar el servicio de solicitud
import { Loader2, XCircle, PawPrint, CheckCircle } from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";

const BACKEND_BASE_URL = "http://127.0.0.1:8081";

export default function MascotasPage() {
  const { token, authLoading, isAuthenticated } = useAuth();
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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
  // ------------------------------------------

  // FUNCIÓN PARA CARGAR EL FEED GLOBAL
  const fetchGlobalMascotas = async () => {
    setLoading(true);
    setError(null);
    try {
      // LLAMADA AL ENDPOINT PÚBLICO: GET /api/animales
      const data = await animalService.getAnimalesPaginados(0, 10);
      setMascotas(data.content);
    } catch (err) {
      console.error("Error al cargar animales:", err);
      const errMsg = err.response?.data?.message || err.message || "Error de conexión al cargar el feed.";
      setError(`No se pudieron cargar las mascotas: ${errMsg}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!authLoading) {
      fetchGlobalMascotas();
    }
  }, [authLoading]);

  // FUNCIÓN PARA ABRIR EL MODAL
  const openSolicitudModal = (animalId, nombre) => {
    setSelectedAnimalId(animalId);
    setSelectedAnimalNombre(nombre);
    setMensajeAdoptante(''); // Limpiar mensaje anterior
    setSubmissionStatus({ success: false, error: null }); // Limpiar estado de envío
    setIsModalOpen(true);
  };

  // FUNCIÓN PARA ENVIAR LA SOLICITUD
  const handleSolicitarAdopcion = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setSubmissionStatus({ success: false, error: null });

    if (!mensajeAdoptante.trim()) {
      setSubmissionStatus({ success: false, error: "El mensaje es obligatorio." });
      setSubmitting(false);
      return;
    }

    const solicitudData = {
      animalId: selectedAnimalId,
      mensajeAdoptante: mensajeAdoptante.trim(), // ¡Importante: Usar el campo corregido!
    };

    try {
      await createSolicitud(solicitudData, token);
      setSubmissionStatus({ success: true, error: null });
      
      // Cierra el modal después de un breve momento para mostrar éxito
      setTimeout(() => {
        setIsModalOpen(false);
        // Opcional: Recargar la lista de mascotas si el estado de adopción cambia
        // fetchGlobalMascotas(); 
      }, 1500); 

    } catch (err) {
      console.error("Error al enviar la solicitud:", err);
      const errMsg = err.response?.data?.message || "Ocurrió un error al enviar tu solicitud.";
      setSubmissionStatus({ success: false, error: errMsg });
    } finally {
      setSubmitting(false);
    }
  };


  // Función para cerrar el modal y resetear el estado
  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedAnimalId(null);
    setSelectedAnimalNombre('');
    setMensajeAdoptante('');
    setSubmissionStatus({ success: false, error: null });
  };


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
                  src={
                    animal.fotos?.[0]
                      ? `${BACKEND_BASE_URL}${animal.fotos[0]}`
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
                      onClick={() => openSolicitudModal(animal.animal_id, animal.nombre)}
                      className="bg-[#316B7A] hover:bg-[#316B7A]/90 text-white font-bold py-2 px-4 rounded-lg transition duration-150 disabled:opacity-50"
                      disabled={!isAuthenticated || animal.estadoAdopcion !== 'Disponible'} 
                    >
                      {animal.estadoAdopcion !== 'Disponible' ? `Estado: ${animal.estadoAdopcion}` : 
                       isAuthenticated ? "Solicitar Adopción" : "Inicia sesión para adoptar"}
                    </button>
                  </div>
                  
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* --- Modal de Solicitud de Adopción --- */}
      {isModalOpen && selectedAnimalId && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl p-8 w-full max-w-md">
            
            {submissionStatus.success ? (
              // Vista de Éxito
              <div className="text-center py-6">
                <CheckCircle className="w-12 h-12 mx-auto text-green-500 mb-4" />
                <h3 className="text-xl font-bold text-gray-800 mb-2">¡Solicitud Enviada!</h3>
                <p className="text-gray-600">Tu solicitud para **{selectedAnimalNombre}** ha sido enviada con éxito. El refugio la revisará pronto.</p>
                <button
                    onClick={closeModal}
                    className="mt-4 px-4 py-2 bg-indigo-600 text-white font-semibold rounded-lg hover:bg-indigo-700 transition duration-150"
                >
                    Cerrar
                </button>
              </div>
            ) : (
              // Formulario de Solicitud
              <>
                <h3 className="text-2xl font-bold mb-4">Adoptar a {selectedAnimalNombre}</h3>
                <p className="mb-4 text-gray-600">Escribe un mensaje de presentación al refugio explicando por qué eres el adoptante ideal. (Obligatorio)</p>
                
                <form onSubmit={handleSolicitarAdopcion}>
                  <div className="mb-4">
                    <label htmlFor="mensaje" className="block text-sm font-medium text-gray-700 mb-1">Tu Mensaje</label>
                    <textarea
                        id="mensaje"
                        rows="4"
                        className="w-full p-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
                        value={mensajeAdoptante}
                        onChange={(e) => setMensajeAdoptante(e.target.value)}
                        disabled={submitting}
                        required
                    ></textarea>
                  </div>
                  
                  {submissionStatus.error && (
                    <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-3 mb-4 rounded-md flex items-center">
                        <XCircle className="w-5 h-5 mr-3" />
                        <p className="text-sm">{submissionStatus.error}</p>
                    </div>
                  )}

                  <div className="flex justify-end space-x-3">
                    <button
                      type="button"
                      onClick={closeModal}
                      className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition duration-150"
                      disabled={submitting}
                    >
                      Cancelar
                    </button>
                    <button
                      type="submit"
                      className="px-4 py-2 bg-indigo-600 text-white font-semibold rounded-lg hover:bg-indigo-700 transition duration-150 disabled:opacity-50 flex items-center"
                      disabled={submitting}
                    >
                      {submitting ? (
                        <Loader2 className="w-5 h-5 animate-spin mr-2" />
                      ) : (
                        <PawPrint className="w-4 h-4 mr-2" />
                      )}
                      {submitting ? 'Enviando...' : 'Confirmar Solicitud'}
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