import React, { useState, useEffect, useMemo } from 'react';
import { Calendar, Mail, Home, Search, Heart, Loader, User, Compass, X } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { getMisSolicitudes } from '../../services/solicitudService';
import { useNavigate } from 'react-router-dom';


const BACKEND_BASE_URL = "http://127.0.0.1:8081";

const SolicitudDetailModal = ({ solicitud, onClose }) => {
    if (!solicitud) return null;
    const getStateStyle = (estado) => {
        switch (estado) {
            case "Aprobada": return "text-white bg-[#2B6777]";
            case "Rechazada": return "text-white bg-[#FFB6A3]";
            case "En Proceso": return "text-[#2B6777] bg-[#AEEAFD]";
            case "Pendiente":
            default: return "text-gray-800 bg-gray-200";
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" onClick={onClose}>
            <div
                className="bg-white rounded-xl shadow-2xl w-full max-w-lg overflow-hidden transform transition-all duration-300 scale-100"
                onClick={(e) => e.stopPropagation()}
            >
                <div className="p-6 bg-[#2B6777] text-white flex justify-between items-center">
                    <h2 className="text-2xl font-bold">Detalles de la Solicitud</h2>
                    <button onClick={onClose} className="text-white hover:text-[#AEEAFD] transition-colors">
                        <X className="w-6 h-6" />
                    </button>
                </div>
                <div className="p-6 space-y-6">
                    <div className="border-b pb-4">
                        <h3 className="text-xl font-semibold text-[#5E5E5E]">Mascota:</h3>
                        <p className="text-3xl font-extrabold text-[#2B6777]">{solicitud.animal.nombre}</p>
                        <p className="text-gray-600">Refugio: {solicitud.animal.refugioNombre}</p>
                    </div>
                    <div className="flex justify-between items-center border-b pb-4">
                        <div>
                            <p className="text-lg font-semibold text-[#5E5E5E]">Estado Actual:</p>
                            <span className={`px-4 py-1.5 text-base font-bold rounded-full ${getStateStyle(solicitud.estadoSolicitud.nombre)}`}>
                                {solicitud.estadoSolicitud.nombre}
                            </span>
                        </div>
                        <div className="text-right">
                            <p className="text-sm text-gray-500">Fecha de Solicitud:</p>
                            <p className="font-medium text-gray-700">{new Date(solicitud.fechaSolicitud).toLocaleDateString()}</p>
                        </div>
                    </div>
                    <div className="space-y-2">
                        <h3 className="text-lg font-semibold text-[#5E5E5E]">Tu Mensaje al Refugio:</h3>
                        <div className="bg-[#FFF8F0] p-4 rounded-lg border border-gray-200 min-h-[50px]">
                            <p className="text-gray-700 italic">
                                {solicitud.mensajeAdoptante || "No se adjuntó un mensaje específico."}
                            </p>
                        </div>
                    </div>
                    <div className="space-y-2">
                        <h3 className="text-lg font-semibold text-[#5E5E5E]">Respuesta del Refugio:</h3>
                        <div className="bg-[#AEEAFD] p-4 rounded-lg border border-gray-200 min-h-[50px]">
                            <p className="text-[#2B6777] font-medium">
                                {solicitud.mensajeAlAdoptante || "El refugio aún no ha respondido a tu solicitud."}
                            </p>
                        </div>
                    </div>

                </div>
                <div className="p-4 bg-gray-50 border-t flex justify-end">
                    <button
                        onClick={onClose}
                        className="px-6 py-2 text-base font-semibold text-white bg-[#FFB6A3] rounded-lg hover:bg-opacity-80 transition-colors shadow-md"
                    >
                        Cerrar
                    </button>
                </div>
            </div>
        </div>
    );
};

const SolicitudCard = ({ solicitud, onOpenModal }) => {
    const getStateStyle = (estado) => {
        switch (estado) {
            case "Aprobada": return "text-white bg-[#2B6777]";
            case "Rechazada": return "text-white bg-[#FFB6A3]";
            case "En Proceso": return "text-[#2B6777] bg-[#AEEAFD]";
            case "Pendiente":
            default: return "text-gray-800 bg-gray-200";
        }
    };

    const imageUrl = solicitud.animal.fotos?.[0]
        ? `${BACKEND_BASE_URL}${solicitud.animal.fotos[0]}`
        : `https://placehold.co/300x230/E0E0E0/2B6777?text=Mascota`;

    const age = solicitud.animal.raza || 'N/A';
    const gender = solicitud.animal.genero || 'N/A';
    const species = solicitud.animal.especie || 'N/A';

    return (
        <div className="bg-white rounded-xl shadow-lg transition-all duration-300 overflow-hidden border border-gray-100">

            {/* Imagen con badge de estado */}
            <div className="relative">
                <img
                    src={imageUrl}
                    alt={`Foto de ${solicitud.animal.nombre}`}
                    className="w-full h-64 object-cover"
                    onError={(e) => {
                        e.target.onerror = null;
                        e.target.src = `https://placehold.co/300x230/E0E0E0/2B6777?text=${solicitud.animal.nombre}`;
                    }}
                />
                <span className={`absolute top-4 right-4 px-3 py-1 text-sm font-semibold rounded-full ${getStateStyle(solicitud.estadoSolicitud.nombre)} shadow-md`}>
                    {solicitud.estadoSolicitud.nombre}
                </span>
            </div>
            <div className="p-5 space-y-3">
                <h3 className="text-3xl font-bold text-[#2B6777]">{solicitud.animal.nombre}</h3>
                <p className="text-gray-600 text-base">
                    {solicitud.animal.refugioNombre} ({solicitud.animal.refugioCiudad})
                </p>

                <div className="flex flex-wrap gap-x-4 gap-y-2 text-sm text-[#5E5E5E]">
                    <span className="flex items-center">
                        <Heart className="w-4 h-4 mr-1 text-[#2B6777]" />
                        {species}
                    </span>
                    <span className="flex items-center">
                        <Calendar className="w-4 h-4 mr-1 text-[#FFB6A3]" />
                        {age}
                    </span>
                    <span className="flex items-center">
                        <User className="w-4 h-4 mr-1 text-[#2B6777]" />
                        {gender}
                    </span>
                </div>
                <button
                    onClick={() => onOpenModal(solicitud)}
                    className="w-full mt-4 py-3 text-base font-semibold text-white bg-[#2B6777] rounded-lg hover:bg-[#1f4a56] transition-colors shadow-md"
                >
                    Ver Detalles de Solicitud
                </button>
            </div>
        </div>
    );
};

const MisSolicitudes = () => {
    const { token } = useAuth();
    const [solicitudes, setSolicitudes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();


    const [searchTerm, setSearchTerm] = useState('');
    const [filterState, setFilterState] = useState('Todas');
    const [availableStates, setAvailableStates] = useState(['Todas']);

    // ESTADOS DEL MODAL
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedSolicitud, setSelectedSolicitud] = useState(null);

    // Función para abrir el modal
    const handleOpenModal = (solicitud) => {
        setSelectedSolicitud(solicitud);
        setIsModalOpen(true);
    };

    // Función para cerrar el modal
    const handleCloseModal = () => {
        setIsModalOpen(false);
        setSelectedSolicitud(null);
    };

    // Función para simular navegación
    const handleNavigate = (path) => {
        navigate(path);
    };


    // --- Lógica de Carga de Datos REALES ---
    useEffect(() => {
        const fetchSolicitudes = async () => {
            try {
                if (!token) {
                    setLoading(false);
                    return;
                }
                const data = await getMisSolicitudes(token);
                setSolicitudes(data);

                const states = new Set(data.map(s => s.estadoSolicitud.nombre));
                setAvailableStates(['Todas', ...Array.from(states)]);

            } catch (err) {
                console.error("Error al cargar solicitudes:", err);
                setError("Error al cargar solicitudes. Verifica tu conexión al backend.");
            } finally {
                setLoading(false);
            }
        };
        fetchSolicitudes();
    }, [token]);

    const filteredSolicitudes = useMemo(() => {
        let result = solicitudes;
        const lowerCaseSearch = searchTerm.toLowerCase();

        if (filterState !== 'Todas') {
            result = result.filter(sol => sol.estadoSolicitud.nombre === filterState);
        }

        if (searchTerm) {
            result = result.filter(sol =>
                sol.animal.nombre.toLowerCase().includes(lowerCaseSearch) ||
                sol.animal.raza.toLowerCase().includes(lowerCaseSearch) ||
                sol.animal.refugioNombre.toLowerCase().includes(lowerCaseSearch)
            );
        }

        return result.sort((a, b) => new Date(b.fechaSolicitud) - new Date(a.fechaSolicitud));

    }, [solicitudes, searchTerm, filterState]);
    if (loading) return (
        <div className="w-full min-h-screen flex items-center justify-center bg-gradient-to-b from-[#AEEAFD] to-[#FFF8F0]">
            <Loader className="w-10 h-10 animate-spin text-[#2B6777]" />
            <p className="text-xl text-[#2B6777] font-semibold ml-4">Cargando tus solicitudes...</p>
        </div>
    );

    if (error) return (
        <div className="w-full min-h-screen flex items-center justify-center bg-gradient-to-b from-[#AEEAFD] to-[#FFF8F0]">
            <p className="text-xl text-red-600 font-semibold">{error}</p>
        </div>
    );

    return (
        <>
            <div className="w-full min-h-screen bg-gradient-to-b from-[#AEEAFD] to-[#FFF8F0] flex flex-col items-center pb-20 font-sans">

                {/* ---------- TÍTULO Y FILTROS ---------- */}
                <div className="w-full max-w-7xl px-4 pt-10 pb-8">

                    <h1 className="text-4xl font-extrabold text-[#2B6777] tracking-tighter mb-6">
                        Tus Solicitudes
                    </h1>

                    <div className="space-y-4 md:space-y-0 md:flex md:justify-between md:items-center">

                        <div className="relative w-full md:w-1/3">
                            <input
                                type="text"
                                placeholder="Buscar por nombre o refugio..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="w-full py-3 pl-12 pr-4 text-gray-700 border border-gray-300 rounded-xl shadow-inner focus:ring-2 focus:ring-[#AEEAFD] focus:border-[#2B6777] transition"
                            />
                            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                        </div>
                        <div className="flex items-center space-x-3">
                            <label htmlFor="filter-state" className="text-gray-700 font-medium whitespace-nowrap">
                                Estado:
                            </label>
                            <select
                                id="filter-state"
                                value={filterState}
                                onChange={(e) => setFilterState(e.target.value)}
                                className="py-3 px-4 border border-gray-300 rounded-xl shadow-inner bg-white focus:ring-2 focus:ring-[#AEEAFD] focus:border-[#2B6777] transition"
                            >
                                {availableStates.map(state => (
                                    <option key={state} value={state}>{state}</option>
                                ))}
                            </select>
                        </div>
                    </div>
                </div>

                {/* ---------- RESULTADOS (GRID) ---------- */}
                <div className="w-full max-w-7xl px-4">
                    {filteredSolicitudes.length > 0 ? (
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3 gap-8">
                            {filteredSolicitudes.map(solicitud => (
                                <SolicitudCard
                                    key={solicitud.id}
                                    solicitud={solicitud}
                                    onOpenModal={handleOpenModal}
                                />
                            ))}
                        </div>
                    ) : (
                        <div className="text-center py-20 bg-white/70 backdrop-blur rounded-3xl shadow-xl border border-gray-200">
                            <h2 className="text-2xl font-bold text-[#2B6777] mb-3">
                                No hay solicitudes que coincidan con tu búsqueda.
                            </h2>
                            <p className="text-gray-600 max-w-md mx-auto">
                                Ajusta los filtros o inicia una nueva búsqueda.
                            </p>
                        </div>
                    )}
                </div>

                {/* ---------- "BUSCAR MASCOTAS" ---------- */}
                <div className="mt-20 pt-16 pb-12 px-4 w-full max-w-7xl border-2 border-gray-400 rounded-3xl bg-[#FFF8F0]">
                    <div className="text-center max-w-md mx-auto">
                        <div className="flex justify-center mb-4">
                            <Compass className="w-10 h-10 text-gray-600 stroke-[1.5]" />
                        </div>
                        <h2 className="text-2xl font-bold text-gray-800 mb-3">
                            ¿Buscas a tu próximo amigo?
                        </h2>
                        <p className="text-gray-600 mb-6">
                            Cuando inicies una solicitud de adopción, aparecerá aquí para que puedas seguir su progreso. ¡Explora y encuentra a tu compañero ideal!
                        </p>
                        <button
                            onClick={() => handleNavigate('/dashboard/adoptante/mascotas')}
                            className="inline-flex items-center px-8 py-3 bg-[#2B6777] text-white font-semibold rounded-lg shadow-lg hover:bg-[#1f4a56] transition-colors"
                        >
                            Buscar Mascotas
                        </button>
                    </div>
                </div>

            </div>
            {isModalOpen && <SolicitudDetailModal solicitud={selectedSolicitud} onClose={handleCloseModal} />}
        </>
    );
};

export default MisSolicitudes;