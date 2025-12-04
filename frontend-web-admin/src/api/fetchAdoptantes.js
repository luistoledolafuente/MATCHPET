import client from './client';

// Obtener la lista de adoptantes, con paginación
export const fetchAdoptantes = async (page = 1) => {
    try {
        const response = await client.get(`/adoptantes/?page=${page}`);
        return response.data;  // Aquí puedes acceder a los adoptantes con response.data.results
    } catch (error) {
        console.error('Error fetching adoptantes:', error);
        throw error;
    }
};
