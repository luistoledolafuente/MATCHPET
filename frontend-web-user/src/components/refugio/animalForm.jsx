import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../../contexts/AuthContext.jsx';
import { X, Save, PawPrint } from 'lucide-react';
import {
    getRazas, getGeneros, getEstadosAdopcion,
    getTamanos, getNivelesEnergia, getTemperamentos
} from '../../services/lookupsService';
import { createAnimal, updateAnimal } from '../../services/animalService';

export default function AnimalForm({ animal, onClose, onSuccess, token: tokenProp }) {

    const auth = useAuth();

    const token =
        tokenProp ||
        auth?.token ||
        auth?.accessToken ||
        auth?.user?.accessToken ||
        auth?.user?.token ||
        (axios.defaults.headers.common?.Authorization || '').replace('Bearer ', '') ||
        null;

    const isEditing = !!animal;

    const [formData, setFormData] = useState({
        nombre: '',
        descripcionPersonalidad: '',
        historialMedico: '',
        razaId: '',
        generoId: '',
        estadoAdopcionId: 1,
        tamanoId: '',
        nivelEnergiaId: '',
        fechaNacimientoAprox: new Date().toISOString().slice(0, 10),
        fechaIngresoRefugio: new Date().toISOString().slice(0, 10),
        compatibleNiños: true,
        compatibleOtrasMascotas: true,
        estaVacunado: true,
        estaEsterilizado: true,
        temperamentosNombres: [],
        fotosUrls: [""],
        fotoPrincipalIndex: 0
    });

    const [lookups, setLookups] = useState({});
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [formStatus, setFormStatus] = useState(null);

    // Cargar lookups
    useEffect(() => {
        const loadLookups = async () => {
            try {
                const [
                    razas, generos, estadosAdopcion,
                    tamanos, nivelesEnergia, temperamentos
                ] = await Promise.all([
                    getRazas(token),
                    getGeneros(token),
                    getEstadosAdopcion(token),
                    getTamanos(token),
                    getNivelesEnergia(token),
                    getTemperamentos(token)
                ]);

                setLookups({
                    razas: razas.map(r => ({ id: r.id, nombre: r.nombreRaza })),
                    generos,
                    estadosAdopcion,
                    tamanos,
                    nivelesEnergia,
                    temperamentos: temperamentos.map(t => ({
                        id: t.id,
                        nombre: t.nombreTemperamento || t.nombre_temperamento || t.nombre
                    }))
                });
                console.log("Temperamentos LOOKUP NORMALIZADOS:", temperamentos);


            } catch (err) {
                console.error("Error cargando lookups:", err);
                setError("No se pudieron cargar los datos.");
            }
        };

        if (token) loadLookups();
    }, [token]);

    // Cargar datos si es edición
    useEffect(() => {
        if (isEditing && animal && lookups.temperamentos?.length > 0) {
            setFormData(prev => ({
                ...prev,
                nombre: animal.nombre || '',
                descripcionPersonalidad: animal.descripcionPersonalidad || '',
                historialMedico: animal.historialMedico || '',
                razaId: animal.raza?.id || '',
                generoId: animal.genero?.id || '',
                estadoAdopcionId: animal.estadoAdopcion?.id || 1,
                tamanoId: animal.tamano?.id || '',
                nivelEnergiaId: animal.nivelEnergia?.id || '',
                fechaNacimientoAprox: animal.fechaNacimientoAprox?.split("T")[0],
                fechaIngresoRefugio: animal.fechaIngresoRefugio?.split("T")[0],
                compatibleNiños: animal.compatibleNiños ?? true,
                compatibleOtrasMascotas: animal.compatibleOtrasMascotas ?? true,
                estaVacunado: animal.estaVacunado ?? true,
                estaEsterilizado: animal.estaEsterilizado ?? true,
                temperamentosNombres: animal.temperamentos?.map(
                    t => t.nombreTemperamento ?? t.nombre_temperamento ?? t.nombre
                ).filter(Boolean) || [],
                fotosUrls: animal.fotos?.map(f => f.urlFoto) || [""],
                fotoPrincipalIndex: animal.fotos?.findIndex(f => f.esPrincipal) ?? 0
            }));
        }
    }, [animal, isEditing, lookups.temperamentos]);

    // Manejo de inputs
    const handleChange = e => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]:
                type === "checkbox"
                    ? checked
                    : name.endsWith("Id")
                        ? parseInt(value)
                        : value
        }));
    };

    const handleTemperamentoChange = e => {
        const t = e.target.value;
        setFormData(prev => ({
            ...prev,
            temperamentosNombres: prev.temperamentosNombres.includes(t)
                ? prev.temperamentosNombres.filter(x => x !== t)
                : [...prev.temperamentosNombres, t]
        }));
    };


    const handleFotoChange = (index, url) => {
        const copy = [...formData.fotosUrls];
        copy[index] = url;
        setFormData(prev => ({ ...prev, fotosUrls: copy }));
    };

    const handleAddFoto = () => {
        setFormData(prev => ({
            ...prev,
            fotosUrls: [...prev.fotosUrls, ""]
        }));
    };

    const handleRemoveFoto = index => {
        const updated = formData.fotosUrls.filter((_, i) => i !== index);
        setFormData(prev => ({
            ...prev,
            fotosUrls: updated,
            fotoPrincipalIndex:
                index === prev.fotoPrincipalIndex
                    ? 0
                    : prev.fotoPrincipalIndex - (index < prev.fotoPrincipalIndex ? 1 : 0)
        }));
    };

    // VALIDACIÓN DE IDS
    const validateIds = () => {
        const errors = [];
        if (!lookups.razas?.some(r => r.id === formData.razaId)) errors.push("Raza inválida");
        if (!lookups.generos?.some(g => g.id === formData.generoId)) errors.push("Género inválido");
        if (!lookups.estadosAdopcion?.some(e => e.id === formData.estadoAdopcionId)) errors.push("Estado de adopción inválido");
        if (!lookups.tamanos?.some(t => t.id === formData.tamanoId)) errors.push("Tamaño inválido");
        if (!lookups.nivelesEnergia?.some(n => n.id === formData.nivelEnergiaId)) errors.push("Nivel de energía inválido");

        const validTempNames = (lookups.temperamentos || []).map(t => t.nombre);
        console.log("temperamentosNombres a validar:", formData.temperamentosNombres);
        console.log("validTempNames:", validTempNames);

        const invalidTemps = formData.temperamentosNombres.filter(
            t => !validTempNames.includes(t)
        );


        if (invalidTemps.length > 0) errors.push("Temperamentos inválidos: " + invalidTemps.join(", "));
        return errors;
    };

    // Envío del formulario
    const handleSubmit = async e => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setFormStatus(null);

        if (!token) {
            setError("Token no disponible.");
            setLoading(false);
            return;
        }

        const idErrors = validateIds();
        if (idErrors.length > 0) {
            setError("Error en los datos: " + idErrors.join("; "));
            setLoading(false);
            return;
        }

        axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
        const temperamentosIds = formData.temperamentosNombres
            .map(nombre => lookups.temperamentos.find(t => t.nombre === nombre)?.id)
            .filter(Boolean);

        const dataToSend = {
            nombre: formData.nombre,
            descripcionPersonalidad: formData.descripcionPersonalidad,
            historialMedico: formData.historialMedico,
            razaId: formData.razaId,
            generoId: formData.generoId,
            estadoAdopcionId: formData.estadoAdopcionId,
            tamanoId: formData.tamanoId,
            nivelEnergiaId: formData.nivelEnergiaId,
            fechaNacimientoAprox: formData.fechaNacimientoAprox,
            fechaIngresoRefugio: formData.fechaIngresoRefugio,
            compatibleNiños: formData.compatibleNiños,
            compatibleOtrasMascotas: formData.compatibleOtrasMascotas,
            estaVacunado: formData.estaVacunado,
            estaEsterilizado: formData.estaEsterilizado,
            temperamentosIds,      // <- aquí van los IDs
            fotosUrls: formData.fotosUrls,
            fotoPrincipalIndex: formData.fotoPrincipalIndex
        };
        console.log("Enviando datos al backend:", dataToSend);



        try {
            if (isEditing) {
                if (!animal?.id) {
                    setError("ID del animal no disponible para edición.");
                    setLoading(false);
                    return;
                }
                await updateAnimal(animal.id, dataToSend, token);
                setFormStatus("Animal actualizado exitosamente.");
            } else {
                await createAnimal(dataToSend, token);
                setFormStatus("Animal registrado exitosamente.");
            }
            setTimeout(onSuccess, 600);

        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || "Error al guardar.");
        }

        setLoading(false);
    };

    // Render select helper
    const renderSelect = (lookup, name, label) => (
        <div className="relative">
            <label className="block text-sm font-medium mb-1 text-gray-700">{label}</label>
            <select
                name={name}
                value={formData[name] || ""}
                onChange={handleChange}
                className="w-full border rounded-lg p-2"
                required
            >
                <option value="" disabled>Seleccionar {label}</option>
                {lookup?.map(item => (
                    <option key={item.id} value={item.id}>{item.nombre}</option>
                ))}
            </select>
        </div>
    );

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center p-4">
            <div className="bg-white rounded-xl shadow-xl w-full max-w-4xl max-h-[90vh] overflow-y-auto">
                <div className="flex justify-between items-center p-6 border-b">
                    <h2 className="text-2xl font-bold flex items-center text-[#316B7A]">
                        <PawPrint className="w-6 h-6 mr-2 text-[#FDB2A0]" />
                        {isEditing ? "Editar Mascota" : "Registrar Nueva Mascota"}
                    </h2>
                    <button onClick={onClose}><X className="w-6 h-6 text-gray-500" /></button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-6">
                    {error && <div className="bg-red-100 p-3 rounded text-red-700">{error}</div>}
                    {formStatus && <div className="bg-green-100 p-3 rounded text-green-700">{formStatus}</div>}

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 p-4 bg-[#FFF7E6] rounded-lg border">
                        <h3 className="md:col-span-2 font-semibold text-xl text-[#316B7A]">Datos Principales</h3>

                        <div>
                            <label className="text-sm block mb-1 text-gray-700">Nombre</label>
                            <input
                                type="text"
                                name="nombre"
                                value={formData.nombre}
                                onChange={handleChange}
                                className="w-full border p-2 rounded"
                                required
                            />
                        </div>

                        {renderSelect(lookups.razas, "razaId", "Raza")}
                        {renderSelect(lookups.generos, "generoId", "Género")}
                        {renderSelect(lookups.estadosAdopcion, "estadoAdopcionId", "Estado de Adopción")}
                        {renderSelect(lookups.tamanos, "tamanoId", "Tamaño")}
                        {renderSelect(lookups.nivelesEnergia, "nivelEnergiaId", "Nivel de Energía")}

                        <div>
                            <label className="text-sm block mb-1 text-gray-700">Fecha Nacimiento</label>
                            <input type="date" name="fechaNacimientoAprox" value={formData.fechaNacimientoAprox} onChange={handleChange} className="w-full border p-2 rounded" required />
                        </div>

                        <div>
                            <label className="text-sm block mb-1 text-gray-700">Fecha Ingreso</label>
                            <input type="date" name="fechaIngresoRefugio" value={formData.fechaIngresoRefugio} onChange={handleChange} className="w-full border p-2 rounded" required />
                        </div>
                    </div>

                    <div className="p-4 bg-[#FFF7E6] border rounded-lg">
                        <h3 className="text-xl font-semibold text-[#316B7A]">Personalidad & Compatibilidad</h3>
                        <label className="block mt-2 text-sm">Descripción</label>
                        <textarea name="descripcionPersonalidad" value={formData.descripcionPersonalidad} onChange={handleChange} className="w-full border p-2 rounded" />
                        <label className="block mt-2 text-sm">Historial Médico</label>
                        <textarea name="historialMedico" value={formData.historialMedico} onChange={handleChange} className="w-full border p-2 rounded" />
                        <div className="flex gap-4 mt-3">
                            <label><input type="checkbox" name="compatibleNiños" checked={formData.compatibleNiños} onChange={handleChange} /> Niños</label>
                            <label><input type="checkbox" name="compatibleOtrasMascotas" checked={formData.compatibleOtrasMascotas} onChange={handleChange} /> Otras Mascotas</label>
                            <label><input type="checkbox" name="estaVacunado" checked={formData.estaVacunado} onChange={handleChange} /> Vacunado</label>
                            <label><input type="checkbox" name="estaEsterilizado" checked={formData.estaEsterilizado} onChange={handleChange} /> Esterilizado</label>
                        </div>

                        <label className="block mt-3 text-sm">Temperamentos</label>
                        {lookups.temperamentos?.map(t => (
                            <label key={t.id} className="border px-2 py-1 rounded inline-flex items-center gap-2">
                                <input
                                    type="checkbox"
                                    value={t.nombre}  // esto coincide con lo que validas
                                    checked={formData.temperamentosNombres.includes(t.nombre)}
                                    onChange={handleTemperamentoChange}
                                />
                                {t.nombre}  {/* Mostrar el nombre correctamente */}
                            </label>
                        ))}
                    </div>

                    <div className="p-4 bg-[#FFF7E6] border rounded-lg">
                        <h3 className="text-xl font-semibold text-[#316B7A]">Fotos</h3>
                        {formData.fotosUrls.map((url, i) => (
                            <div key={i} className="flex items-center gap-2 mt-2">
                                <input className="flex-1 border p-2 rounded"
                                    value={url || ""}
                                    onChange={e => handleFotoChange(i, e.target.value)}
                                    placeholder="URL de foto" />
                                <button type="button" onClick={() => handleRemoveFoto(i)} className="bg-[#93C5FD] text-black px-3 py-1 rounded hover:bg-[#60A5FA] transition">
                                    Eliminar
                                </button>

                                <label className="flex items-center gap-1">
                                    <input type="radio" name="fotoPrincipal" checked={formData.fotoPrincipalIndex === i} onChange={() => setFormData(prev => ({ ...prev, fotoPrincipalIndex: i }))} />
                                    Principal
                                </label>
                            </div>
                        ))}
                        <button type="button" onClick={handleAddFoto} className="mt-3 bg-[#FDB2A0] text-black px-3 py-2 rounded hover:bg-[#fca18b] transition">
                            Agregar Foto
                        </button>

                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-[#316B7A] text-white py-3 rounded-full font-bold hover:bg-[#214e58]"
                    >
                        <Save className="inline-block w-5 h-5 mr-2" />
                        {isEditing ? "Guardar Cambios" : "Registrar Animal"}
                    </button>
                </form>
            </div>
        </div>
    );
}
