import React, { useEffect, useState } from 'react';
import { fetchAdoptantes } from '../../api/fetchAdoptantes';
import { PencilIcon, TrashIcon } from '@heroicons/react/24/outline';  // Iconos de Heroicons
import client from '../../api/client';  // Para hacer solicitudes API

const AdoptantesList = () => {
    const [adoptantes, setAdoptantes] = useState([]);
    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const loadAdoptantes = async () => {
            try {
                setLoading(true);
                const data = await fetchAdoptantes(page);
                setAdoptantes(data.results);
            } catch (err) {
                setError('Error al cargar los adoptantes');
            } finally {
                setLoading(false);
            }
        };
        loadAdoptantes();
    }, [page]);

    const handlePageChange = (newPage) => {
        setPage(newPage);
    };

    const handleDelete = async (id) => {
        try {
            const confirmDelete = window.confirm('¿Estás seguro de eliminar este adoptante?');
            if (confirmDelete) {
                await client.delete(`/adoptantes/${id}/`);
                alert('Adoptante eliminado');
                setAdoptantes(adoptantes.filter(adoptante => adoptante.id !== id));
            }
        } catch (error) {
            setError('Error al eliminar el adoptante');
        }
    };

    const handleEdit = (id) => {
        // Redirige a la página de edición, pasando el ID del adoptante
        window.location.href = `/adoptantes/editar/${id}`;
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-lg max-w-6xl mx-auto space-y-8">
            <h1 className="text-3xl font-semibold text-gray-700 mb-6">Lista de Adoptantes</h1>

            {/* Loading and Error State */}
            {loading && <p className="text-gray-500">Cargando...</p>}
            {error && <p className="text-red-500">{error}</p>}

            {/* Tabla */}
            <div className="overflow-x-auto">
                <table className="min-w-full table-auto">
                    <thead>
                        <tr className="text-left bg-gray-100 border-b">
                            <th className="px-6 py-3 text-sm font-medium text-gray-500">ID</th>
                            <th className="px-6 py-3 text-sm font-medium text-gray-500">Nombre</th>
                            <th className="px-6 py-3 text-sm font-medium text-gray-500">Email</th>
                            <th className="px-6 py-3 text-sm font-medium text-gray-500">Teléfono</th>
                            <th className="px-6 py-3 text-sm font-medium text-gray-500">Ciudad</th>
                            <th className="px-6 py-3 text-sm font-medium text-gray-500">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {adoptantes.map((adoptante) => (
                            <tr key={adoptante.id} className="border-b hover:bg-gray-50">
                                <td className="px-6 py-4 text-sm font-medium text-gray-900">{adoptante.id}</td>
                                <td className="px-6 py-4 text-sm text-gray-700">{adoptante.nombre} {adoptante.apellido_paterno}</td>
                                <td className="px-6 py-4 text-sm text-gray-600">{adoptante.email}</td>
                                <td className="px-6 py-4 text-sm text-gray-600">{adoptante.telefono}</td>
                                <td className="px-6 py-4 text-sm text-gray-600">{adoptante.ciudad}</td>
                                <td className="px-6 py-4 text-sm font-medium">
                                    <button
                                        onClick={() => handleEdit(adoptante.id)}
                                        className="text-blue-600 hover:text-blue-800 mr-4"
                                    >
                                        <PencilIcon className="w-5 h-5 inline-block" />
                                    </button>
                                    <button
                                        onClick={() => handleDelete(adoptante.id)}
                                        className="text-red-600 hover:text-red-800"
                                    >
                                        <TrashIcon className="w-5 h-5 inline-block" />
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Paginación */}
            <div className="flex justify-between items-center mt-6">
                <button
                    onClick={() => handlePageChange(page - 1)}
                    disabled={page === 1}
                    className="px-4 py-2 text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-50"
                >
                    Anterior
                </button>
                <span className="text-gray-700">Página {page}</span>
                <button
                    onClick={() => handlePageChange(page + 1)}
                    className="px-4 py-2 text-white bg-indigo-600 rounded-lg hover:bg-indigo-700"
                >
                    Siguiente
                </button>
            </div>
        </div>
    );
};

export default AdoptantesList;
