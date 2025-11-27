import React, { useState, useEffect } from 'react';
import { PawPrint, PlusCircle, Pencil, Trash2, Loader2, XCircle } from 'lucide-react';
import { getMisAnimales, deleteAnimal } from '../../services/animalService';
import AnimalForm from "../../components/refugio/animalForm";
import { useAuth } from '../../contexts/AuthContext';
import { getRazas } from '../../services/lookupsService';

const BACKEND_BASE_URL = "http://localhost:8081";

export default function MisMascotas() {
  const { isAuthenticated, token, loading: authLoading } = useAuth();
  const [animals, setAnimals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingAnimal, setEditingAnimal] = useState(null);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [animalToDelete, setAnimalToDelete] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [razas, setRazas] = useState([]);

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

  const fetchRazas = async () => {
    if (!token) return;
    try {
      const data = await getRazas(token);
      setRazas(data);
    } catch (err) {
      console.error("Error al cargar razas:", err);
    }
  };

  const handleFormSubmitSuccess = () => {
    setIsFormOpen(false);
    setEditingAnimal(null);
    fetchAnimals();
  };

  useEffect(() => {
    fetchRazas();
  }, [token]);

  useEffect(() => {
    fetchAnimals();
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
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {animals.map((animal, index) => {
              const razaNombre = razas.find(r => r.id === animal.razaId)?.nombreRaza || "Raza desconocida";
              return (
                <div
                  key={animal.animal_id ?? `temp-${index}`}
                  className="bg-white rounded-2xl shadow-md overflow-hidden cursor-pointer transition-shadow hover:shadow-lg"
                >
                  <div className="relative">
                    <img
                      src={
                        (animal.fotos?.[0] && animal.fotos[0].startsWith('/'))
                          ? `${BACKEND_BASE_URL}${animal.fotos[0]}`
                          : animal.fotos?.[0] || "https://placehold.co/400x300/a8d8e0/316B7A?text=No+Photo"
                      }
                      alt={`Foto de ${animal.nombre}`}
                      className="w-full h-48 object-cover rounded-t-2xl"
                      // Mantén el onError por si acaso
                      onError={(e) => { e.target.onerror = null; e.target.src = "https://placehold.co/400x300/a8d8e0/316B7A?text=No+Photo"; }}
                    />
                    <span
                      className={`absolute top-3 right-3 px-3 py-1 rounded-full text-xs font-semibold
                        ${animal.estadoAdopcion === "Aprobada" ? "bg-green-100 text-green-800" :
                          animal.estadoAdopcion === "En Revisión" ? "bg-blue-100 text-blue-800" :
                            "bg-red-100 text-red-800"
                        }
                      `}
                    >
                      {animal.estadoAdopcion || "Sin estado"}
                    </span>
                  </div>

                  <div className="p-5">
                    <h3 className="text-lg font-semibold text-[#316B7A]">{animal.nombre}</h3>
                    <p className="text-sm text-gray-600 mb-1">{animal.refugio || "Refugio desconocido"}</p>

                    <div className="flex flex-wrap items-center gap-2 text-xs text-gray-500 mb-4">
                      <span className="flex items-center gap-1">
                        <PawPrint className="w-4 h-4" />
                        {animal.raza}
                      </span>
                      <span className="flex items-center gap-1">
                        🕒 {animal.tamano}
                      </span>
                      <span className="flex items-center gap-1">
                        {animal.genero === "Macho" ? "♂ Macho" : animal.genero === "Hembra" ? "♀ Hembra" : "Género desconocido"}
                      </span>
                    </div>

                    <div className="flex gap-3">
                      <button
                        onClick={() => { setEditingAnimal({ ...animal, id: animal.animal_id }); setIsFormOpen(true); }}
                        className="flex-1 bg-[#316B7A] text-white py-2 rounded-lg hover:bg-[#264f5b] transition"
                      >
                        Editar
                      </button>
                      <button
                        onClick={() => { setAnimalToDelete(animal); setDeleteModalOpen(true); }}
                        className="flex-1 bg-[#E3E3E3] text-[#316B7A] py-2 rounded-lg hover:bg-[#d0d0d0] transition"
                      >
                        Eliminar
                      </button>
                    </div>
                  </div>
                </div>
              )
            })}
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

      {/* Modal de confirmación de eliminación */}
      {deleteModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-2xl shadow-lg max-w-lg text-center">
            <h2 className="text-xl font-bold mb-4 text-[#316B7A]">Confirmar eliminación</h2>
            <p className="mb-6 text-gray-700">
              ¿Estás seguro de que quieres borrar a la mascota <strong>{animalToDelete?.nombre}</strong>? Esta acción es irreversible.
            </p>
            <div className="flex justify-center space-x-4">
              <button
                onClick={() => setDeleteModalOpen(false)}
                className="px-4 py-2 rounded-full bg-[#FFF7E6] text-[#316B7A] hover:bg-[#fde9c8] transition"
              >
                Cancelar
              </button>
              <button
                onClick={async () => {
                  try {
                    await deleteAnimal(animalToDelete.animal_id, token);
                    setAnimals(prev => prev.filter(a => a.animal_id !== animalToDelete.animal_id));
                    setSuccessMessage("Animal eliminado exitosamente.");
                  } catch (err) {
                    const errMsg = err.response?.data?.message || "Error al eliminar el animal.";
                    setError(`Error al eliminar: ${errMsg}`);
                  } finally {
                    setDeleteModalOpen(false);
                    setAnimalToDelete(null);
                  }
                }}
                className="px-4 py-2 rounded-full bg-[#f79b87] text-white hover:bg-[#e06e5a] transition"
              >
                Eliminar
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Mensaje de éxito */}
      {successMessage && (
        <div className="fixed top-5 right-5 bg-green-500 text-white p-4 rounded-xl shadow-lg z-50 animate-fade-in-out">
          {successMessage}
        </div>
      )}

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
