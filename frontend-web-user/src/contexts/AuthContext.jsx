import React, { createContext, useContext, useEffect, useState } from "react";
import authService from "../services/authService";
import axios from 'axios'; 

const AuthContext = createContext();

// Función de utilidad para extraer el rol del objeto de perfil
const extractUserType = (profile) => {
  // Asume que el backend devuelve la lista de roles en 'roles' o 'authorities'
  const roles = profile.roles || profile.authorities; 
  
  if (roles && roles.length > 0) {
      // Intentamos tomar el nombre del rol.
      const principalRole = roles[0].nombreRol || roles[0].authority || roles[0];
      
      // Limpiamos el prefijo 'ROLE_' y convertimos a minúsculas
      if (typeof principalRole === 'string') {
          return principalRole.replace('ROLE_', '').toLowerCase();
      }
  }
  
  // Si el backend devuelve un campo directo
  if (profile.role) {
      return profile.role.toLowerCase();
  }
  
  // Valor de respaldo por defecto
  return 'adoptante'; 
}


export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [userType, setUserType] = useState(null); 
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Cargar perfil al iniciar
  const loadProfile = async () => {
    const token = localStorage.getItem("userToken");
    
    // Configurar token en el header global de axios si existe
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
      
      // ✅ CORRECCIÓN: Usamos la función para obtener el tipo de usuario limpio
      setUserType(extractUserType(profile)); 
      
      setIsAuthenticated(true);
    } catch (error) {
      console.error("No se pudo obtener perfil o token expirado. Logout automático:", error);
      // Limpiar datos si el token es inválido o expiró
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
  }, []); // Se ejecuta solo al montar el componente

  // La función de login también debe actualizar el perfil
  const login = async (email, password) => {
    const data = await authService.login(email, password);
    await loadProfile(); // Cargar perfil y setear estados después de un login exitoso
    return data;
  };

  const register = async (userData) => {
    const data = await authService.register(userData);
    await loadProfile();
    return data;
  };

  const registerRefugio = async (refugioData) => {
    const data = await authService.registerRefugio(refugioData);
    await loadProfile();
    return data;
  };

  const logout = () => {
    authService.logout();
    setUser(null);
    setUserType(null);
    setIsAuthenticated(false);
  };

  const value = {
    user,
    userType,
    isAuthenticated,
    loading,
    login,
    register,
    registerRefugio,
    logout,
    profileLoading: loading, // Mantener compatibilidad
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  return useContext(AuthContext);
};