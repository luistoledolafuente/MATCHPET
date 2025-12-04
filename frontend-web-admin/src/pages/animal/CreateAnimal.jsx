import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createAnimal, fetchCatalogs } from '../../api/fetchAnimales';

const CreateAnimal = () => {
    const navigate = useNavigate();
    
    // 1. Inicialización SEGURA (Arrays vacíos)
    const [catalogs, setCatalogs] = useState({
        refugios: [], 
        razas: [], 
        generos: [], 
        tamanos: [], 
        energias: [], 
        estados: []
    });

    const [loading, setLoading] = useState(true); // Estado de carga

    const [formData, setFormData] = useState({
        nombre: '',
        fecha_nacimiento_aprox: '',
        descripcion_personalidad: '',
        historial_medico: '',
        compatible_ninos: false,
        compatible_otras_mascotas: false,
        esta_vacunado: false,
        esta_esterilizado: false,
        fecha_ingreso_refugio: new Date().toISOString().split('T')[0],
        refugio: '',
        raza: '',
        genero: '',
        tamano: '',
        nivel_energia: '',
        estado_adopcion: 1
    });

    useEffect(() => {
        const loadCatalogs = async () => {
            try {
                const data = await fetchCatalogs();
                if (data) {
                    // 2. Validación de datos antes de setear el estado
                    setCatalogs({
                        refugios: Array.isArray(data.refugios) ? data.refugios : (data.refugios?.results || []),
                        razas: Array.isArray(data.razas) ? data.razas : (data.razas?.results || []),
                        generos: Array.isArray(data.generos) ? data.generos : (data.generos?.results || []),
                        tamanos: Array.isArray(data.tamanos) ? data.tamanos : (data.tamanos?.results || []),
                        energias: Array.isArray(data.energias) ? data.energias : (data.energias?.results || []),
                        estados: Array.isArray(data.estados) ? data.estados : (data.estados?.results || [])
                    });
                }
            } catch (error) {
                console.error("Error cargando catálogos", error);
            } finally {
                setLoading(false);
            }
        };
        loadCatalogs();
    }, []);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await createAnimal(formData);
            alert("¡Mascota registrada con éxito!");
            navigate('/animales');
        } catch (error) {
            console.error(error);
            alert("Error al registrar. Verifica los campos obligatorios.");
        }
    };

    if (loading) return <div className="p-8 text-center">Cargando formulario...</div>;

    return (
        <div className="bg-white p-8 rounded-lg shadow-lg max-w-5xl mx-auto my-8">
            <h1 className="text-2xl font-bold mb-6 text-gray-800">Registrar Nueva Mascota</h1>
            
            <form onSubmit={handleSubmit} className="space-y-6">
                {/* 1. ASIGNACIÓN */}
                <div className="bg-indigo-50 p-4 rounded-lg border border-indigo-100">
                    <h3 className="text-indigo-800 font-semibold mb-3">Ubicación y Clasificación</h3>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Refugio Asignado *</label>
                            <select name="refugio" value={formData.refugio} onChange={handleChange} required 
                                className="w-full mt-1 p-2 border rounded">
                                <option value="">-- Seleccionar --</option>
                                {/* 3. Renderizado seguro con Optional Chaining (?.) */}
                                {catalogs.refugios?.map(r => (
                                    <option key={r.id} value={r.id}>{r.nombre}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Raza *</label>
                            <select name="raza" value={formData.raza} onChange={handleChange} required 
                                className="w-full mt-1 p-2 border rounded">
                                <option value="">-- Seleccionar --</option>
                                {catalogs.razas?.map(r => (
                                    <option key={r.id} value={r.id}>{r.nombre} ({r.nombre_especie})</option>
                                ))}
                            </select>
                        </div>
                         <div>
                            <label className="block text-sm font-medium text-gray-700">Estado *</label>
                            <select name="estado_adopcion" value={formData.estado_adopcion} onChange={handleChange} required 
                                className="w-full mt-1 p-2 border rounded">
                                {catalogs.estados?.map(e => (
                                    <option key={e.id} value={e.id}>{e.nombre}</option>
                                ))}
                            </select>
                        </div>
                    </div>
                </div>

                {/* 2. DATOS BÁSICOS */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Nombre *</label>
                        <input type="text" name="nombre" value={formData.nombre} onChange={handleChange} required 
                            className="w-full mt-1 p-2 border rounded" placeholder="Ej: Firulais" />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Fecha Nacimiento Aprox. *</label>
                        <input type="date" name="fecha_nacimiento_aprox" value={formData.fecha_nacimiento_aprox} onChange={handleChange} required 
                            className="w-full mt-1 p-2 border rounded" />
                    </div>
                </div>

                {/* 3. CARACTERÍSTICAS FÍSICAS */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Género *</label>
                        <select name="genero" value={formData.genero} onChange={handleChange} required className="w-full mt-1 p-2 border rounded">
                            <option value="">-- Seleccionar --</option>
                            {catalogs.generos?.map(g => (
                                <option key={g.id} value={g.id}>{g.nombre}</option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Tamaño *</label>
                        <select name="tamano" value={formData.tamano} onChange={handleChange} required className="w-full mt-1 p-2 border rounded">
                            <option value="">-- Seleccionar --</option>
                            {catalogs.tamanos?.map(t => (
                                <option key={t.id} value={t.id}>{t.nombre}</option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Energía *</label>
                        <select name="nivel_energia" value={formData.nivel_energia} onChange={handleChange} required className="w-full mt-1 p-2 border rounded">
                            <option value="">-- Seleccionar --</option>
                            {catalogs.energias?.map(e => (
                                <option key={e.id} value={e.id}>{e.nombre}</option>
                            ))}
                        </select>
                    </div>
                </div>

                {/* 4. DETALLES Y SALUD */}
                <div>
                    <label className="block text-sm font-medium text-gray-700">Personalidad / Descripción *</label>
                    <textarea name="descripcion_personalidad" value={formData.descripcion_personalidad} onChange={handleChange} required rows="3"
                        className="w-full mt-1 p-2 border rounded" placeholder="Es muy juguetón..." />
                </div>
                 <div>
                    <label className="block text-sm font-medium text-gray-700">Historial Médico</label>
                    <textarea name="historial_medico" value={formData.historial_medico} onChange={handleChange} rows="2"
                        className="w-full mt-1 p-2 border rounded" placeholder="Vacunas al día, desparasitado..." />
                </div>

                {/* 5. CHECKBOXES */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 pt-2">
                    <label className="flex items-center space-x-2 border p-3 rounded hover:bg-gray-50 cursor-pointer">
                        <input type="checkbox" name="compatible_ninos" checked={formData.compatible_ninos} onChange={handleChange} className="h-4 w-4 text-indigo-600" />
                        <span className="text-sm">Compatible Niños</span>
                    </label>
                    <label className="flex items-center space-x-2 border p-3 rounded hover:bg-gray-50 cursor-pointer">
                        <input type="checkbox" name="compatible_otras_mascotas" checked={formData.compatible_otras_mascotas} onChange={handleChange} className="h-4 w-4 text-indigo-600" />
                        <span className="text-sm">Otras Mascotas</span>
                    </label>
                    <label className="flex items-center space-x-2 border p-3 rounded hover:bg-gray-50 cursor-pointer">
                        <input type="checkbox" name="esta_vacunado" checked={formData.esta_vacunado} onChange={handleChange} className="h-4 w-4 text-indigo-600" />
                        <span className="text-sm">Vacunado</span>
                    </label>
                    <label className="flex items-center space-x-2 border p-3 rounded hover:bg-gray-50 cursor-pointer">
                        <input type="checkbox" name="esta_esterilizado" checked={formData.esta_esterilizado} onChange={handleChange} className="h-4 w-4 text-indigo-600" />
                        <span className="text-sm">Esterilizado</span>
                    </label>
                </div>

                <div className="flex justify-end gap-3 pt-6 border-t">
                    <button type="button" onClick={() => navigate('/animales')} className="px-6 py-2 border rounded text-gray-600 hover:bg-gray-50">Cancelar</button>
                    <button type="submit" className="px-6 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700">Guardar Mascota</button>
                </div>
            </form>
        </div>
    );
};

export default CreateAnimal;