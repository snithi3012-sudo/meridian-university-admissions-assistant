# AI University Admissions Assistant — Backend

## Phase 1: Basic Spring Boot Backend

### What's in this phase
- A bare Spring Boot app that boots successfully
- One endpoint: `GET /api/health`
- No database, no security, no AI yet — that's intentional

### Project structure
```
university-admissions-backend/
├── pom.xml
├── src/main/java/com/university/admissions/
│   ├── UniversityAdmissionsApplication.java
│   └── controller/HealthController.java
├── src/main/resources/application.properties
└── src/test/java/com/university/admissions/UniversityAdmissionsApplicationTests.java
```

### Requirements
- Java 17+ (`java -version`)
- Maven 3.9+ (`mvn -version`) — or use an IDE like IntelliJ / VS Code with the
  Spring Boot extension, which can run it without a separate Maven install

### How to run

**Option A — command line:**
```bash
cd university-admissions-backend
mvn spring-boot:run
```

**Option B — IntelliJ IDEA:**
1. Open the folder as a Maven project
2. Right-click `UniversityAdmissionsApplication.java` → Run

You should see Spring Boot's startup logs ending with something like:
```
Tomcat started on port(s): 8080 (http)
Started UniversityAdmissionsApplication in X.XXX seconds
```

### How to test

Open in a browser, or use curl/Postman:
```bash
curl http://localhost:8080/api/health
```

Expected response:
```json
{"message":"University Admissions Backend is running!"}
```

If you see that JSON, Phase 1 is complete ✅.

---

## Phase 2: MongoDB Database + Course CRUD API ✅

### What's new in this phase
- MongoDB connection (local, database name `university_admissions`)
- `Course` model → maps to the `courses` collection
- `CourseRepository` → data access (Spring Data MongoDB does the heavy lifting)
- `CourseService` → business logic layer
- `CourseController` → full CRUD REST API

### New structure
```
src/main/java/com/university/admissions/
├── model/Course.java
├── repository/CourseRepository.java
├── service/CourseService.java
└── controller/CourseController.java
```

### Before you run it: start MongoDB

You said you're using **local MongoDB**. Make sure the MongoDB service is
actually running before you start the Spring Boot app:

- **Windows**: MongoDB usually runs as a Windows Service named `MongoDB` —
  check Services (`services.msc`) that it's "Running", or run `mongod` manually.
- **Mac (Homebrew)**: `brew services start mongodb-community`
- **Linux**: `sudo systemctl start mongod`

Verify it's up by connecting with `mongosh` (or MongoDB Compass) — if that
connects to `localhost:27017`, you're good.

The app is configured (in `application.properties`) to connect to:
```
localhost:27017
database: university_admissions
```
No username/password. If your local Mongo has auth enabled, see the
commented-out `spring.data.mongodb.uri` line in `application.properties`.

You don't need to manually create the `university_admissions` database or
the `courses` collection — MongoDB creates them automatically the first
time we save a document.

### How to run
Same as before:
```bash
mvn spring-boot:run
```
or click Run on `UniversityAdmissionsApplication` in IntelliJ.

Watch the startup logs — you should NOT see a MongoDB connection error.
If Mongo isn't running, you'll see something like
`MongoSocketOpenException: Exception opening socket` — start Mongo and retry.

### How to test the new API

**1. Create a course**
```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
        "courseName": "B.E. Computer Science and Engineering",
        "duration": "4 Years",
        "eligibility": "12th standard with Mathematics",
        "fees": 85000,
        "availableSeats": 120
      }'
```
Response (201 Created) — note the generated `id`:
```json
{
  "id": "66f...",
  "courseName": "B.E. Computer Science and Engineering",
  "duration": "4 Years",
  "eligibility": "12th standard with Mathematics",
  "fees": 85000,
  "availableSeats": 120
}
```

**2. List all courses**
```bash
curl http://localhost:8080/api/courses
```

**3. Get one course** (replace `{id}` with the id you got back from step 1)
```bash
curl http://localhost:8080/api/courses/{id}
```

**4. Update a course**
```bash
curl -X PUT http://localhost:8080/api/courses/{id} \
  -H "Content-Type: application/json" \
  -d '{
        "courseName": "B.E. Computer Science and Engineering",
        "duration": "4 Years",
        "eligibility": "12th standard with Mathematics and 60%+ marks",
        "fees": 90000,
        "availableSeats": 100
      }'
```

**5. Delete a course**
```bash
curl -X DELETE http://localhost:8080/api/courses/{id}
```
Expect a `204 No Content` response.

**6. Try a bad id** (to see the clean error response)
```bash
curl http://localhost:8080/api/courses/doesnotexist
```
```json
{"success": false, "message": "Course not found with id: doesnotexist", "status": 404}
```

You can also do all of the above from Postman if you prefer a UI over curl.

If steps 1–6 behave as shown, Phase 2 is complete ✅.

---

## Phase 3: Admission Information APIs ✅

### What's new in this phase
- `Admission` model → general info not tied to one course (category, title, content)
- `AdmissionRepository` → includes a lookup by category
- `AdmissionService` → business logic
- `AdmissionController` → CRUD + category lookup

Suggested categories to use: `eligibility`, `fees`, `deadlines`, `documents`,
`process`, `scholarships`, `hostel`, `contact`. You can use any string you
want — MongoDB doesn't enforce a fixed list, but staying consistent will
matter later when the chatbot searches by category.

### New structure
```
src/main/java/com/university/admissions/
├── model/Admission.java
├── repository/AdmissionRepository.java
├── service/AdmissionService.java
└── controller/AdmissionController.java
```

### How to test (Postman, same as Phase 2)

**1. Add an eligibility entry**
- POST `http://localhost:8080/api/admissions`
- Body (raw, JSON):
```json
{
  "category": "eligibility",
  "title": "General Eligibility Criteria",
  "content": "Candidates must have completed 12th standard with required qualifying marks."
}
```
Expect `201 Created` with a generated `id`.

**2. Add a fees entry**
- POST `http://localhost:8080/api/admissions`
```json
{
  "category": "fees",
  "title": "Fee Payment Info",
  "content": "Fees can be paid online via the student portal or in person at the accounts office."
}
```

**3. List everything**
- GET `http://localhost:8080/api/admissions`

**4. Get by category**
- GET `http://localhost:8080/api/admissions/eligibility`
- GET `http://localhost:8080/api/admissions/fees`

**5. Try a category that doesn't exist**
- GET `http://localhost:8080/api/admissions/scholarships`
- Expect a clean 404: `{"success": false, "message": "No admission information found for category: scholarships", "status": 404}`

**6. Update an entry** (use the `id` from step 1 or 2)
- PUT `http://localhost:8080/api/admissions/{id}`
```json
{
  "category": "eligibility",
  "title": "General Eligibility Criteria (Updated)",
  "content": "Candidates must have completed 12th standard with Mathematics and 60%+ marks."
}
```

**7. Delete an entry**
- DELETE `http://localhost:8080/api/admissions/{id}`
- Expect `204 No Content`

If steps 1–7 behave as described, Phase 3 is complete ✅.

---

## Phase 4: Authentication (JWT) ✅

### What's new in this phase
- `User` model, `UserRepository` — accounts with hashed passwords
- `AuthService` / `AuthController` — register & login, issuing JWTs
- `JwtService`, `JwtAuthFilter`, `CustomUserDetailsService`, `SecurityConfig`
  — validates tokens on every request and enforces who can do what

### What's locked down now
| Endpoint | Who can access it |
|---|---|
| `GET /api/health` | Anyone |
| `POST /api/auth/register`, `/login` | Anyone |
| `GET /api/courses/**`, `GET /api/admissions/**` | Anyone (public browsing) |
| `POST/PUT/DELETE /api/courses/**` | **ADMIN only** |
| `POST/PUT/DELETE /api/admissions/**` | **ADMIN only** |

If you try a write operation (POST/PUT/DELETE on courses or admissions)
without a valid ADMIN token now, you'll get a `403 Forbidden` — this is
expected and means security is working.

### New structure
```
src/main/java/com/university/admissions/
├── model/User.java
├── repository/UserRepository.java
├── dto/RegisterRequest.java, LoginRequest.java, AuthResponse.java
├── security/JwtService.java, JwtAuthFilter.java,
│            CustomUserDetailsService.java, SecurityConfig.java
├── service/AuthService.java
└── controller/AuthController.java
```

### Important: change these two secrets before doing anything real
In `application.properties`:
```properties
jwt.secret=${JWT_SECRET}
admin.registration.secret=${ADMIN_REGISTRATION_SECRET}
```
These are fine for local development, but if this project ever goes near
the internet, generate your own random values for both and never commit
real secrets to GitHub.

### How to test (Postman)

**1. Register a STUDENT**
- POST `http://localhost:8080/api/auth/register`
```json
{
  "name": "Test Student",
  "email": "student@example.com",
  "password": "password123"
}
```
Response (`201 Created`) includes a `token`. Copy it.

**2. Register an ADMIN**
- POST `http://localhost:8080/api/auth/register`
```json
{
  "name": "Admin User",
  "email": "admin@example.com",
  "password": "adminpass123",
  "role": "ADMIN",
  "adminSecret": "letmein-admin-2026"
}
```
Copy this token too — you'll need it for write operations from now on.

**3. Try creating a course WITHOUT a token** (should now fail)
- POST `http://localhost:8080/api/courses` with the same body as in Phase 2, no Authorization header
- Expect `403 Forbidden`

**4. Try again WITH the admin token**
- Same request, but in Postman go to the **Authorization** tab → Type: **Bearer Token** → paste the admin token
- Should now succeed with `201 Created`, same as Phase 2

**5. Try creating a course with the STUDENT token instead**
- Same request, Bearer token = student's token
- Expect `403 Forbidden` — students can browse but not write

**6. Log in again later**
- POST `http://localhost:8080/api/auth/login`
```json
{ "email": "admin@example.com", "password": "adminpass123" }
```
Returns a fresh token, valid for 24 hours (`jwt.expiration-ms`).

If steps 1–6 behave as described, Phase 4 is complete ✅.

---

## Phase 5: Gemini AI Integration ✅

### What's new in this phase
- `ai/GeminiService.java` — the only class that talks directly to Google's Gemini API
- `service/ChatService.java` — sits between the controller and Gemini (this is where RAG will plug in later, in Phase 6, without touching the controller)
- `controller/ChatController.java` — exposes `POST /api/chat`
- `dto/ChatRequest.java`, `dto/ChatResponse.java`

### ⚠️ Before you run this: add your real API key

Open `application.properties` and replace the placeholder:
```properties
gemini.api.key=${GEMINI_API_KEY}
```
with your actual key from Google AI Studio. **Never share this key with anyone
or paste it into a chat with me** — treat it like a password.

Also double check `gemini.model` — Google occasionally renames or retires
models. If you get errors mentioning "model not found," go to Google AI
Studio, check which model names are currently available for your key, and
update this line:
```properties
gemini.model=gemini-3.6-flash
```
(This was `gemini-1.5-flash` originally, but Google retired it — always
verify with the ListModels check below if you hit a 404 "model not found" error.)
```

### Who can use the chat endpoint
`/api/chat` requires a logged-in user — **either a student or admin token**
works (unlike the course/admission write endpoints, which are admin-only).
This makes sense: real students should be able to ask the chatbot questions.

### How to test (Postman)

1. Log in as your student (or admin) to get a token, if you don't already have a current one:
   - POST `http://localhost:8080/api/auth/login`
   - `{"email":"student2@example.com","password":"password123"}`
   - Copy the token — remember, no quote marks around it!

2. New request:
   - POST `http://localhost:8080/api/chat`
   - Authorization → Bearer Token → paste your student (or admin) token
   - Body → raw → JSON:
   ```json
   { "question": "What is B.E. Computer Science and Engineering?" }
   ```

3. Send it. You should get back something like:
   ```json
   { "answer": "B.E. Computer Science and Engineering is a four-year..." }
   ```
   This answer comes straight from Gemini's general knowledge — it does NOT
   yet know anything about the courses/admissions you saved in MongoDB.
   That gap is exactly why Phase 6 (RAG) exists.

4. Try it with no Authorization at all — expect `403 Forbidden`, same
   pattern as before.

5. If something goes wrong contacting Gemini (bad API key, wrong model name,
   rate limit, etc.), you'll get a clean `502 Bad Gateway` with a message
   instead of a raw crash.

If steps 1–5 behave as described, Phase 5 is complete ✅.

---

## Phase 6: Knowledge Base + RAG ✅

### What's new in this phase
- `ai/RagService.java` — searches your `courses` and `admissions` MongoDB
  collections for anything relevant to the student's question, then builds
  an augmented prompt combining that data + the question before it ever
  reaches Gemini
- `ChatService` updated to call `RagService.buildPrompt()` first, then pass
  the result to `GeminiService` — `ChatController` didn't change at all

### How retrieval works (in plain terms)
1. Take the question, lowercase it, strip out filler words ("what", "is", "the", etc.)
2. Whatever words are left are the keywords (e.g. "What is the CSE fee?" → keywords: `cse`, `fee`)
3. Loop through every course and every admission-info entry in MongoDB,
   checking if any keyword appears in their text fields
4. Anything that matches gets bundled into a context block
5. If matches were found: Gemini is told "use ONLY this official info,
   and clearly flag anything you add beyond it"
6. If nothing matched: the question goes to Gemini plain, exactly like Phase 5

This is a simple keyword-based approach — good enough for a small
knowledge base like this project's. A production system with thousands of
documents would use embeddings + a vector database instead (that's a
natural next upgrade if you want to go further after this).

### How to test (Postman)

**Step 1 — make sure you have real data to retrieve.** If you haven't
already, add a course and an admission entry with matching keywords:
- POST `/api/courses` (as ADMIN):
```json
{
  "courseName": "B.E. Computer Science and Engineering",
  "duration": "4 Years",
  "eligibility": "12th standard with Mathematics",
  "fees": 85000,
  "availableSeats": 120
}
```
- POST `/api/admissions` (as ADMIN):
```json
{
  "category": "fees",
  "title": "CSE Fee Structure",
  "content": "The annual tuition fee for B.E. Computer Science and Engineering is ₹85,000, payable in two installments."
}
```

**Step 2 — ask a question that SHOULD match your data:**
- POST `/api/chat` (student or admin token)
```json
{ "question": "What is the fee for CSE?" }
```
Compare this answer to what you got in Phase 5 — it should now mention
your actual ₹85,000 figure and installment details, not a generic guess.

**Step 3 — ask something that should NOT match anything in your database:**
```json
{ "question": "What is the capital of France?" }
```
This should still get answered normally (falls back to Gemini's general
knowledge), proving the fallback path works too.

**Step 4 — try a partial match:**
```json
{ "question": "Tell me about the mechanical engineering course" }
```
If you created a mechanical engineering course earlier, this should pull
it in. If not, this is a good moment to add one and re-test.

If steps 1–4 behave as described, Phase 6 is complete ✅ — you now have a
real, working RAG chatbot grounded in your own university data.

---

## Phase 7: Chat History ✅

### What's new in this phase
- `ChatMessage` model → saved to a `chat_messages` collection (userEmail, question, answer, timestamp)
- `ChatRepository` → fetches a user's history, most recent first
- `ChatService` → now saves every question/answer pair automatically, and can fetch history
- `ChatController` → new endpoint: `GET /api/chat/history`

Notice `/api/chat` now needs to know WHO is asking — Spring Security
already knows this from your Bearer token, so we just read it off the
`Authentication` object. You don't send your email in the request body;
the backend figures out who you are from your token.

### How to test (Postman)

**1. Ask a couple of questions** (if you haven't already, or ask new ones)
- POST `/api/chat`, Bearer token = your student or admin token
```json
{ "question": "What is the fee for CSE?" }
```
- Ask a second, different question too, e.g.:
```json
{ "question": "What documents do I need for admission?" }
```

**2. Check your history**
- GET `http://localhost:8080/api/chat/history`
- Same Bearer token as above (whichever account asked the questions)
- You should get back an array like:
```json
[
  {
    "id": "...",
    "userEmail": "student2@example.com",
    "question": "What documents do I need for admission?",
    "answer": "...",
    "timestamp": "2026-09-13T10:15:30"
  },
  {
    "id": "...",
    "userEmail": "student2@example.com",
    "question": "What is the fee for CSE?",
    "answer": "...",
    "timestamp": "2026-09-13T10:14:02"
  }
]
```
Most recent question appears first.

**3. Confirm history is per-user, not shared**
- Log in as a DIFFERENT account (e.g. your admin, if you asked questions
  as a student, or vice versa)
- GET `/api/chat/history` again with that different token
- You should see an empty array `[]` (or only questions THAT account
  asked) — never someone else's questions. This proves each student's
  chat history is private to them.

If steps 1–3 behave as described, Phase 7 is complete ✅.

---

## Phase 8: CORS for the React Frontend ✅

### What's new in this phase
- `SecurityConfig.java` now includes a CORS configuration allowing requests
  from `http://localhost:5173` (Vite's default dev server port)

### Why this was needed
Postman never enforces CORS (it isn't a browser), so everything worked
fine through Phase 7. But real browsers block cross-origin requests by
default for security — and your React frontend (port 5173) calling your
backend (port 8080) counts as cross-origin. Without this fix, every
request from the React app would fail with a CORS error in the browser
console, even though the exact same request works fine in Postman.

### If you deploy the frontend somewhere else later
Add that URL to the `setAllowedOrigins(...)` list in `SecurityConfig.java`
alongside `localhost:5173`, or the deployed frontend will hit the same
CORS error.

The React frontend itself is a separate project/zip — see its own README
for setup instructions. Both the backend (port 8080) and frontend (port
5173) need to be running at the same time for the app to work.
