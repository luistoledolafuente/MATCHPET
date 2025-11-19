// Servicio para obtener datos de catálogos (lookups)
import axios from "axios";

const API_URL = "http://127.0.0.1:8081/api";


const fetchData = async (lookupPath, errorMessage, token) => {
    const fullEndpoint = `${API_URL}/lookups${lookupPath}`;
    try {
        const response = await axios.get(fullEndpoint, {
            headers: { Authorization: `Bearer ${token}` },
        });
        return response.data;
    } catch (error) {
        console.error(errorMessage, error.response || error);
        return [];
    }
};

export const getEspecies = async (token) =>
    fetchData("/especies", "Error al obtener especies:", token);

export const getRazas = async (token) =>
    fetchData("/razas", "Error al obtener razas:", token);

export const getGeneros = async (token) =>
    fetchData("/generos", "Error al obtener géneros:", token);

export const getEstadosAdopcion = async (token) =>
    fetchData("/estados-adopcion", "Error al obtener estados de adopción:", token);

export const getTamanos = async (token) =>
    fetchData("/tamanos", "Error al obtener tamaños:", token);

export const getNivelesEnergia = async (token) =>
    fetchData("/niveles-energia", "Error al obtener niveles de energía:", token);

export const getTemperamentos = async (token) =>
    fetchData("/temperamentos", "Error al obtener temperamentos:", token);
