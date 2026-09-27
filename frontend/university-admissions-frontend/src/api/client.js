import axios from "axios";

// Change this if your backend runs somewhere other than localhost:8080
const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const client = axios.create({
  baseURL: BASE_URL,
});

// Attach the saved JWT (if any) to every outgoing request automatically,
// so individual pages never have to think about auth headers.
client.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default client;
