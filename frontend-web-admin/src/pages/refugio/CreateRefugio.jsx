import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import client from '../../api/client';

const CreateRefugio = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        nombre: '', // Nombre del Refugio
        persona_contacto: '', // Persona de contacto
        email: '', // Email del refugio (también será usuario)
        telefono: '',
        password: '', // Contraseña para el admin
        direccion: '',
        ciudad: '',
        pais: '',
        url_sitio_web: '',
        descripcion: ''
    });

    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({ ...prevData, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        try {
            // Enviar los datos del formulario a la API para crear el refugio
            const response = await client.post('/refugios/', formData);
            setSuccess(`¡Refugio "${response.data.nombre}" creado con éxito! (ID: ${response.data.id})`);

            // Limpiar el formulario después de la creación
            setFormData({
                nombre: '', persona_contacto: '', email: '', telefono: '',
                password: '', direccion: '', ciudad: '', pais: '',
                url_sitio_web: '', descripcion: ''
            });

            // Redirigir a la lista de refugios después de unos segundos
            setTimeout(() => navigate('/refugios'), 2000);
        } catch (err) {
            console.error("Error creando refugio:", err);
            const msg = err.response?.data?.email 
                ? "Ese email ya está registrado." 
                : "Error al crear el refugio. Revisa los datos.";
            setError(msg);
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-lg max-w-4xl mx-auto my-8">
            <h1 className="text-3xl font-bold text-gray-800 mb-2">Registrar Nuevo Refugio</h1>
            <p className="text-gray-500 mb-6">Crea una cuenta para una organización y su administrador asociado.</p>
            
            {error && <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-4">{error}</div>}
            {success && <div className="bg-green-100 border-l-4 border-green-500 text-green-700 p-4 mb-4">{success}</div>}

            <form onSubmit={handleSubmit} className="space-y-6">
                
                {/* SECCIÓN 1: DATOS DE LA ORGANIZACIÓN */}
                <div>
                    <h2 className="text-lg font-semibold text-gray-700 border-b pb-2 mb-4">Datos de la Organización</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                        <div className="flex flex-col">
                            <label className="text-sm font-medium text-gray-700">Nombre del Refugio <span className="text-red-500">*</span></label>
                            <input
                                type="text"
                                name="nombre"
                                value={formData.nombre}
                                onChange={handleInputChange}
                                required
                                placeholder="Ej: Patitas Felices"
                                className="mt-1 p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500"
                            />
                        </div>

                        <div className="flex flex-col">
                            <label className="text-sm font-medium text-gray-700">Sitio Web</label>
                            <input
                                type="url"
                                name="url_sitio_web"
                                value={formData.url_sitio_web}
                                onChange={handleInputChange}
                                placeholder="https://..."
                                className="mt-1 p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500"
                            />
                        </div>

                        <div className="flex flex-col md:col-span-2">
                            <label className="text-sm font-medium text-gray-700">Descripción</label>
                            <textarea
                                name="descripcion"
                                value={formData.descripcion}
                                onChange={handleInputChange}
                                rows="3"
                                placeholder="Breve historia o misión del refugio..."
                                className="mt-1 p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500"
                            />
                        </div>
                    </div>
                </div>

                {/* SECCIÓN 2: DATOS DE CONTACTO Y ACCESO */}
                <div>
                    <h2 className="text-lg font-semibold text-gray-700 border-b pb-2 mb-4">Contacto y Acceso (Admin)</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                        <div className="flex flex-col">
                            <label className="text-sm font-medium text-gray-700">Persona de Contacto <span className="text-red-500">*</span></label>
                            <input
                                type="text"
                                name="persona_contacto"
                                value={formData.persona_contacto}
                                onChange={handleInputChange}
                                required
                                placeholder="Ej: Juan Pérez"
                                className="mt-1 p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500"
                            />
                            <span className="text-xs text-gray-400 mt-1">Se usará para crear el nombre del usuario administrador.</span>
                        </div>

                        <div className="flex flex-col">
                            <label className="text-sm font-medium text-gray-700">Teléfono <span className="text-red-500">*</span></label>
                            <input
                                type="tel"
                                name="telefono"
                                value={formData.telefono}
                                onChange={handleInputChange}
                                required
                                className="mt-1 p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500"
                            />
                        </div>

                        <div className="flex flex-col">
                            <label className="text-sm font-medium text-gray-700">Email (Usuario) <span className="text-red-500">*</span></label>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleInputChange}
                                required
                                autoComplete="new-email"
                                className="mt-1 p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500"
                            />
                        </div>

                        <div className="flex flex-col">
                            <label className="text-sm font-medium text-gray-700">Contraseña <span className="text-red-500">*</span></label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleInputChange}
                                required
                                autoComplete="new-password"
                                className="mt-1 p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500"
                            />
                        </div>
                    </div>
                </div>

                {/* SECCIÓN 3: UBICACIÓN */}
                <div>
                    <h2 className="text-lg font-semibold text-gray-700 border-b pb-2 mb-4">Ubicación</h2>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div className="flex flex-col md:col-span-3">
                            <label className="text-sm font-medium text-gray-700">Dirección</label>
                            <input
                                type="text"
                                name="direccion"
                                value={formData.direccion}
                                onChange={handleInputChange}
                                required
                                className="mt-1 p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500"
                            />
                        </div>
                        <div className="flex flex-col">
                            <label className="text-sm font-medium text-gray-700">Ciudad</label>
                            <input
                                type="text"
                                name="ciudad"
                                value={formData.ciudad}
                                onChange={handleInputChange}
                                required
                                className="mt-1 p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500"
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
                                className="mt-1 p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500"
                            />
                        </div>
                    </div>
                </div>

                <div className="flex justify-end gap-3 mt-8 pt-4 border-t border-gray-100">
                    <button
                        type="button"
                        onClick={() => navigate('/')}
                        className="px-6 py-2.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                    >
                        Cancelar
                    </button>
                    <button
                        type="submit"
                        className="px-6 py-2.5 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 shadow-md transition-colors"
                    >
                        Registrar Refugio
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CreateRefugio;
