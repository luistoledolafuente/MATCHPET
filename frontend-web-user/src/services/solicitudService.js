import axios from "axios";

const API_URL = "http://127.0.0.1:8081/api/solicitudes";

const buildAuthHeader = (token) => {
  if (token) return { Authorization: `Bearer ${token}` };
  const defaultAuth = axios.defaults.headers.common?.Authorization;
  if (defaultAuth) return { Authorization: defaultAuth };
  return {};
};

// =========================
// REFUGIO: Actualizar estado de solicitud
// =========================
export const updateSolicitud = async (id, updateData, token) => {
  if (!token && !axios.defaults.headers.common?.Authorization)
    throw new Error("Token no proporcionado");

  const headers = buildAuthHeader(token);
  const response = await axios.put(`${API_URL}/${id}`, updateData, { headers }); 
  return response.data;
};

// =========================
// ADOPTANTE: Enviar solicitud
// =========================
export const createSolicitud = async (solicitudData, token) => {
  if (!token && !axios.defaults.headers.common?.Authorization)
    throw new Error("Token no proporcionado");

  const headers = buildAuthHeader(token);
  const response = await axios.post(API_URL, solicitudData, { headers });
  return response.data;
};

// =========================
// REFUGIO: Ver todas las solicitudes recibidas
// =========================
export const getSolicitudesRecibidas = async (token) => {
  if (!token && !axios.defaults.headers.common?.Authorization)
    throw new Error("Token no proporcionado");

  const headers = buildAuthHeader(token);
  const response = await axios.get(`${API_URL}/recibidas`, { headers });
  return response.data;
};

// =========================
// ADOPTANTE: Ver mis solicitudes
// =========================
export const getMisSolicitudes = async (token) => {
  if (!token && !axios.defaults.headers.common?.Authorization)
    throw new Error("Token no proporcionado");

  const headers = buildAuthHeader(token);
  const response = await axios.get(`${API_URL}/mis-solicitudes`, { headers });
  return response.data;
};

export default {
  updateSolicitud,
  createSolicitud,
  getSolicitudesRecibidas,
  getMisSolicitudes
};
