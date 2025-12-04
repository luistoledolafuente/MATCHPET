import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import client from "../../api/client";

const EditRefugio = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        nombre: '',
        persona_contacto: '',
        email: '',
        telefono: '',
        direccion: '',
        ciudad: '',
        pais: '',
        url_sitio_web: '',
        descripcion: ''
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 1. Cargar datos del refugio actual
    useEffect(() => {
        const fetchRefugio = async () => {
            try {
                const response = await client.get(`/refugios/${id}/`);
                // Filtramos solo los campos que nos interesan para el form
                const data = response.data;
                setFormData({
                    nombre: data.nombre || '',
                    persona_contacto: data.persona_contacto || '',
                    email: data.email || '',
                    telefono: data.telefono || '',
                    direccion: data.direccion || '',
                    ciudad: data.ciudad || '',
                    pais: data.pais || '',
                    url_sitio_web: data.url_sitio_web || '',
                    descripcion: data.descripcion || ''
                });
                setLoading(false);
            } catch (err) {
                console.error("Error cargando refugio:", err);
                setError("No se pudo cargar la información del refugio.");
                setLoading(false);
            }
        };
        fetchRefugio();
    }, [id]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await client.put(`/refugios/${id}/`, formData);
            alert('Refugio actualizado correctamente');
            navigate('/refugios');
        } catch (err) {
            console.error("Error actualizando:", err);
            setError("Error al guardar cambios. Verifica los datos.");
        }
    };

    if (loading) return <div className="p-8 text-center">Cargando datos...</div>;

    return (
        <div className="bg-white p-8 rounded-lg shadow-lg max-w-4xl mx-auto my-8">
            <div className="flex justify-between items-center mb-6 border-b pb-4">
                <h1 className="text-2xl font-bold text-gray-800">Editar Refugio #{id}</h1>
                <button 
                    onClick={() => navigate('/refugios')}
                    className="text-gray-500 hover:text-gray-700"
                >
                    ✕ Cancelar
                </button>
            </div>

            {error && <div className="bg-red-100 text-red-700 p-3 mb-4 rounded">{error}</div>}

            <form onSubmit={handleSubmit} className="space-y-6">
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    {/* Nombre */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Nombre Refugio</label>
                        <input
                            type="text"
                            name="nombre"
                            value={formData.nombre}
                            onChange={handleInputChange}
                            required
                            className="mt-1 p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    {/* Web */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Sitio Web</label>
                        <input
                            type="url"
                            name="url_sitio_web"
                            value={formData.url_sitio_web}
                            onChange={handleInputChange}
                            className="mt-1 p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    {/* Descripción */}
                    <div className="flex flex-col md:col-span-2">
                        <label className="text-sm font-medium text-gray-700">Descripción</label>
                        <textarea
                            name="descripcion"
                            value={formData.descripcion}
                            onChange={handleInputChange}
                            rows="3"
                            className="mt-1 p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    {/* Contacto */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Persona de Contacto</label>
                        <input
                            type="text"
                            name="persona_contacto"
                            value={formData.persona_contacto}
                            onChange={handleInputChange}
                            required
                            className="mt-1 p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Teléfono</label>
                        <input
                            type="text"
                            name="telefono"
                            value={formData.telefono}
                            onChange={handleInputChange}
                            required
                            className="mt-1 p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                    
                    {/* Ubicación */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Ciudad</label>
                        <input
                            type="text"
                            name="ciudad"
                            value={formData.ciudad}
                            onChange={handleInputChange}
                            required
                            className="mt-1 p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">País</label>
                        <input
                            type="text"
                            name="pais"
                            value={formData.pais}
                            onChange={handleInputChange}
                            required
                            className="mt-1 p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                </div>

                <div className="flex justify-end gap-3 mt-8 pt-4 border-t border-gray-100">
                    <button
                        type="button"
                        onClick={() => navigate('/refugios')}
                        className="px-6 py-2 bg-gray-100 text-gray-700 rounded hover:bg-gray-200"
                    >
                        Cancelar
                    </button>
                    <button
                        type="submit"
                        className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 shadow-md"
                    >
                        Guardar Cambios
                    </button>
                </div>
            </form>
        </div>
    );
};

export default EditRefugio;