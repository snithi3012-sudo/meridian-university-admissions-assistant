import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getErrorMessage } from "../api/errors";

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("STUDENT");
  const [adminSecret, setAdminSecret] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      await register({ name, email, password, role, adminSecret });
      navigate("/");
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="page page-narrow">
      <h1>Create an account</h1>
      <p className="page-lede">
        Students can browse courses and ask the admissions assistant
        questions. Administrators can also add and update courses and
        admission information.
      </p>

      <form className="form" onSubmit={handleSubmit}>
        {error && <div className="form-error">{error}</div>}

        <label className="field">
          <span>Full name</span>
          <input value={name} onChange={(e) => setName(e.target.value)} required autoFocus />
        </label>

        <label className="field">
          <span>Email</span>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>

        <label className="field">
          <span>Password</span>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            minLength={6}
          />
          <span className="field-hint">At least 6 characters</span>
        </label>

        <fieldset className="field-group">
          <legend>Account type</legend>
          <label className="radio">
            <input
              type="radio"
              name="role"
              value="STUDENT"
              checked={role === "STUDENT"}
              onChange={() => setRole("STUDENT")}
            />
            <span>Student</span>
          </label>
          <label className="radio">
            <input
              type="radio"
              name="role"
              value="ADMIN"
              checked={role === "ADMIN"}
              onChange={() => setRole("ADMIN")}
            />
            <span>Administrator</span>
          </label>
        </fieldset>

        {role === "ADMIN" && (
          <label className="field">
            <span>Admin registration code</span>
            <input
              type="password"
              value={adminSecret}
              onChange={(e) => setAdminSecret(e.target.value)}
              required
              placeholder="Provided by your institution"
            />
            <span className="field-hint">
              Ask whoever set up this system for the admin code.
            </span>
          </label>
        )}

        <button className="btn btn-primary" type="submit" disabled={submitting}>
          {submitting ? "Creating account…" : "Create account"}
        </button>
      </form>

      <p className="form-footnote">
        Already have an account? <Link to="/login">Log in</Link>
      </p>
    </div>
  );
}
