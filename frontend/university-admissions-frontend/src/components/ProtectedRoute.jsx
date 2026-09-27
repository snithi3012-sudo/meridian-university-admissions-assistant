import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();

  if (loading) return null; // avoid a flash-redirect while we check localStorage
  if (!isAuthenticated) return <Navigate to="/login" replace />;

  return children;
}
