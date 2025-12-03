import React, { useState, useEffect } from "react";
import { initMercadoPago, Wallet } from '@mercadopago/sdk-react';
import { createDonacionCheckout, getMisDonaciones } from "../../services/donacionService";
import { useAuth } from "../../contexts/AuthContext";
import { Heart, CreditCard, DollarSign, History, Calendar } from "lucide-react";
import { useSearchParams } from "react-router-dom";

// ¡REEMPLAZA CON TU PUBLIC KEY DE PRUEBA!
initMercadoPago('APP_USR-c40a537c-8376-45a4-bfea-9315e92f575d', { locale: 'es-PE' });

export default function Donaciones() {
  const { token, user } = useAuth();
  const [amount, setAmount] = useState(10);
  const [mensaje, setMensaje] = useState("");
  const [preferenceId, setPreferenceId] = useState(null);
  const [loadingPay, setLoadingPay] = useState(false);
  
  // Historial
  const [historial, setHistorial] = useState([]);
  const [loadingHist, setLoadingHist] = useState(true);

  const [searchParams] = useSearchParams();
  const status = searchParams.get("status");

  // Cargar historial al iniciar
  useEffect(() => {
    cargarHistorial();
  }, [token]);

  const cargarHistorial = async () => {
    try {
      const data = await getMisDonaciones(token);
      setHistorial(data);
    } catch (error) {
      console.error("Error cargando historial", error);
    } finally {
      setLoadingHist(false);
    }
  };

  const handleDonate = async () => {
    setLoadingPay(true);
    try {
      // Preparamos los datos según lo que pide tu backend
      // Nota: enviamos 'moneda: PEN' fijo como arreglamos antes
      const payload = {
        monto: amount,
        moneda: "PEN",
        mensajeDonante: mensaje || "Donación voluntaria",
        // Opcionales si el usuario ya está logueado, pero los enviamos por si acaso
        nombreDonante: user?.nombre || "Anónimo", 
        emailDonante: user?.email || "anonimo@matchpet.com"
        // refugioId y animalId los dejamos null para donación general a la plataforma
      };

      const data = await createDonacionCheckout(payload, token);
      if (data && data.preferenceId) {
        setPreferenceId(data.preferenceId);
      }
    } catch (error) {
      console.error("Error al crear preferencia", error);
      alert("Hubo un error al conectar con Mercado Pago.");
    } finally {
      setLoadingPay(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#FFF7E6] p-4 md:p-8 font-sans">
      <div className="max-w-5xl mx-auto space-y-8">
        
        {/* --- SECCIÓN 1: FORMULARIO DE DONACIÓN --- */}
        <div className="bg-white rounded-2xl shadow-xl overflow-hidden p-6 md:p-8">
          <h1 className="text-3xl font-bold text-[#316B7A] flex items-center mb-6">
            <Heart className="w-8 h-8 mr-3 text-red-500 fill-red-500" />
            Realizar Donación
          </h1>

          {status === 'success' && (
            <div className="bg-green-100 border-l-4 border-green-500 text-green-700 p-4 mb-6 rounded animate-pulse">
              <p className="font-bold">¡Gracias por tu apoyo!</p>
              <p>Tu donación se ha registrado correctamente.</p>
            </div>
          )}

          <div className="grid md:grid-cols-2 gap-8">
            <div>
              <p className="text-gray-600 mb-4">Elige un monto para ayudar a los refugios:</p>
              
              <div className="grid grid-cols-3 gap-3 mb-4">
                {[10, 20, 50, 100].map((val) => (
                  <button
                    key={val}
                    onClick={() => { setAmount(val); setPreferenceId(null); }}
                    className={`py-2 px-4 rounded-lg border-2 font-bold transition ${
                      amount === val 
                        ? "border-[#316B7A] bg-[#316B7A] text-white" 
                        : "border-gray-200 text-gray-500 hover:border-[#316B7A]"
                    }`}
                  >
                    S/ {val}
                  </button>
                ))}
              </div>

              <div className="mb-4 relative">
                <label className="text-xs font-bold text-gray-500 uppercase">Otro monto</label>
                <div className="relative mt-1">
                    <DollarSign className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                    <input 
                        type="number" 
                        value={amount}
                        onChange={(e) => { setAmount(Number(e.target.value)); setPreferenceId(null); }}
                        className="w-full pl-10 p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#316B7A] outline-none"
                    />
                </div>
              </div>

              <div className="mb-6">
                <label className="text-xs font-bold text-gray-500 uppercase">Mensaje (Opcional)</label>
                <textarea 
                  rows="2"
                  value={mensaje}
                  onChange={(e) => setMensaje(e.target.value)}
                  placeholder="¡Para las croquetas de los michis!"
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#316B7A] outline-none mt-1"
                />
              </div>

              {!preferenceId ? (
                <button
                  onClick={handleDonate}
                  disabled={loadingPay || amount <= 0}
                  className="w-full bg-[#316B7A] hover:bg-[#25525d] text-white font-bold py-3 rounded-xl transition shadow-lg flex justify-center items-center"
                >
                  {loadingPay ? "Procesando..." : "Donar Ahora"}
                </button>
              ) : (
                <div className="mt-4 animate-fade-in-up">
                  <p className="text-center text-sm text-gray-500 mb-2">Paga seguro con Mercado Pago:</p>
                  <Wallet initialization={{ preferenceId }} customization={{ texts:{ valueProp: 'smart_option'}}} />
                  <button 
                    onClick={() => setPreferenceId(null)}
                    className="mt-2 text-xs text-gray-400 hover:text-gray-600 underline w-full text-center"
                  >
                    Cancelar / Cambiar monto
                  </button>
                </div>
              )}
            </div>

            <div className="bg-gray-50 p-6 rounded-xl flex flex-col justify-center items-center text-center">
              <CreditCard className="w-16 h-16 text-[#316B7A] mb-4" />
              <h3 className="text-xl font-bold text-gray-800 mb-2">Tu ayuda hace la diferencia</h3>
              <p className="text-gray-600 text-sm mb-6">
                Todas las donaciones son procesadas de forma segura y van destinadas al cuidado, alimentación y salud de las mascotas.
              </p>
              <div className="flex items-center text-xs text-gray-400 bg-white px-3 py-1 rounded-full border">
                <span className="w-2 h-2 bg-green-500 rounded-full mr-2"></span>
                Pagos encriptados TLS
              </div>
            </div>
          </div>
        </div>

        {/* --- SECCIÓN 2: HISTORIAL DE DONACIONES --- */}
        <div className="bg-white rounded-2xl shadow-xl overflow-hidden p-6 md:p-8">
           <h2 className="text-2xl font-bold text-gray-800 flex items-center mb-6">
             <History className="w-6 h-6 mr-2 text-gray-500" />
             Mis Donaciones
           </h2>
           
           {loadingHist ? (
             <p className="text-center text-gray-500">Cargando historial...</p>
           ) : historial.length === 0 ? (
             <div className="text-center py-8 text-gray-500 bg-gray-50 rounded-lg">
                No has realizado donaciones aún. ¡Anímate a ser el primero!
             </div>
           ) : (
             <div className="overflow-x-auto">
               <table className="w-full text-left border-collapse">
                 <thead>
                   <tr className="border-b border-gray-200 text-gray-500 text-sm">
                     <th className="py-3 font-semibold">Fecha</th>
                     <th className="py-3 font-semibold">Refugio / Destino</th>
                     <th className="py-3 font-semibold">Monto</th>
                     <th className="py-3 font-semibold">Estado</th>
                   </tr>
                 </thead>
                 <tbody className="text-gray-700">
                   {historial.map((d) => (
                     <tr key={d.id} className="border-b border-gray-100 hover:bg-gray-50">
                       <td className="py-3 flex items-center">
                         <Calendar className="w-4 h-4 mr-2 text-gray-400" />
                         {new Date(d.fechaDonacion).toLocaleDateString()}
                       </td>
                       <td className="py-3">{d.nombreRefugio || "MatchPet General"}</td>
                       <td className="py-3 font-bold text-[#316B7A]">
                         {d.moneda} {d.monto.toFixed(2)}
                       </td>
                       <td className="py-3">
                         <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
                           d.estadoPago?.nombre === 'Completado' || d.estadoPago?.nombre === 'Aprobado' // Ajusta según tu BD
                             ? 'bg-green-100 text-green-700'
                             : 'bg-yellow-100 text-yellow-700'
                         }`}>
                           {d.estadoPago?.nombre || "Pendiente"}
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
    </div>
  );
}