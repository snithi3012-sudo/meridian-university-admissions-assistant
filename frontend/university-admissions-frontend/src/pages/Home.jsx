import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Home() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="page page-home">
      <section className="hero">
        <p className="hero-eyebrow">Undergraduate Admissions, 2027 intake</p>
        <h1>
          Find your course.
          <br />
          Ask your questions.
          <br />
          Apply with confidence.
        </h1>
        <p className="hero-lede">
          Browse every program we offer, check eligibility and fees, and get
          straight answers from an assistant trained on our own admissions
          records — not just generic advice.
        </p>
        <div className="hero-actions">
          <Link to="/courses" className="btn btn-primary">
            Browse courses
          </Link>
          {!isAuthenticated && (
            <Link to="/register" className="btn btn-quiet">
              Create an account
            </Link>
          )}
          {isAuthenticated && (
            <Link to="/chat" className="btn btn-quiet">
              Ask the admissions assistant
            </Link>
          )}
        </div>
      </section>

      <section className="home-grid">
        <div className="home-grid-item">
          <h3>Every program, one place</h3>
          <p>
            Duration, eligibility, fees, and seat availability for each
            course we offer — kept current by our admissions office.
          </p>
        </div>
        <div className="home-grid-item">
          <h3>Answers grounded in our records</h3>
          <p>
            Our assistant checks our own admissions database before
            answering, so figures like fees and deadlines come from us,
            not a guess.
          </p>
        </div>
        <div className="home-grid-item">
          <h3>Your questions, saved</h3>
          <p>
            Sign in and every question you ask is saved to your account, so
            you can pick up the conversation later.
          </p>
        </div>
      </section>
    </div>
  );
}
