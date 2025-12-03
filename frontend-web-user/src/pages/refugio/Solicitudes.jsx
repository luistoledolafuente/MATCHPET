import React, { useState, useEffect } from 'react';
import { PawPrint, Loader2, XCircle, Mail, User, Check, Ban, AlertTriangle, MessageSquare } from 'lucide-react';
import solicitudService from '../../services/solicitudService';
import { useAuth } from '../../contexts/AuthContext';

// Asunción de IDs (Verifica estos IDs en tu BD/modelo EstadoSolicitud.java)
const ESTADO_IDS = {
    ENVIADA: 1,
    EN_PROCESO: 2,
    APROBADA: 3,
    RECHAZADA: 4,
};

export default function Solicitudes() {
  const { isAuthenticated, token, loading: authLoading } = useAuth();
  const [solicitudes, setSolicitudes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // --- Estados para el Modal de Gestión ---
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedSolicitud, setSelectedSolicitud] = useState(null);
  const [gestionData, setGestionData] = useState({
      estadoSolicitudId: null,
      notasInternas: '',
      mensajeAlAdoptante: '',
  });
  const [submitting, setSubmitting] = useState(false);
  const [gestionError, setGestionError] = useState(null);
  // ------------------------------------------

  // Función para obtener la clase de color basada en el estado
  const getEstadoClass = (estadoNombre) => {
    switch (estadoNombre) {
        case 'Aprobada': return 'bg-green-100 text-green-800 border-green-500';
        case 'Rechazada': return 'bg-red-100 text-red-800 border-red-500';
        case 'En Proceso': return 'bg-blue-100 text-blue-800 border-blue-500';
        case 'Enviada':
        default: return 'bg-yellow-100 text-yellow-800 border-yellow-500';
    }
  };

  const fetchSolicitudes = async () => {
    if (authLoading || !token) {
      if (!token && !authLoading) {
        setError("Debes iniciar sesión como Refugio para ver las solicitudes.");
      }
      return;
    }

    setLoading(true);
    setError(null);

    try {
      // Endpoint GET /api/solicitudes/recibidas
      const data = await solicitudService.getSolicitudesRecibidas(token);
      setSolicitudes(data);
    } catch (err) {
      console.error("Error al cargar solicitudes:", err);
      const errMsg = err.response?.data?.message || err.message || "Error de conexión o permisos. Verifica que tu usuario es un Refugio.";
      if (err.response?.status === 401 || err.response?.status === 403) {
        setError("No autorizado. Asegúrate de tener el rol de Refugio.");
      } else {
        setError(`Error al cargar solicitudes: ${errMsg}`);
      }
    } finally {
      setLoading(false);
    }
  };

  // Función para abrir el modal de gestión
  const openGestionModal = (solicitud) => {
    setSelectedSolicitud(solicitud);
    // Inicializar el formulario con los datos actuales de la solicitud
    setGestionData({
        estadoSolicitudId: solicitud.estadoSolicitud?.estadoSolicitudId || null,
        notasInternas: solicitud.notasInternas || '',
        mensajeAlAdoptante: solicitud.mensajeAlAdoptante || '',
    });
    setGestionError(null);
    setIsModalOpen(true);
  };
  
  // Función para manejar el cambio en el formulario del modal
  const handleChange = (e) => {
    const { name, value } = e.target;
    setGestionData(prev => ({
        ...prev,
        // Convertir estadoSolicitudId a número
        [name]: name === 'estadoSolicitudId' ? parseInt(value) : value,
    }));
  };

  // Función corregida para enviar la actualización al backend
  const handleUpdateSolicitud = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setGestionError(null);

    if (!gestionData.estadoSolicitudId) {
        setGestionError("Debes seleccionar un estado para la solicitud.");
        setSubmitting(false);
        return;
    }
    
    try {
        // Endpoint PUT /api/solicitudes/{id} con el DTO completo
        await solicitudService.updateSolicitud(selectedSolicitud.id, gestionData, token);
        
        setIsModalOpen(false);
        fetchSolicitudes(); 
        alert(`Solicitud #${selectedSolicitud.id} actualizada a: ${Object.keys(ESTADO_IDS).find(key => ESTADO_IDS[key] === gestionData.estadoSolicitudId)}`);

    } catch (err) {
        console.error("Error al actualizar la solicitud:", err);
        const errMsg = err.response?.data?.message || "Error al actualizar la solicitud. Verifica el ID del estado.";
        setGestionError(errMsg);
    } finally {
        setSubmitting(false);
    }
  };

  useEffect(() => {
    if (token && !authLoading) {
      fetchSolicitudes();
    } else if (!authLoading && !token) {
      setLoading(false);
    }
  }, [token, authLoading]);
  const renderSolicitudes = () => (
    <div className="space-y-6">
        {solicitudes.map((solicitud) => (
            <div key={solicitud.id} className="bg-white rounded-xl shadow-lg border border-gray-200 overflow-hidden">
                <div className="p-5 border-b flex justify-between items-start">
                    <div>
                        <h2 className="text-2xl font-bold text-[#316B7A]">
                            Solicitud #{solicitud.id} para {solicitud.animal.nombre}
                        </h2>
                        <p className="text-sm text-gray-500 mt-1">
                            Enviada por: <span className="font-semibold text-gray-700">{solicitud.adoptante.nombreCompleto}</span>
                        </p>
                    </div>
                    <span className={`px-3 py-1 text-sm font-medium rounded-full border ${getEstadoClass(solicitud.estadoSolicitud?.nombre)}`}>
                        {solicitud.estadoSolicitud?.nombre || 'Desconocido'}
                    </span>
                </div>

                <div className="p-5 grid grid-cols-1 md:grid-cols-3 gap-6">
                    
                    {/* Detalles del Adoptante y Mensaje Inicial */}
                    <div className="space-y-3 md:col-span-2 border-r pr-6">
                        <h3 className="text-lg font-semibold text-[#316B7A] flex items-center">
                            <User className="w-4 h-4 mr-2" /> Datos de Contacto
                        </h3>
                        <p className="text-sm"><span className="font-medium">Email:</span> {solicitud.adoptante.email}</p>
                        <p className="text-sm"><span className="font-medium">Teléfono:</span> {solicitud.adoptante.telefono || 'N/A'}</p>

                        <h3 className="text-lg font-semibold text-[#316B7A] flex items-center pt-3">
                            <MessageSquare className="w-4 h-4 mr-2" /> Mensaje Inicial
                        </h3>
                        <blockquote className="text-gray-700 border-l-4 border-[#FDB2A0] pl-3 italic bg-gray-50 p-3 rounded-md">
                            {solicitud.mensajeAdoptante || 'Sin mensaje de presentación.'}
                        </blockquote>
                    </div>

                    {/* Acciones */}
                    <div className="md:col-span-1 space-y-3 pt-4 md:pt-0">
                        <h3 className="text-lg font-semibold text-gray-700">Gestión</h3>
                        <p className="text-sm text-gray-500">
                            Última Actualización: {new Date(solicitud.fechaActualizacion).toLocaleDateString()}
                        </p>
                        <button
                            onClick={() => openGestionModal(solicitud)}
                            className="w-full bg-[#316B7A] hover:bg-[#274f5a] text-white text-sm font-medium py-2 px-4 rounded-lg transition duration-150 flex items-center justify-center"
                        >
                            <Check className="w-4 h-4 mr-2" />
                            Gestionar Solicitud
                        </button>
                    </div>
                </div>
            </div>
        ))}
    </div>
  );
return (
  <div className="min-h-screen p-8 font-sans bg-gradient-to-b from-[#FFF7E6] to-[#AEEAFD]">

    <div className="max-w-7xl mx-auto space-y-8">

      <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center pb-4 border-b border-[#FDB2A0]">
        <h1 className="text-4xl font-extrabold text-[#316B7A] flex items-center mb-4 sm:mb-0">
          Solicitudes de Adopción <PawPrint className="w-8 h-8 mr-3 text-[#FDB2A0]" />
        </h1>
      </header>

      {(loading || authLoading) && (
        <div className="text-center p-8 text-[#316B7A]">
          <Loader2 className="w-8 h-8 animate-spin mx-auto mb-3" />
          Cargando solicitudes...
        </div>
      )}

      {error && (
        <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded-lg flex items-center">
          <XCircle className="w-5 h-5 mr-3" />
          {error}
        </div>
      )}

      {!loading && !authLoading && solicitudes.length > 0 && renderSolicitudes()}

      {!loading && !authLoading && solicitudes.length === 0 && isAuthenticated && (
        <div className="text-center p-16 bg-white/70 backdrop-blur rounded-3xl shadow-xl">
          <PawPrint className="w-12 h-12 mx-auto text-[#316B7A] opacity-50 mb-4" />
          <p className="text-xl text-gray-600">No hay solicitudes de adopción por el momento.</p>
        </div>
      )}

      {!loading && !authLoading && !isAuthenticated && !error && (
        <div className="text-center p-16 bg-white/70 backdrop-blur rounded-3xl shadow-xl">
          <XCircle className="w-12 h-12 mx-auto text-red-400 mb-4" />
          <p className="text-xl text-gray-600">Por favor, inicia sesión como Refugio para ver las solicitudes.</p>
        </div>
      )}

    </div>

    {/* --- Modal de Gestión de Solicitudes (PUT) --- */}
    {isModalOpen && selectedSolicitud && (
      <div className="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
        <div className="bg-white rounded-lg shadow-2xl p-8 w-full max-w-lg">
          <h3 className="text-2xl font-bold mb-4 text-gray-800">Gestionar Solicitud #{selectedSolicitud.id}</h3>
          <p className="mb-4 text-gray-600">Para: <strong>{selectedSolicitud.animal.nombre}</strong> - Adoptante: <strong>{selectedSolicitud.adoptante.nombreCompleto}</strong></p>
          
          <form onSubmit={handleUpdateSolicitud}>
            
            {/* Selector de Estado */}
            <div className="mb-4">
              <label htmlFor="estadoSolicitudId" className="block text-sm font-medium text-gray-700 mb-1">Cambiar Estado *</label>
              <select
                id="estadoSolicitudId"
                name="estadoSolicitudId"
                value={gestionData.estadoSolicitudId || ''}
                onChange={handleChange}
                className="w-full p-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
                disabled={submitting}
                required
              >
                <option value="">-- Seleccionar Nuevo Estado --</option>
                <option value={ESTADO_IDS.ENVIADA}>1. Enviada (Recibida)</option>
                <option value={ESTADO_IDS.EN_PROCESO}>2. En Proceso (Revisando)</option>
                <option value={ESTADO_IDS.APROBADA}>3. Aprobada</option>
                <option value={ESTADO_IDS.RECHAZADA}>4. Rechazada</option>
              </select>
            </div>

            {/* Mensaje al Adoptante */}
            <div className="mb-4">
              <label htmlFor="mensajeAlAdoptante" className="block text-sm font-medium text-gray-700 mb-1">Mensaje al Adoptante (Opcional)</label>
              <textarea
                id="mensajeAlAdoptante"
                name="mensajeAlAdoptante"
                rows="3"
                className="w-full p-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
                value={gestionData.mensajeAlAdoptante}
                onChange={handleChange}
                disabled={submitting}
                placeholder="Escribe aquí la respuesta oficial que verá el adoptante."
              ></textarea>
            </div>
            
            {/* Notas Internas */}
            <div className="mb-6">
              <label htmlFor="notasInternas" className="block text-sm font-medium text-gray-700 mb-1">Notas Internas (Solo para el Refugio)</label>
              <textarea
                id="notasInternas"
                name="notasInternas"
                rows="3"
                className="w-full p-2 border border-gray-300 rounded-lg bg-gray-50 focus:ring-indigo-500 focus:border-indigo-500"
                value={gestionData.notasInternas}
                onChange={handleChange}
                disabled={submitting}
                placeholder="Registra comentarios privados de gestión."
              ></textarea>
            </div>
            
            {gestionError && (
              <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-3 mb-4 rounded-md flex items-center">
                <AlertTriangle className="w-5 h-5 mr-3" />
                <p className="text-sm">{gestionError}</p>
              </div>
            )}

            <div className="flex justify-end space-x-3">
              <button
                type="button"
                onClick={() => setIsModalOpen(false)}
                className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition duration-150"
                disabled={submitting}
              >
                Cerrar
              </button>
              <button
                type="submit"
                className="px-4 py-2 bg-[#316B7A] text-white font-semibold rounded-lg hover:bg-[#274f5a] transition duration-150 disabled:opacity-50 flex items-center"
                disabled={submitting}
              >
                {submitting ? (
                  <Loader2 className="w-5 h-5 animate-spin mr-2" />
                ) : (
                  <Check className="w-4 h-4 mr-2" />
                )}
                {submitting ? 'Guardando...' : 'Guardar Cambios'}
              </button>
            </div>
          </form>
        </div>
      </div>
    )}
  </div>
);

}