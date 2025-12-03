import axios from "axios";

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
  return response.data; // Retorna { preferenceId, url, donacionId }
};

// 2. HISTORIAL ADOPTANTE (Mis Donaciones)
export const getMisDonaciones = async (token) => {
  const response = await axios.get(`${API_URL}/mis-donaciones`, {
    headers: buildAuthHeader(token)
  });
  return response.data;
};

// 3. RECIBIDAS REFUGIO (Donaciones Recibidas)
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