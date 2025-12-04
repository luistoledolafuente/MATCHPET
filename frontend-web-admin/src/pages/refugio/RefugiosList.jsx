import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { fetchRefugios, deleteRefugio } from '../../api/fetchRefugios';
import { PencilSquareIcon, TrashIcon, PlusIcon, ChevronLeftIcon, ChevronRightIcon } from '@heroicons/react/24/outline';

const RefugiosList = () => {
    const [refugios, setRefugios] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // --- ESTADOS DE PAGINACIÓN ---
    const [page, setPage] = useState(1);
    const [totalCount, setTotalCount] = useState(0);
    const [hasNext, setHasNext] = useState(false);
    const [hasPrev, setHasPrev] = useState(false);

    // Cargar datos cuando cambia la página
    useEffect(() => {
        cargarDatos(page);
    }, [page]);

    const cargarDatos = async (currentPage) => {
        setLoading(true);
        setError(null);
        try {
            const data = await fetchRefugios(currentPage);
            
            // Verificamos si es una respuesta paginada de Django Rest Framework
            if (data.results) {
                setRefugios(data.results);
                setTotalCount(data.count);
                
                // Usamos las banderas del backend para habilitar/deshabilitar botones
                setHasNext(!!data.next); 
                setHasPrev(!!data.previous);
            } else {
                // Caso fallback (si el backend devuelve array directo)
                setRefugios(Array.isArray(data) ? data : []);
                setTotalCount(data.length || 0);
                setHasNext(false);
                setHasPrev(false);
            }
        } catch (err) {
            console.error(err);
            // Autocorrección: Si la página no existe (404), volver a la 1
            if (currentPage > 1) {
                setPage(1);
            } else {
                setError('Error al cargar refugios');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('¿Deseas eliminar este refugio? Esta acción no se puede deshacer.')) {
            try {
                await deleteRefugio(id);
                // Recargar la página actual para reflejar cambios
                cargarDatos(page);
            } catch (err) {
                alert("Error al eliminar. Puede que tenga animales asociados.");
            }
        }
    };

    const handlePrevious = () => {
        if (hasPrev) setPage(prev => prev - 1);
    };

    const handleNext = () => {
        if (hasNext) setPage(prev => prev + 1);
    };

    return (
        <div className="p-6 bg-gray-50 min-h-screen">
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-gray-800">Directorio de Refugios</h1>
                    <p className="text-sm text-gray-500">Total registrados: {totalCount}</p>
                </div>
                <Link
                    to="/refugios/create"
                    className="bg-indigo-600 text-white py-2 px-4 rounded-lg hover:bg-indigo-700 flex items-center transition duration-300 shadow-sm"
                >
                    <PlusIcon className="w-5 h-5 mr-2" /> Nuevo Refugio
                </Link>
            </div>

            {error && <div className="bg-red-100 text-red-700 p-4 rounded mb-4">{error}</div>}

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden flex flex-col min-h-[400px]">
                {loading ? (
                    <div className="flex-1 flex items-center justify-center text-gray-500">
                        Cargando refugios...
                    </div>
                ) : (
                    <>
                        <div className="overflow-x-auto">
                            <table className="w-full text-left border-collapse">
                                <thead className="bg-gray-100 border-b border-gray-200">
                                    <tr>
                                        <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">ID</th>
                                        <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Organización</th>
                                        <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Contacto</th>
                                        <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Ubicación</th>
                                        <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider text-right">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {refugios.map((refugio) => (
                                        <tr key={refugio.id} className="hover:bg-gray-50 transition duration-300">
                                            <td className="px-6 py-4 text-sm text-gray-500">#{refugio.id}</td>
                                            <td className="px-6 py-4">
                                                <div className="flex flex-col">
                                                    <span className="font-semibold text-gray-900">{refugio.nombre}</span>
                                                    <a href={`mailto:${refugio.email}`} className="text-sm text-indigo-600 hover:underline">{refugio.email}</a>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 text-sm text-gray-600">
                                                <div className="flex flex-col">
                                                    <span>{refugio.persona_contacto}</span>
                                                    <span className="text-xs text-gray-400">{refugio.telefono}</span>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 text-sm text-gray-600">
                                                <div className="flex items-center gap-1">
                                                    <span>{refugio.ciudad}, {refugio.pais}</span>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 text-right space-x-3">
                                                <Link 
                                                    to={`/refugios/editar/${refugio.id}`}
                                                    className="inline-flex items-center text-blue-600 hover:text-blue-800 transition"
                                                    title="Editar"
                                                >
                                                    <PencilSquareIcon className="w-5 h-5" />
                                                </Link>
                                                <button 
                                                    onClick={() => handleDelete(refugio.id)}
                                                    className="inline-flex items-center text-red-600 hover:text-red-800 transition"
                                                    title="Eliminar"
                                                >
                                                    <TrashIcon className="w-5 h-5" />
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                    {refugios.length === 0 && (
                                        <tr>
                                            <td colSpan="5" className="px-6 py-8 text-center text-gray-400">
                                                No hay refugios registrados en esta página.
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>

                        {/* --- CONTROLES DE PAGINACIÓN --- */}
                        <div className="border-t border-gray-200 px-6 py-4 flex items-center justify-between bg-gray-50 mt-auto">
                            <span className="text-sm text-gray-700">
                                Página actual: <span className="font-semibold">{page}</span>
                            </span>
                            <div className="flex gap-2">
                                <button
                                    onClick={handlePrevious}
                                    disabled={!hasPrev}
                                    className="px-3 py-1 border rounded-md bg-white text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed flex items-center transition-all"
                                >
                                    <ChevronLeftIcon className="w-4 h-4 mr-1" /> Anterior
                                </button>
                                <button
                                    onClick={handleNext}
                                    disabled={!hasNext}
                                    className="px-3 py-1 border rounded-md bg-white text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed flex items-center transition-all"
                                >
                                    Siguiente <ChevronRightIcon className="w-4 h-4 ml-1" />
                                </button>
                            </div>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default RefugiosList;