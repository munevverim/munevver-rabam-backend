import axios from 'axios';

export const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use((config) => {
  const language = localStorage.getItem('language') || 'tr';

  config.headers['Accept-Language'] = language;

  return config;
});