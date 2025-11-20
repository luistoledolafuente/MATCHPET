import React, { useState, useEffect } from 'react';
import { PawPrint, PlusCircle, Pencil, Trash2, Loader2, XCircle } from 'lucide-react';
import { getMisAnimales, deleteAnimal } from '../../services/animalService';
import AnimalForm from "../../components/refugio/animalForm";
import { useAuth } from '../../contexts/AuthContext';

export default function MisMascotas() {
  const { isAuthenticated, token, loading: authLoading } = useAuth();
  const [animals, setAnimals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingAnimal, setEditingAnimal] = useState(null);

  const fetchAnimals = async () => {
    if (authLoading || !token) {
      if (!token && !authLoading) {
        setError("Debes iniciar sesión como Refugio para ver tus mascotas.");
      }
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const data = await getMisAnimales(token);
      setAnimals(data);
    } catch (err) {
      console.error("Error al cargar animales:", err);
      const errMsg = err.response?.data?.message || err.message || "Error de conexión o permisos.";

      if (err.response?.status === 401) {
        setError("Sesión expirada o permisos insuficientes. Por favor, vuelve a iniciar sesión.");
      } else {
        setError(`Error al cargar animales: ${errMsg}`);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (animalId) => {
    if (!window.confirm("¿Estás seguro de que quieres borrar este animal? Esta acción es irreversible.")) return;

    try {
      await deleteAnimal(animalId, token);
      setAnimals(prev => prev.filter(a => a.animal_id !== animalId));
      console.log("Animal eliminado exitosamente.");
    } catch (err) {
      console.error("Error al eliminar animal:", err);
      const errMsg = err.response?.data?.message || "Error al eliminar el animal.";
      setError(`Error al eliminar: ${errMsg}`);
    }
  };

  const handleFormSubmitSuccess = () => {
    setIsFormOpen(false);
    setEditingAnimal(null);
    fetchAnimals();
  };

  useEffect(() => {
    if (token && !authLoading) {
      fetchAnimals();
    } else if (!authLoading && !token) {
      setLoading(false);
    }
  }, [token, authLoading]);

  return (
    <div className="bg-[#FFF7E6] min-h-screen p-8 font-sans">
      <div className="max-w-7xl mx-auto space-y-8">
        <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center pb-4 border-b border-[#FDB2A0]">
          <h1 className="text-4xl font-extrabold text-[#316B7A] flex items-center mb-4 sm:mb-0">
            <PawPrint className="w-8 h-8 mr-3 text-[#FDB2A0]" />
            Mis Mascotas en Adopción
          </h1>
          <button
            onClick={() => { setEditingAnimal(null); setIsFormOpen(true); }}
            className="flex items-center bg-[#FDB2A0] text-[#316B7A] font-bold py-2 px-4 rounded-full shadow-lg hover:bg-[#ffc3b3] transition duration-200"
            disabled={!isAuthenticated || authLoading}
          >
            <PlusCircle className="w-5 h-5 mr-2" />
            Agregar Nuevo Animal
          </button>
        </header>

        {(loading || authLoading) && (
          <div className="text-center p-8 text-[#316B7A]">
            <Loader2 className="w-8 h-8 animate-spin mx-auto mb-3" />
            Cargando listado de animales...
          </div>
        )}

        {error && (
          <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded-lg flex items-center">
            <XCircle className="w-5 h-5 mr-3" />
            {error}
          </div>
        )}

        {!loading && !authLoading && animals.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {animals.map((animal, index) => (
              <div
                key={animal.animal_id ?? `temp-${index}`}
                className="bg-white rounded-xl shadow-lg overflow-hidden transition-all hover:shadow-2xl"
              >
                <img
                  src={animal.fotos?.[0] || "https://placehold.co/400x300/a8d8e0/316B7A?text=No+Photo"}
                  alt={`Foto de ${animal.nombre}`}
                  className="w-full h-48 object-cover"
                  onError={(e) => { e.target.onerror = null; e.target.src = "https://placehold.co/400x300/a8d8e0/316B7A?text=No+Photo"; }}
                />

                <div className="p-4">
                  <h2 className="text-2xl font-bold text-[#316B7A] mb-1">{animal.nombre}</h2>
                  <p className="text-sm text-gray-500 mb-3">
                    {animal.raza || 'Raza desconocida'} ({animal.genero || 'Género Desconocido'})
                  </p>
                  <p className="text-sm text-gray-700">
                    <span className="font-semibold">Estado:</span> {animal.estadoAdopcion || 'N/A'}
                  </p>

                  <div className="flex justify-end space-x-2 pt-4 border-t mt-4">
                    <button
                      onClick={() => { setEditingAnimal(animal); setIsFormOpen(true); }}
                      className="text-[#316B7A] hover:text-[#FDB2A0] transition-colors p-2 rounded-full"
                      title="Editar Animal"
                    >
                      <Pencil className="w-5 h-5" />
                    </button>
                    <button
                      onClick={() => handleDelete(animal.animal_id)}
                      className="text-red-500 hover:text-red-700 transition-colors p-2 rounded-full"
                      title="Eliminar Animal"
                    >
                      <Trash2 className="w-5 h-5" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {!loading && !authLoading && !error && animals.length === 0 && isAuthenticated && (
          <div className="text-center p-16 bg-white rounded-2xl shadow-xl">
            <PawPrint className="w-12 h-12 mx-auto text-[#316B7A] opacity-50 mb-4" />
            <p className="text-xl text-gray-600">Aún no hay mascotas registradas. ¡Usa el botón "Agregar Nuevo Animal" para empezar!</p>
          </div>
        )}

        {!loading && !authLoading && !isAuthenticated && !error && (
          <div className="text-center p-16 bg-white rounded-2xl shadow-xl">
            <XCircle className="w-12 h-12 mx-auto text-red-400 mb-4" />
            <p className="text-xl text-gray-600">Por favor, inicia sesión como Refugio para gestionar tus mascotas.</p>
          </div>
        )}
      </div>

      {isFormOpen && (
        <AnimalForm
          animal={editingAnimal}
          onClose={() => { setIsFormOpen(false); setEditingAnimal(null); }}
          onSuccess={handleFormSubmitSuccess}
          token={token}
        />
      )}
    </div>
  );
}
