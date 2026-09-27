import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/");
  }

  return (
    <header className="nav">
      <div className="nav-inner">
        <Link to="/" className="nav-brand">
          Meridian University
          <span className="nav-brand-sub">Admissions</span>
        </Link>

        <nav className="nav-links">
          <Link to="/courses">Courses</Link>
          <Link to="/admissions">Admission info</Link>
          {isAuthenticated && <Link to="/chat">Ask a question</Link>}
          {isAuthenticated && <Link to="/history">My history</Link>}
        </nav>

        <div className="nav-auth">
          {isAuthenticated ? (
            <>
              <span className="nav-user">
                {user.name} <span className="nav-role">{user.role}</span>
              </span>
              <button className="btn btn-quiet" onClick={handleLogout}>
                Log out
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn-quiet">
                Log in
              </Link>
              <Link to="/register" className="btn btn-primary">
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
