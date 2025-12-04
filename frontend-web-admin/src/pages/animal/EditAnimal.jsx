import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getAnimalById, updateAnimal, fetchCatalogs } from '../../api/fetchAnimales';

const EditAnimal = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    
    // Estados de carga y error
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Estado de los catálogos (para los Selects)
    const [catalogs, setCatalogs] = useState({
        refugios: [], razas: [], generos: [], tamanos: [], energias: [], estados: []
    });

    // Estado del formulario
    const [formData, setFormData] = useState({
        nombre: '',
        fecha_nacimiento_aprox: '',
        descripcion_personalidad: '',
        historial_medico: '',
        compatible_ninos: false,
        compatible_otras_mascotas: false,
        esta_vacunado: false,
        esta_esterilizado: false,
        fecha_ingreso_refugio: '',
        // Foreign Keys (IDs)
        refugio: '',
        raza: '',
        genero: '',
        tamano: '',
        nivel_energia: '',
        estado_adopcion: ''
    });

    // CARGAR DATOS AL INICIAR
    useEffect(() => {
        const loadData = async () => {
            try {
                setLoading(true);
                
                // 1. Cargamos catálogos y datos del animal en paralelo
                const [catalogData, animalData] = await Promise.all([
                    fetchCatalogs(),
                    getAnimalById(id)
                ]);

                // 2. Setear Catálogos (con protección de arrays vacíos)
                if (catalogData) {
                    setCatalogs({
                        refugios: Array.isArray(catalogData.refugios) ? catalogData.refugios : (catalogData.refugios?.results || []),
                        razas: Array.isArray(catalogData.razas) ? catalogData.razas : (catalogData.razas?.results || []),
                        generos: Array.isArray(catalogData.generos) ? catalogData.generos : (catalogData.generos?.results || []),
                        tamanos: Array.isArray(catalogData.tamanos) ? catalogData.tamanos : (catalogData.tamanos?.results || []),
                        energias: Array.isArray(catalogData.energias) ? catalogData.energias : (catalogData.energias?.results || []),
                        estados: Array.isArray(catalogData.estados) ? catalogData.estados : (catalogData.estados?.results || [])
                    });
                }

                // 3. Pre-llenar Formulario con los datos actuales
                if (animalData) {
                    setFormData({
                        nombre: animalData.nombre || '',
                        fecha_nacimiento_aprox: animalData.fecha_nacimiento_aprox || '',
                        descripcion_personalidad: animalData.descripcion_personalidad || '',
                        historial_medico: animalData.historial_medico || '',
                        compatible_ninos: animalData.compatible_ninos || false,
                        compatible_otras_mascotas: animalData.compatible_otras_mascotas || false,
                        esta_vacunado: animalData.esta_vacunado || false,
                        esta_esterilizado: animalData.esta_esterilizado || false,
                        fecha_ingreso_refugio: animalData.fecha_ingreso_refugio || '',
                        // Aseguramos que se asignen los IDs (tu serializer devuelve IDs en estos campos por defecto)
                        refugio: animalData.refugio || '',
                        raza: animalData.raza || '',
                        genero: animalData.genero || '',
                        tamano: animalData.tamano || '',
                        nivel_energia: animalData.nivel_energia || '',
                        estado_adopcion: animalData.estado_adopcion || ''
                    });
                }

            } catch (err) {
                console.error("Error cargando datos:", err);
                setError("No se pudo cargar la información de la mascota.");
            } finally {
                setLoading(false);
            }
        };

        loadData();
    }, [id]);

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
            await updateAnimal(id, formData);
            alert("¡Mascota actualizada correctamente!");
            navigate('/animales');
        } catch (error) {
            console.error("Error actualizando:", error);
            alert("Error al actualizar. Revisa los datos.");
        }
    };

    if (loading) return <div className="p-10 text-center text-gray-500 text-lg">Cargando datos de la mascota...</div>;
    if (error) return <div className="p-10 text-center text-red-500 bg-red-50 rounded-lg m-8 border border-red-200">{error}</div>;

    return (
        <div className="bg-white p-8 rounded-lg shadow-lg max-w-5xl mx-auto my-8">
            <div className="flex justify-between items-center mb-6 border-b pb-4">
                <h1 className="text-2xl font-bold text-gray-800">Editar Mascota #{id}</h1>
                <button 
                    type="button"
                    onClick={() => navigate('/animales')}
                    className="text-gray-500 hover:text-gray-700"
                >
                    ✕ Cancelar
                </button>
            </div>
            
            <form onSubmit={handleSubmit} className="space-y-6">
                {/* 1. CLASIFICACIÓN */}
                <div className="bg-indigo-50 p-5 rounded-lg border border-indigo-100">
                    <h3 className="text-indigo-800 font-semibold mb-3 uppercase text-xs tracking-wider">Clasificación</h3>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Refugio</label>
                            <select name="refugio" value={formData.refugio} onChange={handleChange} required 
                                className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 bg-white">
                                <option value="">-- Seleccionar --</option>
                                {catalogs.refugios?.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Raza</label>
                            <select name="raza" value={formData.raza} onChange={handleChange} required 
                                className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 bg-white">
                                <option value="">-- Seleccionar --</option>
                                {catalogs.razas?.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
                            </select>
                        </div>
                         <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Estado Adopción</label>
                            <select name="estado_adopcion" value={formData.estado_adopcion} onChange={handleChange} required 
                                className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 bg-white">
                                {catalogs.estados?.map(e => <option key={e.id} value={e.id}>{e.nombre}</option>)}
                            </select>
                        </div>
                    </div>
                </div>

                {/* 2. DATOS BÁSICOS */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
                        <input type="text" name="nombre" value={formData.nombre} onChange={handleChange} required 
                            className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500" />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Fecha Nacimiento</label>
                        <input type="date" name="fecha_nacimiento_aprox" value={formData.fecha_nacimiento_aprox} onChange={handleChange} required 
                            className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500" />
                    </div>
                </div>

                {/* 3. CARACTERÍSTICAS FÍSICAS */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Género</label>
                        <select name="genero" value={formData.genero} onChange={handleChange} required 
                            className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 bg-white">
                            {catalogs.generos?.map(g => <option key={g.id} value={g.id}>{g.nombre}</option>)}
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Tamaño</label>
                        <select name="tamano" value={formData.tamano} onChange={handleChange} required 
                            className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 bg-white">
                            {catalogs.tamanos?.map(t => <option key={t.id} value={t.id}>{t.nombre}</option>)}
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Nivel de Energía</label>
                        <select name="nivel_energia" value={formData.nivel_energia} onChange={handleChange} required 
                            className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 bg-white">
                            {catalogs.energias?.map(e => <option key={e.id} value={e.id}>{e.nombre}</option>)}
                        </select>
                    </div>
                </div>

                {/* 4. DESCRIPCIÓN */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Descripción / Personalidad</label>
                    <textarea name="descripcion_personalidad" value={formData.descripcion_personalidad} onChange={handleChange} required rows="4"
                        className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500" />
                </div>
                 <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Historial Médico</label>
                    <textarea name="historial_medico" value={formData.historial_medico} onChange={handleChange} rows="2"
                        className="w-full p-2.5 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500" />
                </div>

                {/* 5. CHECKBOXES (SALUD Y COMPORTAMIENTO) */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 pt-2">
                    <label className="flex items-center space-x-3 p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors">
                        <input type="checkbox" name="compatible_ninos" checked={formData.compatible_ninos} onChange={handleChange} 
                            className="h-5 w-5 text-indigo-600 rounded focus:ring-indigo-500" />
                        <span className="text-sm text-gray-700">Compatible Niños</span>
                    </label>
                    <label className="flex items-center space-x-3 p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors">
                        <input type="checkbox" name="compatible_otras_mascotas" checked={formData.compatible_otras_mascotas} onChange={handleChange} 
                            className="h-5 w-5 text-indigo-600 rounded focus:ring-indigo-500" />
                        <span className="text-sm text-gray-700">Otros Animales</span>
                    </label>
                    <label className="flex items-center space-x-3 p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors">
                        <input type="checkbox" name="esta_vacunado" checked={formData.esta_vacunado} onChange={handleChange} 
                            className="h-5 w-5 text-indigo-600 rounded focus:ring-indigo-500" />
                        <span className="text-sm text-gray-700">Vacunado</span>
                    </label>
                    <label className="flex items-center space-x-3 p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors">
                        <input type="checkbox" name="esta_esterilizado" checked={formData.esta_esterilizado} onChange={handleChange} 
                            className="h-5 w-5 text-indigo-600 rounded focus:ring-indigo-500" />
                        <span className="text-sm text-gray-700">Esterilizado</span>
                    </label>
                </div>

                {/* BOTONES DE ACCIÓN */}
                <div className="flex justify-end gap-4 pt-6 border-t border-gray-100">
                    <button 
                        type="button" 
                        onClick={() => navigate('/animales')} 
                        className="px-6 py-2.5 bg-white border border-gray-300 text-gray-700 font-medium rounded-lg hover:bg-gray-50 focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors"
                    >
                        Cancelar
                    </button>
                    <button 
                        type="submit" 
                        className="px-6 py-2.5 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 shadow-sm transition-colors"
                    >
                        Guardar Cambios
                    </button>
                </div>
            </form>
        </div>
    );
};

export default EditAnimal;