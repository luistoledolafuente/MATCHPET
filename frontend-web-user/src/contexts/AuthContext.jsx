import React, { createContext, useContext, useEffect, useState, useMemo } from "react";
import authService from "../services/authService";
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
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [error, setError] = useState(null);

    const loadProfile = async () => {
        const currentToken = localStorage.getItem("accessToken");   // ⚡ unificado

        setToken(currentToken);

        if (currentToken) {
            axios.defaults.headers.common["Authorization"] = `Bearer ${currentToken}`;
        } else {
            delete axios.defaults.headers.common["Authorization"];
            setIsAuthenticated(false);
            setUser(null);
            setUserType(null);
            setLoading(false);
            return;
        }

        try {
            const profile = await authService.getProfile();
            console.log("💛 PROFILE RECIBIDO DEL BACK:", profile);
            setUser(profile);
            setUserType(extractUserType(profile));
            setIsAuthenticated(true);
        } catch (err) {
            console.error("No se pudo obtener perfil o token expirado:", err);
            authService.logout();
            setToken(null);
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

    const login = async (email, password) => {
        setLoading(true);
        setError(null);
        try {
            await authService.login(email, password);   // ⚡ token guardado automáticamente
            const savedToken = localStorage.getItem("accessToken");
            setToken(savedToken);
            await loadProfile();
        } catch (err) {
            console.error("Error en el login:", err);
            setError(err.response?.data?.message || "Credenciales inválidas o error de conexión.");
            setIsAuthenticated(false);
            setUser(null);
            setToken(null);
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
        setToken(null);
        setUser(null);
        setUserType(null);
        setIsAuthenticated(false);
        setError(null);
        delete axios.defaults.headers.common["Authorization"];
    };
    
    const value = useMemo(() => ({
  user,
  userType,
  token,
  isAuthenticated,
  loading,
  error,
  login,
  register,
  registerRefugio,
  logout,
  setToken: ({ accessToken }) => {
    if (accessToken) {
      localStorage.setItem("accessToken", accessToken);
      axios.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
      loadProfile(); // ⚡ Carga perfil después de Google login
    }
  },
}), [user, userType, token, isAuthenticated, loading, error]);



    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);
