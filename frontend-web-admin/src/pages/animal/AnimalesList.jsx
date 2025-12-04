import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { fetchAnimales, deleteAnimal } from '../../api/fetchAnimales';

const AnimalesList = () => {
    const [animales, setAnimales] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const data = await fetchAnimales();
            // Ajusta si tu paginación devuelve { results: [...] } o array directo
            setAnimales(data.results || data); 
        } catch (error) {
            alert("Error cargando la lista");
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("¿Eliminar este animal permanentemente?")) {
            await deleteAnimal(id);
            loadData(); // Recargar
        }
    };

    if (loading) return <div className="p-8 text-center">Cargando mascotas...</div>;

    return (
        <div className="p-6 bg-gray-50 min-h-screen">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-800">Gestión de Mascotas</h1>
                <Link
                    to="/animales/create"
                    className="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition shadow"
                >
                    + Registrar Mascota
                </Link>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
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
                            <tr key={animal.id} className="hover:bg-gray-50">
                                <td className="px-6 py-4 font-medium text-gray-900">{animal.nombre}</td>
                                <td className="px-6 py-4 text-gray-600">
                                    {animal.nombre_raza || "Desconocida"}
                                    <span className="text-xs text-gray-400 block">{animal.nombre_especie}</span>
                                </td>
                                <td className="px-6 py-4 text-indigo-600 font-medium">
                                    {animal.nombre_refugio || "Sin asignar"}
                                </td>
                                <td className="px-6 py-4">
                                    <span className={`px-2 py-1 text-xs rounded-full ${
                                        animal.nombre_estado === 'Adoptado' ? 'bg-green-100 text-green-700' : 
                                        animal.nombre_estado === 'En proceso' ? 'bg-yellow-100 text-yellow-700' :
                                        'bg-blue-100 text-blue-700'
                                    }`}>
                                        {animal.nombre_estado || "Disponible"}
                                    </span>
                                </td>
                                <td className="px-6 py-4 text-right space-x-2">
                                    <Link to={`/animales/editar/${animal.id}`} className="text-blue-600 hover:underline text-sm">Editar</Link>
                                    <button onClick={() => handleDelete(animal.id)} className="text-red-600 hover:underline text-sm">Eliminar</button>
                                </td>
                            </tr>
                        ))}
                        {animales.length === 0 && (
                            <tr><td colSpan="5" className="p-8 text-center text-gray-400">No hay animales registrados.</td></tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AnimalesList;