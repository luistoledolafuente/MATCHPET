import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getAnimalById } from '../../services/animalService';
import { Loader2, ArrowLeft, MapPin, Heart } from 'lucide-react';

const BACKEND_BASE_URL = "http://127.0.0.1:8081";

export default function DetalleMascota() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [animal, setAnimal] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedImage, setSelectedImage] = useState(0);

  useEffect(() => {
    const loadAnimal = async () => {
      try {
        const data = await getAnimalById(id);
        setAnimal(data);
      } catch (error) {
        console.error("Error al cargar animal", error);
      } finally {
        setLoading(false);
      }
    };
    loadAnimal();
  }, [id]);

  if (loading)
    return (
      <div className="flex justify-center items-center min-h-screen bg-[#FFF7E6]">
        <Loader2 className="animate-spin w-12 h-12 text-[#316B7A]" />
      </div>
    );
  if (!animal)
    return <div className="text-center p-10">Mascota no encontrada</div>;

  const imagenes =
    animal.fotos && animal.fotos.length > 0
      ? animal.fotos.map((url) => `${BACKEND_BASE_URL}${url}`)
      : ["https://placehold.co/600x400/e2e8f0/cbd5e1?text=Sin+Foto"];

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#FFF7E6] to-[#EAF7FD] flex items-center justify-center p-4">
      {/* Card centrada */}
      <div className="max-w-5xl w-full bg-white rounded-2xl shadow-2xl border-4 border-gray-300 overflow-hidden">
        {/* Botón Volver */}
        <div className="p-4 border-b border-gray-100 flex items-center">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center text-gray-600 hover:text-[#316B7A] transition"
          >
            <ArrowLeft className="w-5 h-5 mr-2" /> Volver
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2">
          {/* Imagen */}
          <div className="p-6 bg-gray-50">
            <div className="aspect-w-4 aspect-h-3 mb-4 rounded-xl overflow-hidden shadow-lg border-4 border-white">
              <img
                src={imagenes[selectedImage]}
                alt={animal.nombre}
                className="w-full h-96 object-cover"
              />
            </div>
            <div className="flex gap-2 overflow-x-auto pb-2">
              {imagenes.map((img, idx) => (
                <button
                  key={idx}
                  onClick={() => setSelectedImage(idx)}
                  className={`w-20 h-20 flex-shrink-0 rounded-lg overflow-hidden border-2 transition ${
                    selectedImage === idx
                      ? "border-[#316B7A]"
                      : "border-transparent"
                  }`}
                >
                  <img src={img} alt="" className="w-full h-full object-cover" />
                </button>
              ))}
            </div>
          </div>

          {/* Información */}
          <div className="p-8 flex flex-col">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h1 className="text-4xl font-bold text-[#316B7A] mb-2">
                  {animal.nombre}
                </h1>
                <p className="text-lg text-gray-500 font-medium flex items-center">
                  <MapPin className="w-5 h-5 mr-1" /> {animal.refugioNombre}
                </p>
              </div>
              <span
                className={`px-4 py-2 rounded-full font-bold text-sm ${
                  animal.estadoAdopcion === "Disponible"
                    ? "bg-green-100 text-green-700"
                    : "bg-orange-100 text-orange-700"
                }`}
              >
                {animal.estadoAdopcion}
              </span>
            </div>

            {/* Datos Clave */}
            <div className="grid grid-cols-2 gap-4 mb-6">
              <div className="bg-blue-50 p-3 rounded-lg">
                <p className="text-xs text-blue-600 uppercase font-bold">Raza</p>
                <p className="font-semibold text-gray-800">{animal.raza}</p>
              </div>
              <div className="bg-purple-50 p-3 rounded-lg">
                <p className="text-xs text-purple-600 uppercase font-bold">
                  Edad Aprox.
                </p>
                <p className="font-semibold text-gray-800">
                  {animal.edad || "Desconocida"}
                </p>
              </div>
              <div className="bg-pink-50 p-3 rounded-lg">
                <p className="text-xs text-pink-600 uppercase font-bold">Género</p>
                <p className="font-semibold text-gray-800">{animal.genero}</p>
              </div>
              <div className="bg-yellow-50 p-3 rounded-lg">
                <p className="text-xs text-yellow-600 uppercase font-bold">Tamaño</p>
                <p className="font-semibold text-gray-800">{animal.tamano}</p>
              </div>
            </div>

            {/* Descripción */}
            <div className="mb-6">
              <h3 className="text-lg font-bold text-[#316B7A] mb-2">
                Sobre {animal.nombre}
              </h3>
              <p className="text-gray-600 leading-relaxed">
                {animal.descripcionPersonalidad || "Sin descripción disponible."}
              </p>
            </div>

            {/* Características */}
            <div className="mb-8">
              <h3 className="text-lg font-bold text-[#316B7A] mb-2">
                Características
              </h3>
              <div className="flex flex-wrap gap-2">
                {animal.estaVacunado && (
                  <span className="bg-teal-100 text-teal-800 px-3 py-1 rounded-full text-sm">
                    💉 Vacunado
                  </span>
                )}
                {animal.estaEsterilizado && (
                  <span className="bg-indigo-100 text-indigo-800 px-3 py-1 rounded-full text-sm">
                    🩺 Esterilizado
                  </span>
                )}
                {animal.compatibleNiños && (
                  <span className="bg-yellow-100 text-yellow-800 px-3 py-1 rounded-full text-sm">
                    👶 Compatible con niños
                  </span>
                )}
                {animal.compatibleOtrasMascotas && (
                  <span className="bg-orange-100 text-orange-800 px-3 py-1 rounded-full text-sm">
                    🐶 Amigable con mascotas
                  </span>
                )}
              </div>
            </div>

            <button
              className="w-full bg-[#316B7A] text-white py-4 rounded-xl font-bold text-lg hover:bg-[#25525d] transition shadow-lg flex justify-center items-center mt-auto"
              onClick={() => navigate("/dashboard/adoptante/mascotas")}
            >
              <Heart className="w-6 h-6 mr-2" /> ¡Quiero Adoptarlo!
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
