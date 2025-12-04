import client from './client';

// --- CRUD PRINCIPAL ---

export const fetchAnimales = async (page = 1) => {
    try {
        const response = await client.get(`/animales/?page=${page}`);
        return response.data;
    } catch (error) {
        console.error('Error cargando animales:', error);
        throw error;
    }
};

export const getAnimalById = async (id) => {
    try {
        const response = await client.get(`/animales/${id}/`);
        return response.data;
    } catch (error) {
        console.error(`Error cargando animal #${id}:`, error);
        throw error;
    }
};

export const createAnimal = async (animalData) => {
    try {
        const response = await client.post('/animales/', animalData);
        return response.data;
    } catch (error) {
        console.error('Error creando animal:', error);
        throw error;
    }
};

export const updateAnimal = async (id, animalData) => {
    try {
        const response = await client.put(`/animales/${id}/`, animalData);
        return response.data;
    } catch (error) {
        console.error(`Error actualizando animal #${id}:`, error);
        throw error;
    }
};

export const deleteAnimal = async (id) => {
    try {
        await client.delete(`/animales/${id}/`);
        return true;
    } catch (error) {
        console.error(`Error eliminando animal #${id}:`, error);
        throw error;
    }
};

// --- UTILIDADES PARA LOS DROPDOWNS ---
// Esto nos ahorrará hacer 10 archivos diferentes
export const fetchCatalogs = async () => {
    try {
        // Hacemos todas las peticiones en paralelo para que cargue rápido
        const [
            refugios, razas, generos, tamanos, energias, estados
        ] = await Promise.all([
            client.get('/refugios/'),
            client.get('/razas/'),
            client.get('/generos/'),
            client.get('/tamanos/'),
            client.get('/niveles_energia/'),
            client.get('/estados_adopcion/')
        ]);

        return {
            refugios: refugios.data,
            razas: razas.data,
            generos: generos.data,
            tamanos: tamanos.data,
            energias: energias.data,
            estados: estados.data
        };
    } catch (error) {
        console.error("Error cargando catálogos:", error);
        return null;
    }
};