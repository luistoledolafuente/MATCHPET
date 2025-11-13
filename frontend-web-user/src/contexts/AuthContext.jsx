import React, { createContext, useContext, useEffect, useState, useMemo } from "react";
import authService from "../services/authService.js";
import axios from "axios";

const AuthContext = createContext();

const extractUserType = (profile) => {
  const roles = profile.roles || profile.authorities;
  if (roles && roles.length > 0) {
    const principalRole = roles[0].nombreRol || roles[0].authority || roles[0];
    if (typeof principalRole === "string") return principalRole.replace("ROLE_", "").toLowerCase();
  }
  if (profile.role) return profile.role.toLowerCase();
  return "adoptante";
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [userType, setUserType] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [error, setError] = useState(null);

  const loadProfile = async () => {
    const token = localStorage.getItem("userToken");
    if (token) {
      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    } else {
      delete axios.defaults.headers.common["Authorization"];
    }

    if (!token) {
      setIsAuthenticated(false);
      setUser(null);
      setUserType(null);
      setLoading(false);
      return;
    }

    try {
      const profile = await authService.getProfile();
      setUser(profile);
      setUserType(extractUserType(profile));
      setIsAuthenticated(true);
    } catch (error) {
      console.error("No se pudo obtener perfil o token expirado:", error);
      authService.logout();
      setUser(null);
      setUserType(null);
      setIsAuthenticated(false);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProfile();
  }, []);

  // --- Función de Login corregida para usar authService ---
  const login = async (email, password) => { 
    setLoading(true);
    setError(null);
    try {
      // Usamos el servicio que ya tiene la URL correcta (127.0.0.1)
      await authService.login(email, password);
      
      // Si el login es exitoso, cargamos el perfil
      await loadProfile();

    } catch (err) {
      console.error("Error en el login:", err);
      // Mostrar el mensaje de error de la respuesta si existe
      setError(err.response?.data?.message || "Credenciales inválidas o error de conexión.");
      setIsAuthenticated(false);
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const register = async (userData) => {
    await authService.register(userData);
  };

  const registerRefugio = async (refugioData) => {
    await authService.registerRefugio(refugioData);
  };

  const logout = () => {
    authService.logout();
    setUser(null);
    setUserType(null);
    setIsAuthenticated(false);
    setError(null);
    delete axios.defaults.headers.common["Authorization"];
  };

  const value = useMemo(() => ({
    user,
    userType,
    isAuthenticated,
    loading,
    error,
    login,
    register,
    registerRefugio,
    logout
  }), [user, userType, isAuthenticated, loading, error]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);