import { useEffect, useState } from "react";
import client from "../api/client";
import { getErrorMessage } from "../api/errors";
import { useAuth } from "../context/AuthContext";

const EMPTY_FORM = {
  courseName: "",
  duration: "",
  eligibility: "",
  fees: "",
  availableSeats: "",
};

export default function Courses() {
  const { isAdmin } = useAuth();

  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [formError, setFormError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadCourses();
  }, []);

  async function loadCourses() {
    setLoading(true);
    setError("");
    try {
      const response = await client.get("/api/courses");
      setCourses(response.data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  function startAdd() {
    setEditingId(null);
    setForm(EMPTY_FORM);
    setFormError("");
    setShowForm(true);
  }

  function startEdit(course) {
    setEditingId(course.id);
    setForm({
      courseName: course.courseName || "",
      duration: course.duration || "",
      eligibility: course.eligibility || "",
      fees: course.fees ?? "",
      availableSeats: course.availableSeats ?? "",
    });
    setFormError("");
    setShowForm(true);
  }

  function cancelForm() {
    setShowForm(false);
    setEditingId(null);
    setForm(EMPTY_FORM);
    setFormError("");
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setFormError("");
    setSubmitting(true);

    const payload = {
      courseName: form.courseName,
      duration: form.duration,
      eligibility: form.eligibility,
      fees: Number(form.fees),
      availableSeats: Number(form.availableSeats),
    };

    try {
      if (editingId) {
        await client.put(`/api/courses/${editingId}`, payload);
      } else {
        await client.post("/api/courses", payload);
      }
      cancelForm();
      await loadCourses();
    } catch (err) {
      setFormError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  async function handleDelete(id) {
    if (!window.confirm("Delete this course? This cannot be undone.")) return;
    try {
      await client.delete(`/api/courses/${id}`);
      await loadCourses();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Courses</h1>
          <p className="page-lede">
            Every program currently open for admission, with duration,
            eligibility, fees, and remaining seats.
          </p>
        </div>
        {isAdmin && !showForm && (
          <button className="btn btn-primary" onClick={startAdd}>
            Add a course
          </button>
        )}
      </div>

      {isAdmin && showForm && (
        <form className="form form-panel" onSubmit={handleSubmit}>
          <h2>{editingId ? "Edit course" : "New course"}</h2>
          {formError && <div className="form-error">{formError}</div>}

          <label className="field">
            <span>Course name</span>
            <input
              value={form.courseName}
              onChange={(e) => setForm({ ...form, courseName: e.target.value })}
              required
            />
          </label>

          <div className="field-row">
            <label className="field">
              <span>Duration</span>
              <input
                value={form.duration}
                onChange={(e) => setForm({ ...form, duration: e.target.value })}
                placeholder="e.g. 4 Years"
                required
              />
            </label>
            <label className="field">
              <span>Fees (per year)</span>
              <input
                type="number"
                min="0"
                value={form.fees}
                onChange={(e) => setForm({ ...form, fees: e.target.value })}
                required
              />
            </label>
            <label className="field">
              <span>Available seats</span>
              <input
                type="number"
                min="0"
                value={form.availableSeats}
                onChange={(e) => setForm({ ...form, availableSeats: e.target.value })}
                required
              />
            </label>
          </div>

          <label className="field">
            <span>Eligibility</span>
            <textarea
              value={form.eligibility}
              onChange={(e) => setForm({ ...form, eligibility: e.target.value })}
              rows={2}
              required
            />
          </label>

          <div className="form-actions">
            <button className="btn btn-primary" type="submit" disabled={submitting}>
              {submitting ? "Saving…" : editingId ? "Save changes" : "Add course"}
            </button>
            <button className="btn btn-quiet" type="button" onClick={cancelForm}>
              Cancel
            </button>
          </div>
        </form>
      )}

      {loading && <p className="muted">Loading courses…</p>}
      {error && <div className="form-error">{error}</div>}

      {!loading && !error && courses.length === 0 && (
        <p className="empty-state">
          No courses have been added yet.{" "}
          {isAdmin ? "Use \u201cAdd a course\u201d above to create the first one." : "Check back soon."}
        </p>
      )}

      <ul className="record-list">
        {courses.map((course) => (
          <li className="record-row" key={course.id}>
            <div className="record-main">
              <h3>{course.courseName}</h3>
              <dl className="record-meta">
                <div>
                  <dt>Duration</dt>
                  <dd>{course.duration}</dd>
                </div>
                <div>
                  <dt>Fees</dt>
                  <dd>₹{Number(course.fees).toLocaleString("en-IN")} / year</dd>
                </div>
                <div>
                  <dt>Available seats</dt>
                  <dd>{course.availableSeats}</dd>
                </div>
              </dl>
              <p className="record-detail">
                <strong>Eligibility:</strong> {course.eligibility}
              </p>
            </div>
            {isAdmin && (
              <div className="record-actions">
                <button className="btn btn-quiet" onClick={() => startEdit(course)}>
                  Edit
                </button>
                <button className="btn btn-danger" onClick={() => handleDelete(course.id)}>
                  Delete
                </button>
              </div>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}
