import React, { useState, useEffect } from 'react';
import { PawPrint, Loader2, XCircle } from 'lucide-react';
import solicitudService from '../../services/solicitudService';
import { useAuth } from '../../contexts/AuthContext';

export default function Solicitudes() {
  const { isAuthenticated, token, loading: authLoading } = useAuth();
  const [solicitudes, setSolicitudes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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
      const data = await solicitudService.getSolicitudesRecibidas(token);
      setSolicitudes(data);
    } catch (err) {
      console.error("Error al cargar solicitudes:", err);
      const errMsg = err.response?.data?.message || err.message || "Error de conexión o permisos.";

      if (err.response?.status === 401) {
        setError("Sesión expirada o permisos insuficientes. Por favor, vuelve a iniciar sesión.");
      } else {
        setError(`Error al cargar solicitudes: ${errMsg}`);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleActualizarEstado = async (id, estado) => {
    try {
      await solicitudService.updateSolicitud(id, estado, token);
      fetchSolicitudes(); // refrescar lista
    } catch (err) {
      console.error("Error al actualizar solicitud:", err);
      setError(err.response?.data?.message || "No se pudo actualizar la solicitud.");
    }
  };

  useEffect(() => {
    if (token && !authLoading) {
      fetchSolicitudes();
    } else if (!authLoading && !token) {
      setLoading(false);
    }
  }, [token, authLoading]);

  return (
    <div className="bg-[#FFF7E6] min-h-screen p-8 font-sans">
      <div className="max-w-7xl mx-auto space-y-8">
        <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center pb-4 border-b border-[#FDB2A0]">
          <h1 className="text-4xl font-extrabold text-[#316B7A] flex items-center mb-4 sm:mb-0">
            <PawPrint className="w-8 h-8 mr-3 text-[#FDB2A0]" />
            Solicitudes de Adopción
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

        {!loading && !authLoading && solicitudes.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-1 lg:grid-cols-1 gap-6">
            {solicitudes.map((sol, index) => (
              <div
                key={sol.id ?? `temp-${index}`}
                className="bg-white rounded-xl shadow-lg overflow-hidden transition-all hover:shadow-2xl p-4 flex flex-col md:flex-row justify-between items-center"
              >
                <div className="mb-4 md:mb-0">
                  <h2 className="text-2xl font-bold text-[#316B7A] mb-1">{sol.animalNombre}</h2>
                  <p className="text-sm text-gray-500 mb-1">Solicitante: {sol.adoptanteNombre}</p>
                  <p className="text-sm text-gray-700">Estado: {sol.estado}</p>
                </div>
                <div className="flex space-x-2">
                  <button
                    onClick={() => handleActualizarEstado(sol.id, "Aprobada")}
                    className="px-4 py-2 rounded-full bg-[#FDB2A0] text-[#316B7A] hover:bg-[#ffc3b3] transition"
                  >
                    Aprobar
                  </button>
                  <button
                    onClick={() => handleActualizarEstado(sol.id, "Rechazada")}
                    className="px-4 py-2 rounded-full bg-red-500 text-white hover:bg-red-700 transition"
                  >
                    Rechazar
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {!loading && !authLoading && solicitudes.length === 0 && isAuthenticated && (
          <div className="text-center p-16 bg-white rounded-2xl shadow-xl">
            <PawPrint className="w-12 h-12 mx-auto text-[#316B7A] opacity-50 mb-4" />
            <p className="text-xl text-gray-600">No hay solicitudes de adopción por el momento.</p>
          </div>
        )}

        {!loading && !authLoading && !isAuthenticated && !error && (
          <div className="text-center p-16 bg-white rounded-2xl shadow-xl">
            <XCircle className="w-12 h-12 mx-auto text-red-400 mb-4" />
            <p className="text-xl text-gray-600">Por favor, inicia sesión como Refugio para ver las solicitudes.</p>
          </div>
        )}
      </div>
    </div>
  );
}
