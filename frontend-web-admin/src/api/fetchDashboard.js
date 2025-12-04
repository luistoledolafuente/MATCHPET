import client from './client';

export const fetchDashboardStats = async () => {
    try {
        const response = await client.get('/dashboard/stats/');
        return response.data;
    } catch (error) {
        console.error("Error cargando estadísticas del dashboard:", error);
        throw error;
    }
};