import React, { createContext, useState, useEffect, useContext } from "react";
import authService from "../services/authService";

const AuthContext = createContext({
  token: null,
  user: null,
  isAuthenticated: false,
  loading: false,
  error: null,
  login: () => Promise.reject("Función de login no disponible fuera del AuthProvider"),
  logout: () => console.error("Función de logout no disponible fuera del AuthProvider"),
  fetchProfile: () => Promise.reject("Función de fetchProfile no disponible"),
});

export const useAuth = () => useContext(AuthContext);

export default function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("userToken"));
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (token) {
      localStorage.setItem("userToken", token);
      fetchProfile();
    } else {
      localStorage.removeItem("userToken");
      setUser(null);
    }
  }, [token]);

  const login = async (email, password) => {
    setLoading(true);
    setError(null);
    try {
      const receivedToken = await authService.login(email, password);
      setToken(receivedToken);
      return receivedToken;
    } catch (err) {
      const errorMessage = err.response?.data || "Error al iniciar sesión.";
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    authService.logout();
    setToken(null);
    setUser(null);
  };

  const fetchProfile = async () => {
    if (!token) return null;
    try {
      const profileData = await authService.getProfile();
      setUser(profileData);
      return profileData;
    } catch (err) {
      console.error("No se pudo obtener perfil:", err);
      return null;
    }
  };

  const value = {
    token,
    user,
    isAuthenticated: !!token,
    loading,
    error,
    login,
    logout,
    fetchProfile,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
