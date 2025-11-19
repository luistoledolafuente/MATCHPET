import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../../contexts/AuthContext.jsx'; // <-- agregado
import { X, Save, PawPrint, ChevronDown } from 'lucide-react';
import {
    getRazas, getGeneros, getEstadosAdopcion,
    getTamanos, getNivelesEnergia, getTemperamentos
} from '../../services/lookupsService';
import { createAnimal, updateAnimal } from '../../services/animalService';

export default function AnimalForm({ animal, onClose, onSuccess, token: tokenProp }) {
  const auth = useAuth(); 

  const token = tokenProp
    || auth?.token
    || auth?.accessToken
    || auth?.user?.accessToken
    || auth?.user?.token
    || (axios.defaults.headers.common?.Authorization || '').replace('Bearer ', '') || null;

    const isEditing = !!animal;

    const [formData, setFormData] = useState({
        nombre: '',
        razaId: '',
        nombreRazaManual: '',
        generoId: '',
        estadoAdopcionId: 1,
        tamanoId: '',
        nivelEnergiaId: '',
        temperamentosIds: [],
        fotosUrls: [],
        fotoPrincipalIndex: 0,
        fechaNacimientoAprox: new Date().toISOString().slice(0, 10),
        fechaIngresoRefugio: new Date().toISOString().slice(0, 10),
        descripcionPersonalidad: '',
        compatibleNiños: true,
        compatibleOtrasMascotas: true,
        estaVacunado: true,
        estaEsterilizado: true,
        historialMedico: '',
    });

    const [lookups, setLookups] = useState({});
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [formStatus, setFormStatus] = useState(null);

    // Cargar lookups
    useEffect(() => {
        const loadLookups = async () => {
            try {
                const [razas, generos, estadosAdopcion, tamanos, nivelesEnergia, temperamentos] = await Promise.all([
                    getRazas(token),
                    getGeneros(token),
                    getEstadosAdopcion(token),
                    getTamanos(token),
                    getNivelesEnergia(token),
                    getTemperamentos(token),
                ]);

                setLookups({
                    razas: razas.map(r => ({ id: r.id, nombre: r.nombreRaza })),
                    generos,
                    estadosAdopcion,
                    tamanos,
                    nivelesEnergia,
                    temperamentos,
                });
            } catch (err) {
                console.error("Error cargando lookups:", err);
                setError("No se pudieron cargar los datos de selección.");
            }
        };
        loadLookups();
    }, [token]);

    // Rellenar formulario si es edición
    useEffect(() => {
        if (isEditing && animal) {
            setFormData({
                nombre: animal.nombre || '',
                razaId: animal.raza?.id || '',
                nombreRazaManual: '',
                generoId: animal.genero?.id || '',
                estadoAdopcionId: animal.estadoAdopcion?.id || 1,
                tamanoId: animal.tamano?.id || '',
                nivelEnergiaId: animal.nivelEnergia?.id || '',
                temperamentosIds: animal.temperamentos?.map(t => t.id) || [],
                fotosUrls: animal.fotos?.map(f => f.urlFoto) || [],
                fotoPrincipalIndex: animal.fotos?.findIndex(f => f.esPrincipal) ?? 0,
                fechaNacimientoAprox: animal.fechaNacimientoAprox?.split('T')[0] || new Date().toISOString().slice(0, 10),
                fechaIngresoRefugio: animal.fechaIngresoRefugio?.split('T')[0] || new Date().toISOString().slice(0, 10),
                descripcionPersonalidad: animal.descripcionPersonalidad || '',
                compatibleNiños: animal.compatibleNiños ?? true,
                compatibleOtrasMascotas: animal.compatibleOtrasMascotas ?? true,
                estaVacunado: animal.estaVacunado ?? true,
                estaEsterilizado: animal.estaEsterilizado ?? true,
                historialMedico: animal.historialMedico || '',
            });
        }
    }, [isEditing, animal]);

    // Cambios en inputs
    const handleChange = e => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: name.endsWith('Id') ? parseInt(value) : type === 'checkbox' ? checked : value
        }));
    };

    const handleTemperamentoChange = e => {
        const value = parseInt(e.target.value);
        setFormData(prev => ({
            ...prev,
            temperamentosIds: prev.temperamentosIds.includes(value)
                ? prev.temperamentosIds.filter(id => id !== value)
                : [...prev.temperamentosIds, value],
        }));
    };

    const handleFotoChange = (index, url) => {
        setFormData(prev => {
            const newFotos = [...prev.fotosUrls];
            newFotos[index] = url;
            return { ...prev, fotosUrls: newFotos };
        });
    };

    const handleAddFoto = () => {
        setFormData(prev => ({ ...prev, fotosUrls: [...prev.fotosUrls, ''] }));
    };

    const handleRemoveFoto = index => {
        setFormData(prev => {
            const newFotos = prev.fotosUrls.filter((_, i) => i !== index);
            let newPrincipal = prev.fotoPrincipalIndex;
            if (index === prev.fotoPrincipalIndex) newPrincipal = 0;
            else if (index < prev.fotoPrincipalIndex) newPrincipal -= 1;
            return { ...prev, fotosUrls: newFotos, fotoPrincipalIndex: newPrincipal };
        });
    };

    const parseJwt = (token) => {
        try {
            const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
            return JSON.parse(decodeURIComponent(escape(atob(base64))));
        } catch (e) {
            return null;
        }
    };

    const handleSubmit = async e => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setFormStatus(null);

        // Obtener token: prioridad prop > contexto > axios.defaults
        const token = tokenProp
          || auth?.token
          || auth?.accessToken
          || auth?.user?.accessToken
          || auth?.user?.token
          || (axios.defaults.headers.common?.Authorization || '').replace('Bearer ', '') || null;

        if (!token) {
          setError("Token no disponible. Por favor inicia sesión de nuevo.");
          setLoading(false);
          return;
        }

        // Asegurar header Authorization (refuerzo)
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

        // Construir payload JSON (si necesitas subir archivos binarios convierte a FormData)
        const dataToSend = {
            ...formData,
            razaId: formData.razaId || undefined,
            nombreRazaManual: formData.razaId ? undefined : formData.nombreRazaManual || undefined,
            fotos: formData.fotosUrls.map((url, index) => ({ urlFoto: url, esPrincipal: index === formData.fotoPrincipalIndex })),
            temperamentosIds: formData.temperamentosIds.map(id => parseInt(id)),
        };
        delete dataToSend.fotosUrls;
        delete dataToSend.fotoPrincipalIndex;

        try {
            if (isEditing) {
                await updateAnimal(animal.id, dataToSend, token);
                setFormStatus("Animal actualizado exitosamente.");
            } else {
                await createAnimal(dataToSend, token);
                setFormStatus("Animal registrado exitosamente.");
            }
            setTimeout(onSuccess, 600);
        } catch (err) {
            console.error('Error creando/actualizando animal:', err);
            if (err?.response?.status === 401) {
                setError("No autorizado (401). El servidor rechazó el token.");
            } else {
                setError(err.response?.data?.message || err.message || "Error al comunicarse con el servidor.");
            }
        } finally {
            setLoading(false);
        }
    };

    const renderRazaField = () => (
        <div className="relative">
            <label className="block text-sm font-medium text-gray-700 mb-1">Raza</label>
            {lookups.razas?.length > 0 ? (
                <select
                    name="razaId"
                    value={formData.razaId || ""}
                    onChange={handleChange}
                    required
                    className="w-full pl-3 pr-10 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-[#FDB2A0] focus:border-[#FDB2A0] appearance-none"
                >
                    <option value="" disabled>Selecciona Raza</option>
                    {lookups.razas.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
                </select>
            ) : (
                <input
                    type="text"
                    name="nombreRazaManual"
                    value={formData.nombreRazaManual || ""}
                    onChange={handleChange}
                    placeholder="Escribe la raza"
                    required
                    className="w-full p-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-[#FDB2A0] focus:border-[#FDB2A0]"
                />
            )}
            <ChevronDown className="absolute right-3 top-9 h-4 w-4 text-gray-400 pointer-events-none" />
        </div>
    );

    const renderSelectOptions = (lookup, name, label) => (
        <div className="relative">
            <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
            <select
                name={name}
                value={formData[name] || ""}
                onChange={handleChange}
                required
                className="w-full pl-3 pr-10 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-[#FDB2A0] focus:border-[#FDB2A0] appearance-none"
            >
                <option value="" disabled>Selecciona {label}</option>
                {lookup?.map(l => <option key={l.id} value={l.id}>{l.nombre}</option>)}
            </select>
            <ChevronDown className="absolute right-3 top-9 h-4 w-4 text-gray-400 pointer-events-none" />
        </div>
    );

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50 p-4">
            <div className="bg-white rounded-xl shadow-2xl w-full max-w-4xl max-h-[90vh] overflow-y-auto">
                <div className="sticky top-0 bg-white p-6 border-b border-gray-200 flex justify-between items-center z-10">
                    <h2 className="text-2xl font-bold text-[#316B7A] flex items-center">
                        <PawPrint className="w-6 h-6 mr-2 text-[#FDB2A0]" />
                        {isEditing ? "Editar Mascota" : "Registrar Nueva Mascota"}
                    </h2>
                    <button onClick={onClose} className="text-gray-500 hover:text-gray-800 transition">
                        <X className="w-6 h-6" />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-6">
                    {error && <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-3 rounded-lg">{error}</div>}
                    {formStatus && <div className="bg-green-100 border-l-4 border-green-500 text-green-700 p-3 rounded-lg">{formStatus}</div>}

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 p-4 border border-[#FDB2A0]/50 rounded-lg bg-[#FFF7E6]">
                        <h3 className="md:col-span-2 text-xl font-semibold text-[#316B7A] border-b pb-2 mb-4">Datos Principales</h3>
                        <div>
                            <label htmlFor="nombre" className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
                            <input type="text" id="nombre" name="nombre" value={formData.nombre} onChange={handleChange} required className="w-full p-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-[#FDB2A0] focus:border-[#FDB2A0]" />
                        </div>
                        {renderRazaField()}
                        {renderSelectOptions(lookups.generos, 'generoId', 'Género')}
                        {renderSelectOptions(lookups.estadosAdopcion, 'estadoAdopcionId', 'Estado de Adopción')}
                        {renderSelectOptions(lookups.tamanos, 'tamanoId', 'Tamaño')}
                        {renderSelectOptions(lookups.nivelesEnergia, 'nivelEnergiaId', 'Nivel de Energía')}
                        <div>
                            <label htmlFor="fechaNacimientoAprox" className="block text-sm font-medium text-gray-700 mb-1">Fecha Nacimiento (Aprox.)</label>
                            <input type="date" id="fechaNacimientoAprox" name="fechaNacimientoAprox" value={formData.fechaNacimientoAprox} onChange={handleChange} required className="w-full p-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-[#FDB2A0] focus:border-[#FDB2A0]" />
                        </div>
                        <div>
                            <label htmlFor="fechaIngresoRefugio" className="block text-sm font-medium text-gray-700 mb-1">Fecha Ingreso Refugio</label>
                            <input type="date" id="fechaIngresoRefugio" name="fechaIngresoRefugio" value={formData.fechaIngresoRefugio} onChange={handleChange} required className="w-full p-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-[#FDB2A0] focus:border-[#FDB2A0]" />
                        </div>
                    </div>

                    {/* Personalidad y compatibilidad */}
                    <div className="p-4 border border-[#FDB2A0]/50 rounded-lg bg-[#FFF7E6]">
                        <h3 className="text-xl font-semibold text-[#316B7A] border-b pb-2 mb-4">Personalidad y Compatibilidad</h3>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Descripción Personalidad</label>
                            <textarea name="descripcionPersonalidad" value={formData.descripcionPersonalidad} onChange={handleChange} className="w-full p-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-[#FDB2A0] focus:border-[#FDB2A0]" />
                        </div>
                        <div className="flex gap-4 mt-2">
                            <label className="flex items-center gap-2"><input type="checkbox" name="compatibleNiños" checked={formData.compatibleNiños} onChange={handleChange} /> Compatible con Niños</label>
                            <label className="flex items-center gap-2"><input type="checkbox" name="compatibleOtrasMascotas" checked={formData.compatibleOtrasMascotas} onChange={handleChange} /> Compatible con Otras Mascotas</label>
                            <label className="flex items-center gap-2"><input type="checkbox" name="estaVacunado" checked={formData.estaVacunado} onChange={handleChange} /> Vacunado</label>
                            <label className="flex items-center gap-2"><input type="checkbox" name="estaEsterilizado" checked={formData.estaEsterilizado} onChange={handleChange} /> Esterilizado</label>
                        </div>
                        <div className="mt-2">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Historial Médico</label>
                            <textarea name="historialMedico" value={formData.historialMedico} onChange={handleChange} className="w-full p-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-[#FDB2A0] focus:border-[#FDB2A0]" />
                        </div>
                        <div className="mt-2">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Temperamentos</label>
                            <div className="flex flex-wrap gap-2">
                                {lookups.temperamentos?.map(t => (
                                    <label key={t.id} className="flex items-center gap-1 border px-2 py-1 rounded">
                                        <input type="checkbox" value={t.id} checked={formData.temperamentosIds.includes(t.id)} onChange={handleTemperamentoChange} />
                                        {t.nombre}
                                    </label>
                                ))}
                            </div>
                        </div>
                    </div>

                    {/* Fotos */}
                    <div className="p-4 border border-[#FDB2A0]/50 rounded-lg bg-[#FFF7E6]">
                        <h3 className="text-xl font-semibold text-[#316B7A] border-b pb-2 mb-4">Fotos</h3>
                        {formData.fotosUrls.map((url, index) => (
                            <div key={index} className="flex items-center gap-2 mb-2">
                                <input type="text" value={url} onChange={e => handleFotoChange(index, e.target.value)} placeholder="URL de la foto" className="flex-1 p-2 border border-gray-300 rounded-lg" />
                                <button type="button" onClick={() => handleRemoveFoto(index)} className="px-2 py-1 bg-red-500 text-white rounded">Eliminar</button>
                                <label className="flex items-center gap-1">
                                    <input type="radio" name="fotoPrincipal" checked={formData.fotoPrincipalIndex === index} onChange={() => setFormData(prev => ({ ...prev, fotoPrincipalIndex: index }))} />
                                    Principal
                                </label>
                            </div>
                        ))}
                        <button type="button" onClick={handleAddFoto} className="px-4 py-2 bg-green-500 text-white rounded mt-2">Agregar Foto</button>
                    </div>

                    <div className="pt-4 border-t border-gray-200">
                        <button type="submit" className="w-full flex items-center justify-center bg-[#316B7A] text-white font-bold py-3 px-6 rounded-full shadow-lg hover:bg-[#2a5b67] transition duration-200 disabled:opacity-50" disabled={loading}>
                            <Save className="w-5 h-5 mr-2" /> {isEditing ? 'Guardar Cambios' : 'Registrar Animal'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
