import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { fetchAdoptantes } from '../../api/fetchAdoptantes';
import client from '../../api/client';
import { PencilIcon, TrashIcon, PlusIcon, ChevronLeftIcon, ChevronRightIcon } from '@heroicons/react/24/outline';

const AdoptantesList = () => {
    const [adoptantes, setAdoptantes] = useState([]);
    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    // ESTADOS PARA PAGINACIÓN REAL
    const [totalCount, setTotalCount] = useState(0);
    const [hasNext, setHasNext] = useState(false);
    const [hasPrev, setHasPrev] = useState(false);

    useEffect(() => {
        loadAdoptantes(page);
    }, [page]);

    const loadAdoptantes = async (currentPage) => {
        setLoading(true);
        setError(null);
        try {
            const data = await fetchAdoptantes(currentPage);
            
            // Verificamos si la respuesta es paginada (DRF standard)
            if (data.results) {
                setAdoptantes(data.results);
                setTotalCount(data.count);
                
                // LA CLAVE: Usamos las URLs que nos da el backend para saber si hay más
                setHasNext(!!data.next); 
                setHasPrev(!!data.previous);
            } else {
                // Fallback si no hay paginación
                setAdoptantes(Array.isArray(data) ? data : []);
                setTotalCount(data.length || 0);
                setHasNext(false);
                setHasPrev(false);
            }
        } catch (err) {
            console.error(err);
            // Si la página no existe (404), volvemos a la 1 automáticamente
            if (currentPage > 1) {
                setPage(1);
            } else {
                setError('No se pudieron cargar los adoptantes.');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('¿Estás seguro de eliminar este adoptante?')) {
            try {
                await client.delete(`/adoptantes/${id}/`);
                // Recargar la misma página para actualizar la lista
                loadAdoptantes(page);
            } catch (error) {
                alert('Error al eliminar el adoptante. Puede tener datos asociados.');
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
                    <h1 className="text-2xl font-bold text-gray-800">Directorio de Adoptantes</h1>
                    <p className="text-sm text-gray-500">Total registrados: {totalCount}</p>
                </div>
                <Link
                    to="/adoptantes/create"
                    className="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition shadow flex items-center gap-2"
                >
                    <PlusIcon className="w-5 h-5" /> Nuevo Adoptante
                </Link>
            </div>

            {error && <div className="bg-red-100 text-red-700 p-4 rounded mb-4">{error}</div>}

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden flex flex-col min-h-[400px]">
                {loading ? (
                    <div className="flex-1 flex items-center justify-center text-gray-500">
                        Cargando registros...
                    </div>
                ) : (
                    <>
                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead className="bg-gray-100 border-b border-gray-200">
                                    <tr>
                                        <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase">Nombre</th>
                                        <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase">Contacto</th>
                                        <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase">Ubicación</th>
                                        <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase text-right">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {adoptantes.map((adoptante) => (
                                        <tr key={adoptante.id} className="hover:bg-gray-50 transition-colors">
                                            <td className="px-6 py-4">
                                                <div className="font-medium text-gray-900">
                                                    {adoptante.nombre} {adoptante.apellido_paterno}
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 text-sm text-gray-600">
                                                <div className="flex flex-col">
                                                    <span>{adoptante.email}</span>
                                                    <span className="text-xs text-gray-400">{adoptante.telefono}</span>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 text-sm text-gray-600">
                                                {adoptante.ciudad || "No especificada"}
                                            </td>
                                            <td className="px-6 py-4 text-right space-x-3">
                                                <Link 
                                                    to={`/adoptantes/editar/${adoptante.id}`}
                                                    className="text-blue-600 hover:text-blue-800 inline-flex items-center"
                                                    title="Editar"
                                                >
                                                    <PencilIcon className="w-4 h-4" />
                                                </Link>
                                                <button
                                                    onClick={() => handleDelete(adoptante.id)}
                                                    className="text-red-600 hover:text-red-800 inline-flex items-center"
                                                    title="Eliminar"
                                                >
                                                    <TrashIcon className="w-4 h-4" />
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                    {adoptantes.length === 0 && (
                                        <tr>
                                            <td colSpan="4" className="p-8 text-center text-gray-400">
                                                No hay adoptantes registrados.
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>

                        {/* CONTROLES DE PAGINACIÓN CORREGIDOS */}
                        <div className="border-t border-gray-200 px-6 py-4 flex items-center justify-between bg-gray-50 mt-auto">
                            <span className="text-sm text-gray-700">
                                Página actual: <span className="font-semibold">{page}</span>
                            </span>
                            <div className="flex gap-2">
                                <button
                                    onClick={handlePrevious}
                                    disabled={!hasPrev} // Solo activo si el backend dice que hay "previous"
                                    className="px-3 py-1 border rounded-md bg-white text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed flex items-center transition-all"
                                >
                                    <ChevronLeftIcon className="w-4 h-4 mr-1" /> Anterior
                                </button>
                                <button
                                    onClick={handleNext}
                                    disabled={!hasNext} // Solo activo si el backend dice que hay "next"
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

export default AdoptantesList;