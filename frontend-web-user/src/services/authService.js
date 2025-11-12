import axios from 'axios';

// ✅ URLs apuntando al backend en 8081
const API_URL = 'http://localhost:8081/api/auth';

// LOGIN
const login = async (email, password) => {
  const response = await axios.post(`${API_URL}/login`, { email, password });

  if (response.data.accessToken) {
    localStorage.setItem('userToken', response.data.accessToken);
    localStorage.setItem('refreshToken', response.data.refreshToken);

    // Configura el header por defecto para futuras solicitudes
    axios.defaults.headers.common["Authorization"] = `Bearer ${response.data.accessToken}`;
  }

  return response.data;
};

// REGISTRO ADOPTANTE
const register = async (userData) => {
  const response = await axios.post(`${API_URL}/register`, userData);

  if (response.data.accessToken) {
    localStorage.setItem('userToken', response.data.accessToken);
    localStorage.setItem('refreshToken', response.data.refreshToken);
    axios.defaults.headers.common["Authorization"] = `Bearer ${response.data.accessToken}`;
  }

  return response.data;
};

// REGISTRO REFUGIO
const registerRefugio = async (refugioData) => {
  const response = await axios.post(`${API_URL}/register-refugio`, refugioData);

  if (response.data.accessToken) {
    localStorage.setItem('userToken', response.data.accessToken);
    localStorage.setItem('refreshToken', response.data.refreshToken);
    axios.defaults.headers.common["Authorization"] = `Bearer ${response.data.accessToken}`;
  }

  return response.data;
};

// LOGOUT
const logout = () => {
  localStorage.removeItem('userToken');
  localStorage.removeItem('refreshToken');
  delete axios.defaults.headers.common["Authorization"];
};

// OBTENER PERFIL
const getProfile = async () => {
  const token = localStorage.getItem('userToken');
  if (!token) throw new Error('No hay token disponible');

  const response = await axios.get(`${API_URL}/profile`, {  // <-- aquí apuntamos al endpoint correcto
    headers: { Authorization: `Bearer ${token}` }
  });

  return response.data;
};

// RENOVAR TOKEN
const refreshToken = async () => {
  const tokenRefresh = localStorage.getItem('refreshToken');
  if (!tokenRefresh) throw new Error('No hay refresh token disponible');

  const response = await axios.post(`${API_URL}/refresh`, { refreshToken: tokenRefresh });

  if (response.data.accessToken) {
    localStorage.setItem('userToken', response.data.accessToken);
    localStorage.setItem('refreshToken', response.data.refreshToken);
    axios.defaults.headers.common["Authorization"] = `Bearer ${response.data.accessToken}`;
  }

  return response.data.accessToken;
};

// Exportamos todos los servicios
const authService = { 
  login, 
  register, 
  registerRefugio,
  logout, 
  getProfile, 
  refreshToken
};

export default authService;
