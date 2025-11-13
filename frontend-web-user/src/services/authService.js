import axios from "axios";

// ¡IMPORTANTE! Cambiamos 'localhost' por '127.0.0.1' para evitar el bloqueo de red
const API_URL = "http://127.0.0.1:8081/api";

const saveToken = (token) => {
  localStorage.setItem("userToken", token);
  axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
};

const removeToken = () => {
  localStorage.removeItem("userToken");
  delete axios.defaults.headers.common["Authorization"];
};

// 1. LOGIN
const login = async (email, password) => {
  const response = await axios.post(`${API_URL}/auth/login`, { email, password });
  const token = response.data.accessToken;
  saveToken(token);
  return response.data;
};

// 2. REGISTRO (Adoptante)
const register = async (userData) => {
  const response = await axios.post(`${API_URL}/adoptantes/register`, userData);
  return response.data;
};

// 3. REGISTRO (Refugio)
const registerRefugio = async (refugioData) => {
  const response = await axios.post(`${API_URL}/refugios/register`, refugioData);
  return response.data;
};

// 4. PERFIL
const getProfile = async () => {
  const response = await axios.get(`${API_URL}/user/profile`);
  return response.data;
};

// 5. REFRESH TOKEN
const refreshToken = async () => {
  const response = await axios.post(`${API_URL}/auth/refresh`);
  const token = response.data.accessToken;
  saveToken(token);
  return token;
};


const logout = () => {
  removeToken();
};


export default {
  login,
  register,
  registerRefugio,
  getProfile,
  refreshToken,
  logout,
};