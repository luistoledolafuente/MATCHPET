import React, { useState, useEffect } from "react";
import { getDonacionesRecibidas } from "../../services/donacionService";
import { useAuth } from "../../contexts/AuthContext";
import { Loader2, DollarSign, Calendar, User } from "lucide-react";

export default function DonacionesRecibidas() {
  const { token } = useAuth();
  const [donaciones, setDonaciones] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDonaciones = async () => {
      try {
        const data = await getDonacionesRecibidas(token);
        setDonaciones(data);
      } catch (error) {
        console.error("Error al cargar donaciones recibidas", error);
      } finally {
        setLoading(false);
      }
    };
    fetchDonaciones();
  }, [token]);

  // Calcular total recaudado (Solo de las completadas, asumiendo que el estado 'Pendiente' no cuenta)
  // Ajusta la lógica del estado según tus IDs de base de datos
  const totalRecaudado = donaciones
    //.filter(d => d.estadoPago?.nombre === 'Completado') 
    .reduce((acc, curr) => acc + curr.monto, 0);

  if (loading) return <div className="flex justify-center p-10"><Loader2 className="animate-spin text-[#316B7A]" /></div>;

  return (
    <div className="p-6 font-sans">
      <h1 className="text-3xl font-bold text-[#316B7A] mb-2">Donaciones Recibidas</h1>
      <p className="text-gray-500 mb-8">Gestiona y visualiza el apoyo económico recibido por la comunidad.</p>

      {/* Tarjeta de Resumen */}
      <div className="bg-white p-6 rounded-xl shadow-md border-l-4 border-[#316B7A] mb-8 max-w-sm">
        <div className="flex items-center justify-between">
            <div>
                <p className="text-sm font-bold text-gray-500 uppercase tracking-wide">Total Recaudado</p>
                <h2 className="text-4xl font-bold text-[#316B7A] mt-1">S/ {totalRecaudado.toFixed(2)}</h2>
            </div>
            <div className="bg-green-100 p-3 rounded-full">
                <DollarSign className="w-8 h-8 text-green-600" />
            </div>
        </div>
      </div>

      {/* Tabla de Donaciones */}
      <div className="bg-white rounded-xl shadow-md overflow-hidden">
        <div className="p-4 border-b bg-gray-50">
            <h3 className="font-bold text-gray-700">Historial de Transacciones</h3>
        </div>
        
        {donaciones.length === 0 ? (
            <div className="p-10 text-center text-gray-500">
                Aún no has recibido donaciones.
            </div>
        ) : (
            <div className="overflow-x-auto">
                <table className="w-full text-left text-sm text-gray-600">
                    <thead className="bg-gray-100 text-gray-700 uppercase font-semibold">
                        <tr>
                            <th className="px-6 py-3">Fecha</th>
                            <th className="px-6 py-3">Donante</th>
                            <th className="px-6 py-3">Mensaje</th>
                            <th className="px-6 py-3">Monto</th>
                            <th className="px-6 py-3">Estado</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                        {donaciones.map((d) => (
                            <tr key={d.id} className="hover:bg-gray-50 transition">
                                <td className="px-6 py-4 flex items-center">
                                    <Calendar className="w-4 h-4 mr-2 text-gray-400" />
                                    {new Date(d.fechaDonacion).toLocaleDateString()}
                                </td>
                                <td className="px-6 py-4">
                                    <div className="flex items-center">
                                        <User className="w-4 h-4 mr-2 text-gray-400" />
                                        <span className="font-medium">{d.nombreDonante || "Anónimo"}</span>
                                    </div>
                                    <div className="text-xs text-gray-400 ml-6">{d.emailDonante}</div>
                                </td>
                                <td className="px-6 py-4 italic text-gray-500">"{d.mensajeDonante}"</td>
                                <td className="px-6 py-4 font-bold text-[#316B7A]">
                                    {d.moneda} {d.monto.toFixed(2)}
                                </td>
                                <td className="px-6 py-4">
                                    <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                                        d.estadoPago?.nombre === 'Pendiente' ? 'bg-yellow-100 text-yellow-700' : 'bg-green-100 text-green-700'
                                    }`}>
                                        {d.estadoPago?.nombre}
                                    </span>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        )}
      </div>
    </div>
  );
}