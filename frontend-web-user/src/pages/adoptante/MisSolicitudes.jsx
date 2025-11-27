import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { getMisSolicitudes } from '../../services/solicitudService';
import { Loader2, XCircle, PawPrint, Calendar, Mail, User, Heart, Home } from 'lucide-react';

const MisSolicitudes = () => {
    const { token } = useAuth();
    const [solicitudes, setSolicitudes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

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
    
    // Asumimos que la base URL para fotos está en las variables de entorno o definida
    const BACKEND_BASE_URL = "http://127.0.0.1:8081";

    useEffect(() => {
        const fetchSolicitudes = async () => {
            try {
                if (!token) return;
                const data = await getMisSolicitudes(token);
                setSolicitudes(data);
            } catch (err) {
                console.error("Error al cargar mis solicitudes:", err);
                setError("Hubo un error al cargar tus solicitudes. Asegúrate de estar logueado y ser un adoptante.");
            } finally {
                setLoading(false);
            }
        };

        fetchSolicitudes();
    }, [token]);

    if (loading) {
        return (
            <div className="text-center py-20 text-indigo-600">
                <Loader2 className="w-8 h-8 animate-spin mx-auto mb-3" />
                Cargando tu historial de solicitudes...
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
        <div className="p-6 max-w-7xl mx-auto">
            <h1 className="text-4xl font-extrabold mb-8 text-indigo-700 flex items-center">
                <Heart className="w-8 h-8 mr-3 text-red-500" />
                Mi Historial de Solicitudes de Adopción
            </h1>
            
            {solicitudes.length === 0 ? (
                <div className="text-center p-10 bg-indigo-50 rounded-xl border-2 border-dashed border-indigo-200">
                    <PawPrint className="w-12 h-12 mx-auto text-indigo-400 mb-4" />
                    <p className="font-bold text-xl text-indigo-700">Aún no tienes solicitudes enviadas.</p>
                    <p className="text-gray-600 mt-2">¡Es momento de encontrar a tu nuevo compañero peludo!</p>
                </div>
            ) : (
                <div className="space-y-6">
                    {solicitudes.map((solicitud) => (
                        <div key={solicitud.id} className="bg-white rounded-xl shadow-xl overflow-hidden border border-gray-100">
                            
                            {/* CABECERA Y ESTADO */}
                            <div className="p-6 flex justify-between items-start border-b border-gray-100">
                                <div>
                                    <h2 className="text-2xl font-bold text-gray-800 flex items-center">
                                        Solicitud para: {solicitud.animal.nombre}
                                    </h2>
                                    <p className="text-sm text-gray-500 mt-1 flex items-center">
                                        <Calendar className="w-4 h-4 mr-1" />
                                        Enviada el: {new Date(solicitud.fechaSolicitud).toLocaleDateString()}
                                    </p>
                                </div>
                                <div className={`px-4 py-1.5 text-sm font-semibold rounded-full border-2 ${getEstadoClass(solicitud.estadoSolicitud.nombre)}`}>
                                    {solicitud.estadoSolicitud.nombre.toUpperCase()}
                                </div>
                            </div>
                            
                            <div className="p-6 grid md:grid-cols-3 gap-6">
                                {/* Información de la Mascota */}
                                <div className="md:col-span-1 border-r pr-6">
                                    <h3 className="text-lg font-semibold text-indigo-600 mb-3">Detalles de la Mascota</h3>
                                    <img
                                        src={
                                            solicitud.animal.fotos?.[0]
                                            ? `${BACKEND_BASE_URL}${solicitud.animal.fotos[0]}`
                                            : "https://placehold.co/150x150/f0f4f8/94a3b8?text=Mascota"
                                        }
                                        alt={`Foto de ${solicitud.animal.nombre}`}
                                        className="w-full h-32 object-cover rounded-lg mb-3"
                                    />
                                    <p className="text-sm">
                                        <span className="font-medium">Raza:</span> {solicitud.animal.raza}
                                    </p>
                                    <p className="text-sm flex items-center">
                                        <Home className="w-4 h-4 mr-1 text-gray-500" />
                                        <span className="font-medium">Refugio:</span> {solicitud.animal.refugioNombre} ({solicitud.animal.refugioCiudad})
                                    </p>
                                </div>

                                {/* Mensajes y Notas */}
                                <div className="md:col-span-2 space-y-4">
                                    
                                    {/* Mi Mensaje */}
                                    <div>
                                        <h3 className="text-lg font-semibold text-gray-700 flex items-center mb-1">
                                            <Mail className="w-4 h-4 mr-1" /> Mi Mensaje al Refugio
                                        </h3>
                                        <blockquote className="text-gray-600 border-l-4 border-indigo-300 pl-3 italic bg-indigo-50 p-3 rounded-md">
                                            {solicitud.mensajeAdoptante || 'N/A'}
                                        </blockquote>
                                    </div>

                                    {/* Respuesta del Refugio */}
                                    {solicitud.mensajeAlAdoptante && (
                                        <div>
                                            <h3 className="text-lg font-semibold text-gray-700 flex items-center mb-1">
                                                <User className="w-4 h-4 mr-1" /> Respuesta del Refugio
                                            </h3>
                                            <p className="text-gray-700 bg-gray-50 p-3 border rounded-md">
                                                {solicitud.mensajeAlAdoptante}
                                            </p>
                                        </div>
                                    )}
                                    
                                    {/* Notas Internas (Solo si están expuestas, que no deberían estarlo, pero si lo están en el DTO, las mostramos) */}
                                    {solicitud.notasInternas && (
                                        <div className="text-sm text-red-500">
                                            <span className="font-bold">Advertencia:</span> Se están mostrando notas internas del refugio.
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default MisSolicitudes;