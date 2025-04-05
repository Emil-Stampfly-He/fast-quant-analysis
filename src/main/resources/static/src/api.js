import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

export const apiClient = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true, // 如需传递 cookie
    headers: {
        'Content-Type': 'application/json'
    }
});
