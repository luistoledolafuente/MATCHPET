import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import animalService from '../../services/animalService';
import { Loader2, XCircle, Zap, Heart, Info } from 'lucide-react';

const BACKEND_BASE_URL = "http://127.0.0.1:8081"; // Ajusta esta URL si es necesario

const RecomendacionesPage = () => {
    const { token, isAuthenticated } = useAuth();
    const [recomendaciones, setRecomendaciones] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchRecomendaciones = async () => {
            setLoading(true);
            setError(null);
            
            if (!token || !isAuthenticated) {
                // Esto debería ser manejado por ProtectedRoute, pero es un buen fallback
                setError("Debes iniciar sesión como Adoptante para ver tus recomendaciones.");
                setLoading(false);
                return;
            }

            try {
                // Llamada al endpoint: GET /api/animales/recomendados
                const data = await animalService.getRecomendaciones(token);
                setRecomendaciones(data);
            } catch (err) {
                console.error("Error al cargar recomendaciones:", err);
                const errMsg = err.response?.data?.message || err.message || "Error al obtener las recomendaciones. Asegúrate de tener tu perfil de Adoptante completo.";
                setError(errMsg);
            } finally {
                setLoading(false);
            }
        };

        fetchRecomendaciones();
    }, [token, isAuthenticated]);

    // Lógica del Modal de Solicitud (simplemente un placeholder para el botón)
    const handleSolicitar = (animalId, nombre) => {
        // Aquí iría la lógica para abrir el modal de solicitud de adopción,
        // similar a como lo implementaste en MascotasPage.jsx
        alert(`Preparando solicitud para ${nombre} (ID: ${animalId}).`);
    };


    if (loading) {
        return (
            <div className="text-center py-20 text-indigo-600">
                <Loader2 className="w-8 h-8 animate-spin mx-auto mb-3" />
                Buscando tu match perfecto con la IA...
            </div>
        );
    }

    if (error) {
        return (
             <div className="text-center py-10 bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded-lg flex items-center justify-center">
                <XCircle className="w-5 h-5 mr-3" />
                {error}
            </div>
        );
    }

    return (
        <div className="p-6 max-w-7xl mx-auto">
            <h1 className="text-4xl font-extrabold mb-8 text-indigo-700 flex items-center">
                <Zap className="w-8 h-8 mr-3 text-yellow-500" />
                Tu Match Perfecto (Recomendaciones IA)
            </h1>
            
            <div className="bg-indigo-50 border-l-4 border-indigo-400 text-indigo-800 p-4 mb-6 rounded-lg flex items-start">
                <Info className="w-5 h-5 mt-1 mr-3 flex-shrink-0" />
                <p className="text-sm">Estas mascotas han sido seleccionadas por nuestra Inteligencia Artificial basándose en tu perfil de Adoptante, estilo de vida y preferencias de vivienda. ¡Tu compañero ideal podr\u00EDa estar aqu\u00ED!</p>
            </div>


            {recomendaciones.length === 0 ? (
                <div className="text-center p-10 bg-gray-100 rounded-xl border-2 border-dashed border-gray-300">
                    <Heart className="w-12 h-12 mx-auto text-red-400 mb-4" />
                    <p className="font-bold text-xl text-gray-700">No encontramos coincidencias perfectas en este momento.</p>
                    <p className="text-gray-600 mt-2">Aseg\u00FArate de que tu perfil est\u00E9 completamente actualizado para mejorar los resultados.</p>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {recomendaciones.map((animal) => (
                        <div key={animal.animal_id} className="bg-white rounded-xl shadow-lg overflow-hidden border border-indigo-200 hover:shadow-2xl transition duration-300">
                            <img
                                src={
                                    animal.fotos?.[0]
                                        ? `${BACKEND_BASE_URL}${animal.fotos[0]}`
                                        : "https://placehold.co/400x300/a8d8e0/316B7A?text=Match+IA"
                                }
                                alt={`Foto de ${animal.nombre}`}
                                className="w-full h-48 object-cover"
                            />
                            <div className="p-4">
                                <h2 className="text-2xl font-bold text-indigo-700 mb-1">{animal.nombre}</h2>
                                <p className="text-sm text-gray-600 mb-3">
                                    {animal.especie} - {animal.raza}
                                </p>
                                
                                <div className="space-y-1 text-sm text-gray-700">
                                    <p><span className="font-semibold">Energ\u00EDa:</span> {animal.nivelEnergia}</p>
                                    <p><span className="font-semibold">Tama\u00F1o:</span> {animal.tamano}</p>
                                    <p><span className="font-semibold">Compatible con Ni\u00F1os:</span> {animal.compatibleNiños ? 'Sí' : 'No'}</p>
                                    <p><span className="font-semibold">Refugio:</span> {animal.refugioCiudad}</p>
                                </div>

                                <div className="mt-4">
                                    <button 
                                        onClick={() => handleSolicitar(animal.animal_id, animal.nombre)}
                                        className="w-full bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-2 rounded-lg transition"
                                        disabled={animal.estadoAdopcion !== 'Disponible'}
                                    >
                                        {animal.estadoAdopcion !== 'Disponible' ? 'No Disponible' : 'Solicitar Adopción'}
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default RecomendacionesPage;