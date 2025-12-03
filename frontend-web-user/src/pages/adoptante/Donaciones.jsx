import React, { useState, useEffect } from "react";
import { initMercadoPago, Wallet } from '@mercadopago/sdk-react';
import { createDonacionCheckout, getMisDonaciones } from "../../services/donacionService";
// Necesitamos este servicio para saber el nombre de la mascota si venimos con un ID
import { getAnimalById } from "../../services/animalService"; 
import { useAuth } from "../../contexts/AuthContext";
import { Heart, CreditCard, DollarSign, History, Calendar, PawPrint } from "lucide-react";
import { useSearchParams, useNavigate } from "react-router-dom"; // <--- Importamos useNavigate

// --- INICIALIZAR MERCADO PAGO ---
// Usa tu llave pública real aquí
initMercadoPago('APP_USR-c40a537c-8376-45a4-bfea-9315e92f575d', { locale: 'es-PE' });

export default function Donaciones() {
  const { token, user } = useAuth();
  const navigate = useNavigate(); // <--- Hook para navegación
  
  // Estados del formulario
  const [amount, setAmount] = useState(10);
  const [mensaje, setMensaje] = useState("");
  const [preferenceId, setPreferenceId] = useState(null);
  const [loadingPay, setLoadingPay] = useState(false);
  
  // Estados para datos externos
  const [historial, setHistorial] = useState([]);
  const [loadingHist, setLoadingHist] = useState(true);
  const [animalDestino, setAnimalDestino] = useState(null);

  // Leer parámetros de la URL (ej: ?status=success&animalId=15)
  const [searchParams] = useSearchParams();
  const status = searchParams.get("status");
  const animalIdParam = searchParams.get("animalId");

  // EFECTO PRINCIPAL: Cargar historial y datos del animal si corresponde
  useEffect(() => {
    if (token) {
      cargarHistorial();
    }
    
    if (animalIdParam) {
      cargarDatosAnimal(animalIdParam);
    }
  }, [token, animalIdParam]);

  // Función para obtener detalles de la mascota si venimos del botón "Donar" en su perfil
  const cargarDatosAnimal = async (id) => {
    try {
      const data = await getAnimalById(id);
      setAnimalDestino(data);
      // Pre-llenamos el mensaje para animar al usuario
      setMensaje(`Donación para ayudar a ${data.nombre}`);
    } catch (error) {
      console.error("No se pudo cargar la info del animal para donación", error);
    }
  };

  // Función para cargar el historial de donaciones pasadas
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

  // Función para iniciar el proceso de pago
  const handleDonate = async () => {
    setLoadingPay(true);
    try {
      const payload = {
        monto: amount,
        moneda: "PEN", // Importante: Siempre enviar el código ISO
        mensajeDonante: mensaje || "Donación voluntaria",
        nombreDonante: user?.nombre || "Anónimo",
        emailDonante: user?.email || "anonimo@matchpet.com",
        
        // Si tenemos un animal destino cargado, enviamos sus IDs
        animalId: animalDestino ? animalDestino.animal_id : null,
        refugioId: animalDestino ? animalDestino.refugioId : null 
        // Si son null, el backend lo tomará como donación general
      };

      const data = await createDonacionCheckout(payload, token);
      
      if (data && data.preferenceId) {
        setPreferenceId(data.preferenceId);
      }
    } catch (error) {
      console.error("Error al crear preferencia de pago", error);
      alert("Hubo un error al conectar con Mercado Pago. Inténtalo de nuevo.");
    } finally {
      setLoadingPay(false);
    }
  };

  // --- RENDERIZADO ---
  return (
    <div className="min-h-screen bg-[#FFF7E6] p-4 md:p-8 font-sans">
      <div className="max-w-5xl mx-auto space-y-8">
        
        {/* === TARJETA SUPERIOR: FORMULARIO DE DONACIÓN === */}
        <div className="bg-white rounded-2xl shadow-xl overflow-hidden p-6 md:p-8">
          
          {/* HEADER CON BOTÓN "BUSCAR" */}
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
              <h1 className="text-3xl font-bold text-[#316B7A] flex items-center">
                <Heart className="w-8 h-8 mr-3 text-red-500 fill-red-500" />
                {animalDestino ? `Donar a ${animalDestino.nombre}` : "Donación General"}
              </h1>

              {/* BOTÓN PARA CAMBIAR A MODO "ESPECÍFICO" (Solo visible si es general) */}
              {!animalDestino && (
                  <button 
                    onClick={() => navigate('/dashboard/adoptante/mascotas')}
                    className="text-sm bg-white border border-[#316B7A] text-[#316B7A] px-4 py-2 rounded-full hover:bg-blue-50 transition flex items-center shadow-sm"
                  >
                    <PawPrint className="w-4 h-4 mr-2" />
                    Elegir Mascota para Donar
                  </button>
              )}
          </div>

          {/* Mensaje de éxito al volver de Mercado Pago */}
          {status === 'success' && (
            <div className="bg-green-100 border-l-4 border-green-500 text-green-700 p-4 mb-6 rounded animate-pulse">
              <p className="font-bold">¡Muchas gracias!</p>
              <p>Tu donación ha sido procesada con éxito.</p>
            </div>
          )}

          {/* Tarjeta de información del animal (si aplica) */}
          {animalDestino && (
            <div className="bg-blue-50 border-l-4 border-[#316B7A] p-4 mb-6 flex items-center rounded-r-lg">
                <div className="bg-white p-2 rounded-full mr-4 shadow-sm">
                   <PawPrint className="w-6 h-6 text-[#316B7A]" />
                </div>
                <div>
                    <p className="text-[#316B7A] font-bold text-lg">Estás apoyando a {animalDestino.nombre}</p>
                    <p className="text-sm text-gray-600">
                        Tu aporte será gestionado por el refugio <strong>{animalDestino.refugioNombre}</strong>.
                    </p>
                </div>
            </div>
          )}

          <div className="grid md:grid-cols-2 gap-8">
            {/* LADO IZQUIERDO: INPUTS */}
            <div>
              <p className="text-gray-600 mb-4 font-medium">Selecciona el monto de tu ayuda:</p>
              
              <div className="grid grid-cols-3 gap-3 mb-4">
                {[10, 20, 50, 100].map((val) => (
                  <button
                    key={val}
                    onClick={() => { setAmount(val); setPreferenceId(null); }}
                    className={`py-2 px-4 rounded-lg border-2 font-bold transition duration-200 ${
                      amount === val 
                        ? "border-[#316B7A] bg-[#316B7A] text-white shadow-md" 
                        : "border-gray-200 text-gray-500 hover:border-[#316B7A] hover:text-[#316B7A]"
                    }`}
                  >
                    S/ {val}
                  </button>
                ))}
              </div>

              <div className="mb-5 relative">
                <label className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-1 block">Otro monto</label>
                <div className="relative">
                    <DollarSign className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                    <input 
                        type="number" 
                        value={amount}
                        onChange={(e) => { setAmount(Number(e.target.value)); setPreferenceId(null); }}
                        className="w-full pl-10 p-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-[#316B7A] focus:border-[#316B7A] outline-none transition"
                        placeholder="0.00"
                    />
                </div>
              </div>

              <div className="mb-6">
                <label className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-1 block">Mensaje de aliento</label>
                <textarea 
                  rows="2"
                  value={mensaje}
                  onChange={(e) => setMensaje(e.target.value)}
                  placeholder="¡Espero que sirva para sus vacunas!"
                  className="w-full p-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-[#316B7A] focus:border-[#316B7A] outline-none transition"
                />
              </div>

              {/* Botón Principal */}
              {!preferenceId ? (
                <button
                  onClick={handleDonate}
                  disabled={loadingPay || amount <= 0}
                  className="w-full bg-[#316B7A] hover:bg-[#25525d] text-white font-bold py-4 rounded-xl transition shadow-lg flex justify-center items-center disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loadingPay ? (
                    <span className="flex items-center"><div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div> Procesando...</span>
                  ) : (
                    "Continuar al Pago"
                  )}
                </button>
              ) : (
                <div className="mt-4 animate-fade-in-up bg-gray-50 p-4 rounded-xl border border-gray-200">
                  <p className="text-center text-sm text-gray-600 mb-3 font-medium">Todo listo. Paga de forma segura:</p>
                  <Wallet initialization={{ preferenceId }} customization={{ texts:{ valueProp: 'smart_option'}}} />
                  <button 
                    onClick={() => setPreferenceId(null)}
                    className="mt-3 text-xs text-gray-400 hover:text-red-500 hover:underline w-full text-center transition"
                  >
                    Cancelar y cambiar monto
                  </button>
                </div>
              )}
            </div>

            {/* LADO DERECHO: INFO */}
            <div className="bg-gray-50 p-8 rounded-2xl flex flex-col justify-center items-center text-center border border-gray-100">
              <div className="bg-white p-4 rounded-full shadow-sm mb-4">
                <CreditCard className="w-12 h-12 text-[#316B7A]" />
              </div>
              <h3 className="text-xl font-bold text-gray-800 mb-3">Tu ayuda llega lejos</h3>
              <p className="text-gray-600 text-sm mb-6 leading-relaxed">
                Usamos <strong>Mercado Pago</strong> para procesar tu donación con los más altos estándares de seguridad. Puedes usar tarjeta de crédito, débito o Yape.
              </p>
              <div className="flex items-center text-xs font-semibold text-gray-500 bg-white px-4 py-2 rounded-full border shadow-sm">
                <span className="w-2 h-2 bg-green-500 rounded-full mr-2 animate-pulse"></span>
                Conexión Segura TLS
              </div>
            </div>
          </div>
        </div>

        {/* === TARJETA INFERIOR: HISTORIAL === */}
        <div className="bg-white rounded-2xl shadow-xl overflow-hidden p-6 md:p-8">
           <h2 className="text-2xl font-bold text-gray-800 flex items-center mb-6">
             <History className="w-6 h-6 mr-2 text-[#316B7A]" />
             Mis Donaciones Anteriores
           </h2>
           
           {loadingHist ? (
             <div className="text-center py-10">
               <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-[#316B7A] mx-auto mb-2"></div>
               <p className="text-gray-500">Cargando historial...</p>
             </div>
           ) : historial.length === 0 ? (
             <div className="text-center py-12 text-gray-500 bg-gray-50 rounded-xl border border-dashed border-gray-300">
                <Heart className="w-10 h-10 mx-auto text-gray-300 mb-2" />
                <p>Aún no has realizado ninguna donación.</p>
                <p className="text-sm">¡Tu primer aporte puede cambiar una vida hoy!</p>
             </div>
           ) : (
             <div className="overflow-x-auto">
               <table className="w-full text-left border-collapse">
                 <thead>
                   <tr className="border-b border-gray-200 text-gray-500 text-xs uppercase tracking-wider">
                     <th className="py-4 px-2">Fecha</th>
                     <th className="py-4 px-2">Destino</th>
                     <th className="py-4 px-2">Monto</th>
                     <th className="py-4 px-2 text-center">Estado</th>
                   </tr>
                 </thead>
                 <tbody className="text-gray-700 text-sm">
                   {historial.map((d) => (
                     <tr key={d.id} className="border-b border-gray-100 hover:bg-blue-50 transition duration-150">
                       <td className="py-4 px-2 flex items-center font-medium">
                         <Calendar className="w-4 h-4 mr-2 text-gray-400" />
                         {new Date(d.fechaDonacion).toLocaleDateString()}
                       </td>
                       <td className="py-4 px-2">
                         {d.nombreAnimal ? (
                            <span className="flex items-center text-[#316B7A] font-semibold">
                                <PawPrint className="w-3 h-3 mr-1" /> {d.nombreAnimal}
                            </span>
                         ) : (
                            <span className="text-gray-600">{d.nombreRefugio || "MatchPet General"}</span>
                         )}
                       </td>
                       <td className="py-4 px-2 font-bold text-gray-800">
                         {d.moneda} {d.monto.toFixed(2)}
                       </td>
                       <td className="py-4 px-2 text-center">
                         <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                           d.estadoPago?.nombre === 'Completado' || d.estadoPago?.nombre === 'Aprobado'
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