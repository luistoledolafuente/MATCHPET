import client from './client';

// 1. Obtener la lista de refugios con paginación
export const fetchRefugios = async (page = 1) => {
    try {
        const response = await client.get(`/refugios/?page=${page}`);
        return response.data; // Devuelve los datos de la paginación
    } catch (error) {
        console.error('Error al obtener refugios:', error);
        throw error;
    }
};

// 2. Crear refugio
export const createRefugio = async (refugioData) => {
    try {
        const response = await client.post('/refugios/', refugioData);
        return response.data;
    } catch (error) {
        console.error('Error creando refugio:', error);
        throw error;
    }
};

// 3. Actualizar refugio
export const updateRefugio = async (id, refugioData) => {
    try {
        const response = await client.put(`/refugios/${id}/`, refugioData);
        return response.data;
    } catch (error) {
        console.error('Error actualizando refugio:', error);
        throw error;
    }
};

// 4. Eliminar refugio
export const deleteRefugio = async (id) => {
    try {
        await client.delete(`/refugios/${id}/`);
    } catch (error) {
        console.error('Error eliminando refugio:', error);
        throw error;
    }
};
