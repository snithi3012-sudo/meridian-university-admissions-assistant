import { createContext, useContext, useEffect, useState } from "react";
import client from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null); // { name, email, role } | null
  const [loading, setLoading] = useState(true);

  // On first load, restore whatever was saved from a previous session.
  useEffect(() => {
    const token = localStorage.getItem("token");
    const savedUser = localStorage.getItem("user");
    if (token && savedUser) {
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  function persistSession(data) {
    const { token, name, email, role } = data;
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify({ name, email, role }));
    setUser({ name, email, role });
  }

  async function login(email, password) {
    const response = await client.post("/api/auth/login", { email, password });
    persistSession(response.data);
    return response.data;
  }

  async function register({ name, email, password, role, adminSecret }) {
    const payload = { name, email, password };
    if (role === "ADMIN") {
      payload.role = "ADMIN";
      payload.adminSecret = adminSecret;
    }
    const response = await client.post("/api/auth/register", payload);
    persistSession(response.data);
    return response.data;
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  }

  const value = {
    user,
    isAuthenticated: !!user,
    isAdmin: user?.role === "ADMIN",
    loading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside an AuthProvider");
  }
  return context;
}
