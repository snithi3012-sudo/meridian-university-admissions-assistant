import { useEffect, useState } from "react";
import client from "../api/client";
import { getErrorMessage } from "../api/errors";
import { useAuth } from "../context/AuthContext";

const EMPTY_FORM = { category: "", title: "", content: "" };

const CATEGORY_LABELS = {
  eligibility: "Eligibility",
  fees: "Fees",
  deadlines: "Deadlines",
  documents: "Documents",
  process: "Application process",
  scholarships: "Scholarships",
  hostel: "Hostel",
  contact: "Contact",
};

export default function Admissions() {
  const { isAdmin } = useAuth();

  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [formError, setFormError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadAll();
  }, []);

  async function loadAll() {
    setLoading(true);
    setError("");
    try {
      const response = await client.get("/api/admissions");
      setItems(response.data);
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

  function startEdit(item) {
    setEditingId(item.id);
    setForm({ category: item.category || "", title: item.title || "", content: item.content || "" });
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
    try {
      if (editingId) {
        await client.put(`/api/admissions/${editingId}`, form);
      } else {
        await client.post("/api/admissions", form);
      }
      cancelForm();
      await loadAll();
    } catch (err) {
      setFormError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  async function handleDelete(id) {
    if (!window.confirm("Delete this entry? This cannot be undone.")) return;
    try {
      await client.delete(`/api/admissions/${id}`);
      await loadAll();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Admission information</h1>
          <p className="page-lede">
            Eligibility, fees, deadlines, and everything else you need to
            apply, organized by category.
          </p>
        </div>
        {isAdmin && !showForm && (
          <button className="btn btn-primary" onClick={startAdd}>
            Add information
          </button>
        )}
      </div>

      {isAdmin && showForm && (
        <form className="form form-panel" onSubmit={handleSubmit}>
          <h2>{editingId ? "Edit entry" : "New entry"}</h2>
          {formError && <div className="form-error">{formError}</div>}

          <label className="field">
            <span>Category</span>
            <input
              value={form.category}
              onChange={(e) => setForm({ ...form, category: e.target.value })}
              placeholder="e.g. eligibility, fees, deadlines, documents"
              required
            />
            <span className="field-hint">
              Use short, consistent categories — the admissions assistant
              uses these to find relevant answers.
            </span>
          </label>

          <label className="field">
            <span>Title</span>
            <input
              value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              required
            />
          </label>

          <label className="field">
            <span>Content</span>
            <textarea
              value={form.content}
              onChange={(e) => setForm({ ...form, content: e.target.value })}
              rows={4}
              required
            />
          </label>

          <div className="form-actions">
            <button className="btn btn-primary" type="submit" disabled={submitting}>
              {submitting ? "Saving…" : editingId ? "Save changes" : "Add entry"}
            </button>
            <button className="btn btn-quiet" type="button" onClick={cancelForm}>
              Cancel
            </button>
          </div>
        </form>
      )}

      {loading && <p className="muted">Loading admission information…</p>}
      {error && <div className="form-error">{error}</div>}

      {!loading && !error && items.length === 0 && (
        <p className="empty-state">
          No admission information has been added yet.{" "}
          {isAdmin ? "Use \u201cAdd information\u201d above to create the first entry." : "Check back soon."}
        </p>
      )}

      <ul className="record-list">
        {items.map((item) => (
          <li className="record-row" key={item.id}>
            <div className="record-main">
              <span className="record-tag">
                {CATEGORY_LABELS[item.category?.toLowerCase()] || item.category}
              </span>
              <h3>{item.title}</h3>
              <p className="record-detail">{item.content}</p>
            </div>
            {isAdmin && (
              <div className="record-actions">
                <button className="btn btn-quiet" onClick={() => startEdit(item)}>
                  Edit
                </button>
                <button className="btn btn-danger" onClick={() => handleDelete(item.id)}>
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
