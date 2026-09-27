import { useEffect, useState } from "react";
import client from "../api/client";
import { getErrorMessage } from "../api/errors";

export default function History() {
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    (async () => {
      try {
        const response = await client.get("/api/chat/history");
        setMessages(response.data); // already most-recent-first
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>My question history</h1>
          <p className="page-lede">Every question you've asked, most recent first.</p>
        </div>
      </div>

      {loading && <p className="muted">Loading…</p>}
      {error && <div className="form-error">{error}</div>}

      {!loading && !error && messages.length === 0 && (
        <p className="empty-state">
          You haven't asked the admissions assistant anything yet.
        </p>
      )}

      <ul className="record-list">
        {messages.map((msg) => (
          <li className="record-row" key={msg.id}>
            <div className="record-main">
              <time className="record-timestamp">{formatTimestamp(msg.timestamp)}</time>
              <h3>{msg.question}</h3>
              <p className="record-detail">{msg.answer}</p>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

function formatTimestamp(value) {
  try {
    return new Date(value).toLocaleString(undefined, {
      dateStyle: "medium",
      timeStyle: "short",
    });
  } catch {
    return value;
  }
}
