import React, { useEffect, useState } from "react";
import solicitudService from "../../services/solicitudService";
import { useAuth } from "../../contexts/AuthContext";

const MisSolicitudes = () => {
  const { token } = useAuth();
  const [solicitudes, setSolicitudes] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchSolicitudes = async () => {
    try {
      const data = await solicitudService.getMisSolicitudes(token);
      setSolicitudes(data);
    } catch (error) {
      console.error("Error al obtener mis solicitudes:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSolicitudes();
  }, []);

  if (loading) return <p>Cargando mis solicitudes...</p>;

  return (
    <div>
      <h1>Mis Solicitudes</h1>
      {solicitudes.length === 0 ? (
        <p>No has enviado solicitudes aún.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Animal</th>
              <th>Refugio</th>
              <th>Estado</th>
            </tr>
          </thead>
          <tbody>
            {solicitudes.map((sol) => (
              <tr key={sol.id}>
                <td>{sol.animalNombre}</td>
                <td>{sol.refugioNombre}</td>
                <td>{sol.estado}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default MisSolicitudes;
