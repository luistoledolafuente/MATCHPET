import axios from "axios";

const API_URL = "http://127.0.0.1:8081/api/animales";


const buildAuthHeader = (token) => {
  if (token) return { Authorization: `Bearer ${token}` };
  const defaultAuth = axios.defaults.headers.common?.Authorization;
  if (defaultAuth) return { Authorization: defaultAuth };
  return {};
};


export const getMisAnimales = async (token) => {
  if (!token && !axios.defaults.headers.common?.Authorization) throw new Error("Token no proporcionado");

  const response = await axios.get(`${API_URL}/mis-animales`, {
    headers: buildAuthHeader(token),
  });

  return response.data;
};


export async function createAnimal(payloadOrFormData, token) {
  const headers = buildAuthHeader(token);
  console.debug('createAnimal: axios.defaults.headers.common=', axios.defaults.headers?.common);
  console.debug('createAnimal: request headers=', headers, 'tokenPassed=', !!token);
  const url = `${API_URL}`;
  const res = await axios.post(url, payloadOrFormData, { headers: { ...headers } });

  return res.data;
}

// ---------- PUT: Actualizar Animal ----------
export const updateAnimal = async (id, formDataOrJson, token) => {
  if (!token && !axios.defaults.headers.common?.Authorization) throw new Error("Token no proporcionado");

  const headers = buildAuthHeader(token);
  const isFormData = (typeof FormData !== "undefined") && (formDataOrJson instanceof FormData);

  const config = { headers: { ...headers } };
  const response = await axios.put(`${API_URL}/${id}`, formDataOrJson, config);
  return response.data;
};

// ---------- DELETE: Borrar Animal ----------
export const deleteAnimal = async (id, token) => {
  if (!token && !axios.defaults.headers.common?.Authorization) throw new Error("Token no proporcionado");

  const response = await axios.delete(`${API_URL}/${id}`, {
    headers: buildAuthHeader(token)
  });

  return response.data;
};

// Obtener últimos eventos de bitácora
export const getBitacora = async (token) => {
  const headers = token ? { Authorization: `Bearer ${token}` } : {};
  const response = await axios.get(`${API_URL}/bitacora`, { headers });
  return response.data;
};


export default {
  getMisAnimales,
  createAnimal,
  updateAnimal,
  deleteAnimal,
  getBitacora
};
