# Meridian University Admissions — Frontend

A React app (built with Vite) that talks to your Spring Boot backend from
Phases 1–7: browsing courses and admission info, registering/logging in,
asking the RAG-powered admissions assistant questions, and viewing your
chat history.

## Before you start

**Your backend must be updated and running.** This frontend requires the
CORS fix that was added to `SecurityConfig.java` in the backend (Phase 8
update) — without it, your browser will block every request with a CORS
error, even though Postman never had a problem with it. If you haven't
already, grab the latest backend zip and re-run it.

Also make sure MongoDB is running and your backend is up at
`http://localhost:8080` (test with `http://localhost:8080/api/health` in
a browser first).

## Requirements

- Node.js 18+ and npm — check with `node -v` and `npm -v`
- If you don't have Node.js, download it from https://nodejs.org (choose
  the LTS version)

## Project structure

```
university-admissions-frontend/
├── src/
│   ├── api/
│   │   ├── client.js       - axios instance, attaches your login token automatically
│   │   └── errors.js       - turns backend errors into readable messages
│   ├── context/
│   │   └── AuthContext.jsx - who's logged in, login/register/logout
│   ├── components/
│   │   ├── Navbar.jsx
│   │   ├── ProtectedRoute.jsx  - blocks a page unless logged in
│   │   └── AdminRoute.jsx      - blocks a page unless logged in AS ADMIN
│   ├── pages/
│   │   ├── Home.jsx
│   │   ├── Login.jsx
│   │   ├── Register.jsx
│   │   ├── Courses.jsx     - browse + (if admin) add/edit/delete courses
│   │   ├── Admissions.jsx  - browse + (if admin) add/edit/delete admission info
│   │   ├── Chat.jsx        - the live chat window
│   │   └── History.jsx     - read-only log of past questions
│   ├── App.jsx              - routes
│   └── index.css            - all styling
├── .env                      - points the app at your backend's URL
└── package.json
```

## How to run it

```bash
cd university-admissions-frontend
npm install
npm run dev
```

Then open the URL it prints — usually **http://localhost:5173**

Leave both terminals running side by side:
- One running the Spring Boot backend (port 8080)
- One running `npm run dev` for this frontend (port 5173)

## How to use it

1. Open http://localhost:5173 — you'll see the homepage
2. Click **Register** to create a student account (or check "Administrator"
   and enter your admin registration code from `application.properties`
   to create an admin account)
3. Browse **Courses** and **Admission info** — visible to everyone, even
   logged out
4. If you registered as admin, you'll see **Add a course** / **Add
   information** buttons on those pages, plus Edit/Delete on each entry
5. Log in as a student (or admin) and click **Ask a question** to chat
   with the admissions assistant — try asking about something you added
   as admin, to see RAG pull in your real data
6. Click **My history** to see your past questions in a simple log

## If something doesn't work

- **"Could not reach the server"** — your backend isn't running, or isn't
  on port 8080. Check `http://localhost:8080/api/health` directly in a
  browser first.
- **CORS error in the browser console** — your backend doesn't have the
  Phase 8 CORS fix yet. Update `SecurityConfig.java` (see backend README)
  and restart the backend.
- **403 Forbidden when adding a course/admission info** — you're logged in
  as a student, not an admin. Only admin accounts can create/edit/delete.
- **Blank page after registering as admin** — double check the admin
  registration code matches `admin.registration.secret` in the backend's
  `application.properties` exactly.

## Changing the backend URL

If your backend ever runs somewhere other than `http://localhost:8080`
(e.g. after deploying it), update the `.env` file:
```
VITE_API_BASE_URL=https://your-backend-url-here
```
and restart `npm run dev`.
