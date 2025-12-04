import { useState, useEffect } from 'react';
import client from '../api/client';

export function useFetchOptions(endpoint, labelKey = 'nombre', valueKey = 'id') {
    const [options, setOptions] = useState([]);

    useEffect(() => {
        client.get(`${endpoint}/`)
            .then(res => {
                const data = Array.isArray(res.data) ? res.data : res.data.results;
                setOptions(data.map(item => ({
                    value: item[valueKey],
                    label: item[labelKey]
                })));
            })
            .catch(err => console.error(`Error cargando opciones de ${endpoint}`, err));
    }, [endpoint, labelKey, valueKey]);

    return options;
}