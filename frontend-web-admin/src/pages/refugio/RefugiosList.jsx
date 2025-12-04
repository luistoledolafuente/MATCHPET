import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { fetchRefugios, deleteRefugio } from '../../api/fetchRefugios';
import { PencilSquareIcon, TrashIcon, PlusIcon } from '@heroicons/react/24/outline';

const RefugiosList = () => {
    const [refugios, setRefugios] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const cargarDatos = async () => {
        try {
            const data = await fetchRefugios(); // Función para traer refugios
            setRefugios(data.results || data); // Si la API devuelve una lista paginada
            setLoading(false);
        } catch (err) {
            setError('Error al cargar refugios');
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (confirm('¿Deseas eliminar este refugio?')) {
            await deleteRefugio(id);  // Función para eliminar refugio
            cargarDatos(); // Recarga la lista después de eliminar
        }
    };

    useEffect(() => {
        cargarDatos();
    }, []);

    if (loading) return <div className="p-8 text-center">Cargando refugios...</div>;

    return (
        <div className="p-6 bg-gray-50 min-h-screen">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-semibold text-gray-700">Directorio de Refugios</h1>
                <Link
                    to="/refugios/create"
                    className="bg-indigo-600 text-white py-2 px-4 rounded-lg hover:bg-indigo-700 flex items-center transition duration-300"
                >
                    <PlusIcon className="w-5 h-5 mr-2" /> Nuevo Refugio
                </Link>
            </div>

            {error && <div className="text-red-500">{error}</div>}

            <div className="bg-white rounded-lg shadow-md overflow-hidden">
                <table className="w-full text-left border-collapse">
                    <thead className="bg-indigo-100 text-gray-700">
                        <tr>
                            <th className="px-6 py-4 text-xs font-semibold uppercase tracking-wider">ID</th>
                            <th className="px-6 py-4 text-xs font-semibold uppercase tracking-wider">Organización</th>
                            <th className="px-6 py-4 text-xs font-semibold uppercase tracking-wider">Ubicación</th>
                            <th className="px-6 py-4 text-xs font-semibold uppercase tracking-wider text-right">Acciones</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {refugios.length > 0 ? (
                            refugios.map((refugio) => (
                                <tr key={refugio.id} className="hover:bg-indigo-50 transition duration-300">
                                    <td className="px-6 py-4 text-sm text-gray-500">#{refugio.id}</td>
                                    <td className="px-6 py-4">
                                        <div className="flex flex-col">
                                            <span className="font-semibold text-gray-800">{refugio.nombre}</span>
                                            <a href={`mailto:${refugio.email}`} className="text-sm text-indigo-500 hover:underline">{refugio.email}</a>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 text-sm text-gray-600">
                                        <div className="flex items-center gap-1">
                                            <span>{refugio.ciudad}, {refugio.pais}</span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 text-right space-x-2">
                                        <Link 
                                            to={`/refugios/editar/${refugio.id}`}
                                            className="inline-flex items-center px-2 py-1 text-sm font-medium text-blue-600 bg-blue-50 rounded-md hover:bg-blue-100 transition"
                                        >
                                            <PencilSquareIcon className="w-5 h-5 inline" />
                                        </Link>
                                        <button 
                                            onClick={() => handleDelete(refugio.id)}
                                            className="inline-flex items-center px-2 py-1 text-sm font-medium text-red-600 bg-red-50 rounded-md hover:bg-red-100 transition"
                                        >
                                            <TrashIcon className="w-5 h-5 inline" />
                                        </button>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="4" className="px-6 py-8 text-center text-gray-500">
                                    No hay refugios registrados aún.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default RefugiosList;
