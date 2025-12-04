import { useState, useEffect, useCallback } from 'react';
import client from '../api/client';

export function useCrud(endpoint) {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);

    // Cargar datos (Listar)
    const fetchAll = useCallback(async () => {
        setLoading(true);
        try {
            const res = await client.get(endpoint + '/');
            // Django DRF con paginación devuelve { results: [...] } o directo [...]
            setData(Array.isArray(res.data) ? res.data : res.data.results || []);
        } catch (err) {
            alert('Error cargando datos de ' + endpoint);
        } finally {
            setLoading(false);
        }
    }, [endpoint]);

    // Crear
    const create = async (item) => {
        await client.post(endpoint + '/', item);
        fetchAll(); // Recargar tabla
    };

    // Actualizar
    const update = async (id, item) => {
        await client.put(`${endpoint}/${id}/`, item);
        fetchAll();
    };

    // Borrar
    const remove = async (id) => {
        if (!confirm('¿Estás seguro de eliminar esto?')) return;
        await client.delete(`${endpoint}/${id}/`);
        fetchAll();
    };

    useEffect(() => {
        fetchAll();
    }, [fetchAll]);

    return { data, loading, create, update, remove, refresh: fetchAll };
}