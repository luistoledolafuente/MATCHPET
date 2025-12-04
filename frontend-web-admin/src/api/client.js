import axios from 'axios';

const client = axios.create({
    baseURL: 'http://localhost:8000/api/',
});

// Interceptor para debugging o tokens futuros
client.interceptors.response.use(
    response => response,
    error => {
        console.error("API Error:", error.response || error);
        return Promise.reject(error);
    }
);

export default client;