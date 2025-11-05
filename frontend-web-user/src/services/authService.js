import axios from 'axios';

const API_URL = 'http://localhost:8081/api/auth';
const USER_URL = 'http://localhost:8081/api/user';


const login = async (email, password) => {
  const response = await axios.post(`${API_URL}/login`, { email, password });
  if (response.data.accessToken) {
    localStorage.setItem('userToken', response.data.accessToken);
    localStorage.setItem('refreshToken', response.data.refreshToken);
  }
  return response.data.accessToken;
};


const register = async (userData) => {
  const response = await axios.post(`${API_URL}/register`, userData);
  if (response.data.accessToken) {
    localStorage.setItem('userToken', response.data.accessToken);
    localStorage.setItem('refreshToken', response.data.refreshToken);
  }
  return response.data.accessToken;
};


const logout = () => {
  localStorage.removeItem('userToken');
  localStorage.removeItem('refreshToken');
};


// Obtener perfil del usuario autenticado
const getProfile = async () => {
  const token = localStorage.getItem('userToken');
  if (!token) throw new Error('No hay token disponible');
  const response = await axios.get(`${USER_URL}/profile`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.data;
};


// Renovar token de acceso usando refreshToken
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
  logout, 
  getProfile, 
  refreshToken
};

export default authService;
