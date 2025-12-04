import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { fetchAnimales, deleteAnimal } from '../../api/fetchAnimales';
import { ChevronLeftIcon, ChevronRightIcon } from '@heroicons/react/24/outline';

const AnimalesList = () => {
    const [animales, setAnimales] = useState([]);
    const [loading, setLoading] = useState(true);
    
    // Estados para paginación
    const [page, setPage] = useState(1);
    const [totalCount, setTotalCount] = useState(0);
    
    // NUEVOS ESTADOS: Controlan si realmente hay otra página
    const [hasNext, setHasNext] = useState(false);
    const [hasPrev, setHasPrev] = useState(false);

    useEffect(() => {
        loadData(page);
    }, [page]);

    const loadData = async (currentPage) => {
        setLoading(true);
        try {
            const data = await fetchAnimales(currentPage);
            
            // DRF devuelve: { count: 10, next: "url...", previous: "url...", results: [...] }
            if (data.results) {
                setAnimales(data.results);
                setTotalCount(data.count);
                
                // AQUÍ ESTÁ LA SOLUCIÓN:
                // Solo habilitamos "Siguiente" si el backend nos dice explícitamente que hay una URL "next"
                setHasNext(!!data.next); 
                setHasPrev(!!data.previous);
            } else {
                // Fallback por si la paginación se desactiva en el backend
                setAnimales(data);
                setTotalCount(data.length);
                setHasNext(false);
                setHasPrev(false);
            }
        } catch (error) {
            console.error(error);
            // Si da error 404 en página > 1, volvemos a la 1 automáticamente
            if (currentPage > 1) {
                setPage(1);
            } else {
                alert("Error cargando la lista");
            }
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("¿Eliminar este animal permanentemente?")) {
            await deleteAnimal(id);
            loadData(page);
        }
    };

    const handlePrevious = () => {
        if (hasPrev) setPage(prev => prev - 1);
    };

    const handleNext = () => {
        if (hasNext) setPage(prev => prev + 1);
    };

    if (loading && page === 1) return <div className="p-8 text-center text-gray-500">Cargando mascotas...</div>;

    return (
        <div className="p-6 bg-gray-50 min-h-screen">
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-gray-800">Gestión de Mascotas</h1>
                    <p className="text-sm text-gray-500">Total registros: {totalCount}</p>
                </div>
                <Link
                    to="/animales/create"
                    className="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition shadow flex items-center gap-2"
                >
                    + Registrar Mascota
                </Link>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden flex flex-col min-h-[400px]">
                {loading ? (
                    <div className="flex-1 flex items-center justify-center text-gray-500 opacity-50">
                        Actualizando...
                    </div>
                ) : (
                    <>
                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead className="bg-gray-100 border-b">
                                    <tr>
                                        <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase">Nombre</th>
                                        <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase">Raza / Especie</th>
                                        <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase">Refugio</th>
                                        <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase">Estado</th>
                                        <th className="px-6 py-3 text-xs font-semibold text-gray-500 uppercase text-right">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {animales.map((animal) => (
                                        <tr key={animal.id} className="hover:bg-gray-50 transition-colors">
                                            <td className="px-6 py-4 font-medium text-gray-900">{animal.nombre}</td>
                                            <td className="px-6 py-4 text-gray-600">
                                                {animal.nombre_raza || "Desconocida"}
                                                <span className="text-xs text-gray-400 block">{animal.nombre_especie}</span>
                                            </td>
                                            <td className="px-6 py-4 text-indigo-600 font-medium">
                                                {animal.nombre_refugio || "Sin asignar"}
                                            </td>
                                            <td className="px-6 py-4">
                                                <span className={`px-2 py-1 text-xs rounded-full font-medium ${
                                                    animal.nombre_estado === 'Adoptado' ? 'bg-green-100 text-green-700' : 
                                                    animal.nombre_estado === 'En proceso' ? 'bg-yellow-100 text-yellow-700' :
                                                    'bg-blue-100 text-blue-700'
                                                }`}>
                                                    {animal.nombre_estado || "Disponible"}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 text-right space-x-2">
                                                <Link to={`/animales/editar/${animal.id}`} className="text-blue-600 hover:text-blue-800 text-sm font-medium">Editar</Link>
                                                <button onClick={() => handleDelete(animal.id)} className="text-red-600 hover:text-red-800 text-sm font-medium">Eliminar</button>
                                            </td>
                                        </tr>
                                    ))}
                                    {animales.length === 0 && (
                                        <tr><td colSpan="5" className="p-8 text-center text-gray-400">No hay animales para mostrar.</td></tr>
                                    )}
                                </tbody>
                            </table>
                        </div>

                        {/* --- CONTROLES DE PAGINACIÓN CORREGIDOS --- */}
                        <div className="border-t border-gray-200 px-6 py-4 flex items-center justify-between bg-gray-50 mt-auto">
                            <span className="text-sm text-gray-700">
                                Página actual: <span className="font-semibold">{page}</span>
                            </span>
                            <div className="flex gap-2">
                                <button
                                    onClick={handlePrevious}
                                    disabled={!hasPrev} // Deshabilitado si el backend dice que no hay anterior
                                    className="px-3 py-1 border rounded-md bg-white text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed flex items-center transition-all"
                                >
                                    <ChevronLeftIcon className="w-4 h-4 mr-1" /> Anterior
                                </button>
                                <button
                                    onClick={handleNext}
                                    disabled={!hasNext} // Deshabilitado si el backend dice que no hay siguiente
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

export default AnimalesList;