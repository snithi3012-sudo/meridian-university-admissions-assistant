import { useEffect, useRef, useState } from "react";
import client from "../api/client";
import { getErrorMessage } from "../api/errors";

export default function Chat() {
  const [messages, setMessages] = useState([]); // { question, answer, timestamp }
  const [question, setQuestion] = useState("");
  const [loadingHistory, setLoadingHistory] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState("");
  const bottomRef = useRef(null);

  useEffect(() => {
    loadHistory();
  }, []);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, sending]);

  async function loadHistory() {
    setLoadingHistory(true);
    try {
      const response = await client.get("/api/chat/history");
      // Backend returns most-recent-first; a chat feed reads top-to-bottom
      // chronologically, so reverse it here.
      setMessages([...response.data].reverse());
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoadingHistory(false);
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    const trimmed = question.trim();
    if (!trimmed || sending) return;

    setError("");
    setSending(true);
    setQuestion("");

    try {
      const response = await client.post("/api/chat", { question: trimmed });
      setMessages((prev) => [
        ...prev,
        { question: trimmed, answer: response.data.answer, timestamp: new Date().toISOString() },
      ]);
    } catch (err) {
      setError(getErrorMessage(err));
      setQuestion(trimmed); // give the question back so they don't retype it
    } finally {
      setSending(false);
    }
  }

  return (
    <div className="page page-chat">
      <div className="page-header">
        <div>
          <h1>Ask the admissions assistant</h1>
          <p className="page-lede">
            Ask about courses, fees, eligibility, or deadlines. Answers are
            checked against our own admissions records first.
          </p>
        </div>
      </div>

      <div className="chat-thread">
        {loadingHistory && <p className="muted">Loading your past conversation…</p>}

        {!loadingHistory && messages.length === 0 && (
          <p className="empty-state">
            You haven't asked anything yet. Try: "What is the fee for CSE?"
          </p>
        )}

        {messages.map((msg, i) => (
          <div className="chat-exchange" key={i}>
            <p className="chat-question">{msg.question}</p>
            <div className="chat-answer">{msg.answer}</div>
          </div>
        ))}

        {sending && (
          <div className="chat-exchange">
            <p className="chat-question">{question || "…"}</p>
            <div className="chat-answer chat-answer-loading">Thinking…</div>
          </div>
        )}

        <div ref={bottomRef} />
      </div>

      {error && <div className="form-error">{error}</div>}

      <form className="chat-input-bar" onSubmit={handleSubmit}>
        <input
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder="Ask a question about admissions…"
          disabled={sending}
        />
        <button className="btn btn-primary" type="submit" disabled={sending || !question.trim()}>
          {sending ? "Sending…" : "Send"}
        </button>
      </form>
    </div>
  );
}
