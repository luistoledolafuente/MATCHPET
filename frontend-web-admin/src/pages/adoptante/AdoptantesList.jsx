import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom'; // Asegúrate de importar Link
import { fetchAdoptantes } from '../../api/fetchAdoptantes';
import { PencilIcon, TrashIcon, PlusIcon } from '@heroicons/react/24/outline'; // Asegúrate de tener heroicons
import client from '../../api/client'; // Para hacer solicitudes API

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
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-semibold text-gray-700">Lista de Adoptantes</h1>
                <Link
                    to="/adoptantes/create"
                    className="bg-indigo-600 text-white py-3 px-6 rounded-lg hover:bg-indigo-700 flex items-center transition duration-300"
                >
                    <PlusIcon className="w-5 h-5 mr-2" /> Nuevo Adoptante
                </Link>
            </div>

            {/* Loading and Error State */}
            {loading && <p className="text-gray-500">Cargando...</p>}
            {error && <p className="text-red-500">{error}</p>}

            {/* Tabla */}
            <div className="overflow-x-auto">
                <table className="min-w-full table-auto bg-white rounded-xl shadow-sm border border-gray-200">
                    <thead>
                        <tr className="text-left bg-indigo-100 text-gray-700">
                            <th className="px-6 py-3 text-sm font-medium">ID</th>
                            <th className="px-6 py-3 text-sm font-medium">Nombre</th>
                            <th className="px-6 py-3 text-sm font-medium">Email</th>
                            <th className="px-6 py-3 text-sm font-medium">Teléfono</th>
                            <th className="px-6 py-3 text-sm font-medium">Ciudad</th>
                            <th className="px-6 py-3 text-sm font-medium text-right">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {adoptantes.map((adoptante) => (
                            <tr key={adoptante.id} className="hover:bg-indigo-50 transition duration-300">
                                <td className="px-6 py-4 text-sm text-gray-500">#{adoptante.id}</td>
                                <td className="px-6 py-4 text-sm text-gray-700">{adoptante.nombre} {adoptante.apellido_paterno}</td>
                                <td className="px-6 py-4 text-sm text-gray-600">{adoptante.email}</td>
                                <td className="px-6 py-4 text-sm text-gray-600">{adoptante.telefono}</td>
                                <td className="px-6 py-4 text-sm text-gray-600">{adoptante.ciudad}</td>
                                <td className="px-6 py-4 text-right space-x-2">
                                    <button
                                        onClick={() => handleEdit(adoptante.id)}
                                        className="text-blue-600 hover:text-blue-800 inline-flex items-center"
                                    >
                                        <PencilIcon className="w-5 h-5 inline-block" />
                                    </button>
                                    <button
                                        onClick={() => handleDelete(adoptante.id)}
                                        className="text-red-600 hover:text-red-800 inline-flex items-center"
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
                    className="px-4 py-2 text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition duration-300"
                >
                    Anterior
                </button>
                <span className="text-gray-700">Página {page}</span>
                <button
                    onClick={() => handlePageChange(page + 1)}
                    className="px-4 py-2 text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition duration-300"
                >
                    Siguiente
                </button>
            </div>
        </div>
    );
};

export default AdoptantesList;
