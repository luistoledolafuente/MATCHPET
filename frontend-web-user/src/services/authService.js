import axios from 'axios';

const API_URL = 'http://localhost:8081/api/auth';
const USER_URL = 'http://localhost:8081/api/user';

// LOGIN
const login = async (email, password) => {
  const response = await axios.post(`${API_URL}/login`, { email, password });
  if (response.data.accessToken) {
    localStorage.setItem('userToken', response.data.accessToken);
    localStorage.setItem('refreshToken', response.data.refreshToken);
  }
  return response.data;
};

// REGISTRO ADOPTANTE
const register = async (userData) => {
  const response = await axios.post(`${API_URL}/register`, userData);
  if (response.data.accessToken) {
    localStorage.setItem('userToken', response.data.accessToken);
    localStorage.setItem('refreshToken', response.data.refreshToken);
  }
  return response.data;
};

// REGISTRO REFUGIO
const registerRefugio = async (refugioData) => {
  const response = await axios.post(`${API_URL}/register-refugio`, refugioData);
  if (response.data.accessToken) {
    localStorage.setItem('userToken', response.data.accessToken);
    localStorage.setItem('refreshToken', response.data.refreshToken);
  }
  return response.data;
};

// LOGOUT
const logout = () => {
  localStorage.removeItem('userToken');
  localStorage.removeItem('refreshToken');
};

// OBTENER PERFIL
const getProfile = async () => {
  const token = localStorage.getItem('userToken');
  if (!token) throw new Error('No hay token disponible');
  const response = await axios.get(`${USER_URL}/profile`, {
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
  }
  return response.data.accessToken;
};

const authService = { 
  login, 
  register, 
  registerRefugio,
  logout, 
  getProfile, 
  refreshToken
};

export default authService;
