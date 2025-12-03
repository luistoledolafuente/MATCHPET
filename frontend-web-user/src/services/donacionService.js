import axios from "axios";

// Asegúrate de que esta URL coincida con tu backend
const API_URL = "http://127.0.0.1:8081/api/donaciones";

const buildAuthHeader = (token) => {
  return { Authorization: `Bearer ${token}` };
};

// 1. INICIAR PAGO (Checkout)
export const createDonacionCheckout = async (donacionData, token) => {
  // donacionData debe incluir: { monto, moneda: 'PEN', mensajeDonante, ... }
  const response = await axios.post(`${API_URL}/checkout`, donacionData, {
    headers: buildAuthHeader(token)
  });
  // Retorna { preferenceId, url, donacionId }
  return response.data; 
};

// 2. HISTORIAL ADOPTANTE (Mis Donaciones)
export const getMisDonaciones = async (token) => {
  const response = await axios.get(`${API_URL}/mis-donaciones`, {
    headers: buildAuthHeader(token)
  });
  return response.data;
};

// 3. RECIBIDAS REFUGIO (Donaciones Recibidas - Para el dashboard del refugio)
export const getDonacionesRecibidas = async (token) => {
  const response = await axios.get(`${API_URL}/recibidas`, {
    headers: buildAuthHeader(token)
  });
  return response.data;
};

export default {
  createDonacionCheckout,
  getMisDonaciones,
  getDonacionesRecibidas
};