import React, { useState, useEffect } from "react";
import axios from "axios";
import { Loader2, XCircle, PawPrint } from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";

const API_URL = "http://127.0.0.1:8081/api";

export default function MascotasPage() {
  const { token, isAuthenticated, loading: authLoading } = useAuth();
  const [perfil, setPerfil] = useState(null);
  const [mascotas, setMascotas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Obtener perfil de adoptante
  const fetchPerfil = async () => {
    if (!token) return;
    try {
      const res = await axios.get(`${API_URL}/user/profile`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setPerfil(res.data);
      return res.data;
    } catch (err) {
      console.error("Error al obtener perfil:", err);
      setError("No se pudo obtener el perfil del adoptante.");
      setLoading(false);
    }
  };

  // Obtener mascotas recomendadas
  const fetchMascotas = async (perfil) => {
    if (!token) return;
    setLoading(true);
    setError(null);
    try {
      const res = await axios.get(`${API_URL}/animales/recomendados`, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      setMascotas(res.data);
    } catch (err) {
      console.error("Error al cargar animales:", err);
      const errMsg = err.response?.data?.message || err.message || "Error de conexión.";
      setError(`No se pudieron cargar las mascotas: ${errMsg}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!authLoading && isAuthenticated && token) {
      (async () => {
        const perfilData = await fetchPerfil();
        if (perfilData) {
          await fetchMascotas(perfilData);
        }
      })();
    } else if (!authLoading && !isAuthenticated) {
      setLoading(false);
    }
  }, [token, authLoading, isAuthenticated]);

  return (
    <div className="bg-[#FFF7E6] min-h-screen p-8 font-sans">
      <div className="max-w-7xl mx-auto space-y-8">
        <h1 className="text-3xl font-bold text-[#316B7A] flex items-center mb-4">
          <PawPrint className="w-8 h-8 mr-3 text-[#FDB2A0]" />
          Explorar Mascotas Recomendadas
        </h1>

        {(loading || authLoading) && (
          <div className="text-center p-8 text-[#316B7A]">
            <Loader2 className="w-8 h-8 animate-spin mx-auto mb-3" />
            Cargando mascotas...
          </div>
        )}

        {error && (
          <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded-lg flex items-center">
            <XCircle className="w-5 h-5 mr-3" />
            {error}
          </div>
        )}

        {!loading && !error && mascotas.length === 0 && (
          <div className="text-center p-16 bg-white rounded-2xl shadow-xl">
            <PawPrint className="w-12 h-12 mx-auto text-[#316B7A] opacity-50 mb-4" />
            <p className="text-xl text-gray-600">
              No hay mascotas recomendadas aún. Asegúrate de completar tu perfil.
            </p>
          </div>
        )}

        {!loading && !error && mascotas.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {mascotas.map((animal) => (
              <div key={animal.animal_id} className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-2xl transition">
                <img
                  src={animal.fotos?.[0] || "https://placehold.co/400x300/a8d8e0/316B7A?text=No+Photo"}
                  alt={`Foto de ${animal.nombre}`}
                  className="w-full h-48 object-cover"
                />
                <div className="p-4">
                  <h2 className="text-2xl font-bold text-[#316B7A] mb-1">{animal.nombre}</h2>
                  <p className="text-sm text-gray-500 mb-1">{animal.raza || "Raza desconocida"} ({animal.genero || "Género desconocido"})</p>
                  <p className="text-sm text-gray-700">{animal.descripcionPersonalidad || "Sin descripción"}</p>
                  <p className="text-sm text-gray-700"><strong>Refugio:</strong> {animal.refugioNombre} - {animal.refugioCiudad}</p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
