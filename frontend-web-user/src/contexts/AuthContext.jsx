import React, { createContext, useContext, useEffect, useState, useMemo } from "react";
import authService from "../services/authService"; // Importación corregida (sin .js)
import axios from "axios"; // <-- agregado

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
    const [token, setToken] = useState(null); // <-- CLAVE: Nuevo estado para el token
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [error, setError] = useState(null);

    const loadProfile = async () => {
        const currentToken = localStorage.getItem("userToken");

        // 1. Setear el token en el estado
        setToken(currentToken);

        // NUEVO: si hay token en localStorage, asegurarse de que axios lo use
        if (currentToken) {
            axios.defaults.headers.common['Authorization'] = `Bearer ${currentToken}`;
        }

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
            setUser({ ...profile, refugio_id: profile.refugio_id });
            setUserType(extractUserType(profile));
            setIsAuthenticated(true);
        } catch (error) {
            console.error("No se pudo obtener perfil o token expirado:", error);
            authService.logout();
            setToken(null); // Limpiar token al fallar
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

    // --- Función de Login ---
    const login = async (email, password) => {
        setLoading(true);
        setError(null);
        try {
            await authService.login(email, password);

            // Si el login es exitoso, volvemos a cargar el perfil (y el nuevo token)
            await loadProfile();

        } catch (err) {
            console.error("Error en el login:", err);
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
        setToken(null); // Limpiar el token del estado
        setUser(null);
        setUserType(null);
        setIsAuthenticated(false);
        setError(null);
        // NUEVO: limpiar header por defecto de axios al cerrar sesión
        delete axios.defaults.headers.common['Authorization'];
    };

    const value = useMemo(() => ({
        user,
        userType,
        token, // <-- CLAVE: Exponer el token
        isAuthenticated,
        loading,
        error,
        login,
        register,
        registerRefugio,
        logout
    }), [user, userType, token, isAuthenticated, loading, error]); // <-- Incluir token en dependencias

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);