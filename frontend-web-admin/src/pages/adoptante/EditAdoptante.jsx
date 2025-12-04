import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import client from '../../api/client';

const EditAdoptante = () => {
    const { id } = useParams();
    const navigate = useNavigate();  // Para redirigir después de guardar o cancelar
    const [adoptante, setAdoptante] = useState(null);
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

    useEffect(() => {
        const loadAdoptante = async () => {
            try {
                const response = await client.get(`/adoptantes/${id}/`);
                setAdoptante(response.data);
                setFormData(response.data);
            } catch (err) {
                console.error('Error al cargar adoptante', err);
            }
        };

        loadAdoptante();
    }, [id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await client.put(`/adoptantes/${id}/`, formData);
            alert('Adoptante actualizado');
            navigate('/adoptantes'); // Redirige al listado de adoptantes después de guardar
        } catch (err) {
            console.error('Error al actualizar adoptante', err);
            alert('Hubo un error al guardar los cambios');
        }
    };

    const handleCancel = () => {
        navigate('/adoptantes'); // Redirige al listado de adoptantes al cancelar
    };

    if (!adoptante) return <p>Cargando...</p>;

    return (
        <div className="p-8 max-w-4xl mx-auto bg-white rounded-lg shadow-lg">
            <h1 className="text-3xl font-semibold text-gray-700 mb-6">Editar Adoptante</h1>
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Campo Nombre */}
                <div>
                    <label htmlFor="nombre" className="block text-sm font-medium text-gray-700">Nombre</label>
                    <input
                        type="text"
                        name="nombre"
                        id="nombre"
                        value={formData.nombre}
                        onChange={handleChange}
                        className="mt-2 p-2 border border-gray-300 rounded-md w-full"
                        required
                    />
                </div>

                {/* Campo Apellido Paterno */}
                <div>
                    <label htmlFor="apellido_paterno" className="block text-sm font-medium text-gray-700">Apellido Paterno</label>
                    <input
                        type="text"
                        name="apellido_paterno"
                        id="apellido_paterno"
                        value={formData.apellido_paterno}
                        onChange={handleChange}
                        className="mt-2 p-2 border border-gray-300 rounded-md w-full"
                        required
                    />
                </div>

                {/* Campo Apellido Materno */}
                <div>
                    <label htmlFor="apellido_materno" className="block text-sm font-medium text-gray-700">Apellido Materno</label>
                    <input
                        type="text"
                        name="apellido_materno"
                        id="apellido_materno"
                        value={formData.apellido_materno}
                        onChange={handleChange}
                        className="mt-2 p-2 border border-gray-300 rounded-md w-full"
                    />
                </div>

                {/* Campo Email */}
                <div>
                    <label htmlFor="email" className="block text-sm font-medium text-gray-700">Email</label>
                    <input
                        type="email"
                        name="email"
                        id="email"
                        value={formData.email}
                        onChange={handleChange}
                        className="mt-2 p-2 border border-gray-300 rounded-md w-full"
                        required
                    />
                </div>

                {/* Campo Teléfono */}
                <div>
                    <label htmlFor="telefono" className="block text-sm font-medium text-gray-700">Teléfono</label>
                    <input
                        type="text"
                        name="telefono"
                        id="telefono"
                        value={formData.telefono}
                        onChange={handleChange}
                        className="mt-2 p-2 border border-gray-300 rounded-md w-full"
                        required
                    />
                </div>

                {/* Campo Ciudad */}
                <div>
                    <label htmlFor="ciudad" className="block text-sm font-medium text-gray-700">Ciudad</label>
                    <input
                        type="text"
                        name="ciudad"
                        id="ciudad"
                        value={formData.ciudad}
                        onChange={handleChange}
                        className="mt-2 p-2 border border-gray-300 rounded-md w-full"
                        required
                    />
                </div>

                {/* Campo Dirección */}
                <div>
                    <label htmlFor="direccion" className="block text-sm font-medium text-gray-700">Dirección</label>
                    <input
                        type="text"
                        name="direccion"
                        id="direccion"
                        value={formData.direccion}
                        onChange={handleChange}
                        className="mt-2 p-2 border border-gray-300 rounded-md w-full"
                        required
                    />
                </div>

                {/* Campo Fecha de Nacimiento */}
                <div>
                    <label htmlFor="fecha_nacimiento" className="block text-sm font-medium text-gray-700">Fecha de Nacimiento</label>
                    <input
                        type="date"
                        name="fecha_nacimiento"
                        id="fecha_nacimiento"
                        value={formData.fecha_nacimiento}
                        onChange={handleChange}
                        className="mt-2 p-2 border border-gray-300 rounded-md w-full"
                        required
                    />
                </div>

                {/* Campo País */}
                <div>
                    <label htmlFor="pais" className="block text-sm font-medium text-gray-700">País</label>
                    <input
                        type="text"
                        name="pais"
                        id="pais"
                        value={formData.pais}
                        onChange={handleChange}
                        className="mt-2 p-2 border border-gray-300 rounded-md w-full"
                        required
                    />
                </div>

                {/* Botones para guardar y cancelar */}
                <div className="col-span-2 flex justify-end gap-3 mt-6">
                    <button
                        type="button"
                        onClick={handleCancel}
                        className="px-6 py-3 text-sm text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300"
                    >
                        Cancelar
                    </button>
                    <button
                        type="submit"
                        className="px-6 py-3 text-sm text-white bg-indigo-600 rounded-lg hover:bg-indigo-700"
                    >
                        Guardar Cambios
                    </button>
                </div>
            </form>
        </div>
    );
};

export default EditAdoptante;
