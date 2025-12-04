import React, { useState } from 'react';
import client from "../../api/client";  // Ruta corregida

const CreateAdoptante = () => {
    const [formData, setFormData] = useState({
        email: '',
        nombre: '',
        apellido_paterno: '',
        apellido_materno: '',
        telefono: '',
        password: '',
        ciudad: '',
        direccion: '',
        fecha_nacimiento: '',
        pais: '',
    });
    const [error, setError] = useState(null);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({ ...prevData, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await client.post('/adoptantes/', formData);
            alert('Adoptante creado: ' + response.data.id);
        } catch (err) {
            setError('Error al crear el adoptante');
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-lg max-w-3xl mx-auto">
            <h1 className="text-2xl font-semibold text-gray-700 mb-6">Crear Adoptante</h1>
            {error && <p className="text-red-500 mb-4">{error}</p>}
            <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {/* Input for Email */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Email:</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleInputChange}
                            required
                            autoComplete="email"  // Agregado el autocomplete para el email
                            className="mt-2 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>

                    {/* Input for Nombre */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Nombre:</label>
                        <input
                            type="text"
                            name="nombre"
                            value={formData.nombre}
                            onChange={handleInputChange}
                            required
                            autoComplete="given-name"  // Agregado el autocomplete para el nombre
                            className="mt-2 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {/* Input for Apellido Paterno */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Apellido Paterno:</label>
                        <input
                            type="text"
                            name="apellido_paterno"
                            value={formData.apellido_paterno}
                            onChange={handleInputChange}
                            required
                            autoComplete="family-name"  // Agregado el autocomplete para el apellido
                            className="mt-2 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>

                    {/* Input for Apellido Materno */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Apellido Materno:</label>
                        <input
                            type="text"
                            name="apellido_materno"
                            value={formData.apellido_materno}
                            onChange={handleInputChange}
                            required
                            autoComplete="family-name"  // Agregado el autocomplete para el apellido
                            className="mt-2 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {/* Input for Teléfono */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Teléfono:</label>
                        <input
                            type="text"
                            name="telefono"
                            value={formData.telefono}
                            onChange={handleInputChange}
                            required
                            autoComplete="tel"  // Agregado el autocomplete para el teléfono
                            className="mt-2 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>

                    {/* Input for Password */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Password:</label>
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleInputChange}
                            required
                            autoComplete="new-password"  // Agregado el autocomplete para el password
                            className="mt-2 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {/* Input for Ciudad */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Ciudad:</label>
                        <input
                            type="text"
                            name="ciudad"
                            value={formData.ciudad}
                            onChange={handleInputChange}
                            required
                            autoComplete="address-level2"  // Agregado el autocomplete para la ciudad
                            className="mt-2 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>

                    {/* Input for Dirección */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Dirección:</label>
                        <input
                            type="text"
                            name="direccion"
                            value={formData.direccion}
                            onChange={handleInputChange}
                            required
                            autoComplete="street-address"  // Agregado el autocomplete para la dirección
                            className="mt-2 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {/* Input for Fecha de Nacimiento */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">Fecha de Nacimiento:</label>
                        <input
                            type="date"
                            name="fecha_nacimiento"
                            value={formData.fecha_nacimiento}
                            onChange={handleInputChange}
                            required
                            className="mt-2 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>

                    {/* Input for País */}
                    <div className="flex flex-col">
                        <label className="text-sm font-medium text-gray-700">País:</label>
                        <input
                            type="text"
                            name="pais"
                            value={formData.pais}
                            onChange={handleInputChange}
                            required
                            autoComplete="country"  // Agregado el autocomplete para el país
                            className="mt-2 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>
                </div>

                <div className="flex justify-end gap-3 mt-6 pt-4 border-t border-gray-100">
                    <button
                        type="button"
                        onClick={() => setFormData({
                            email: '',
                            nombre: '',
                            apellido_paterno: '',
                            apellido_materno: '',
                            telefono: '',
                            password: '',
                            ciudad: '',
                            direccion: '',
                            fecha_nacimiento: '',
                            pais: '',
                        })}
                        className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
                    >
                        Cancelar
                    </button>
                    <button
                        type="submit"
                        className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 disabled:opacity-50"
                    >
                        Guardar
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CreateAdoptante;
