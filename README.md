# 🎓 Meridian University Admissions Assistant

A full-stack AI-powered university admissions assistant that helps students
browse courses, explore admission information, and ask questions through an
AI chatbot.

The chatbot uses the university's own data to provide grounded answers
instead of relying only on generic AI responses.

---

## 🚀 Project Overview

**Meridian University Admissions Assistant** is a full-stack web application
built with Java Spring Boot, MongoDB, Google Gemini, and React.

Students can:

- Browse available courses
- View admission information
- Check eligibility and fees
- Ask admission-related questions through an AI chatbot
- View their previous chat conversations
- Create and manage their own account

Administrators can:

- Add courses
- Update courses
- Delete courses
- Manage admission information
- Maintain the university knowledge base

---

## 🛠️ Technology Stack

### Frontend

- React
- JavaScript
- Vite
- HTML5
- CSS3

### Backend

- Java
- Spring Boot
- Spring Security
- JWT Authentication
- Maven

### Database

- MongoDB

### AI

- Google Gemini API
- Retrieval-Augmented Generation (RAG)

---

# 🏗️ System Architecture

```text
                    ┌──────────────────────┐
                    │      React UI        │
                    │      Frontend        │
                    └──────────┬───────────┘
                               │
                               │ REST API
                               ▼
                    ┌──────────────────────┐
                    │   Spring Boot API    │
                    │      Backend         │
                    └──────────┬───────────┘
                               │
                 ┌─────────────┼─────────────┐
                 │             │             │
                 ▼             ▼             ▼
          ┌──────────┐   ┌──────────┐   ┌──────────┐
          │ MongoDB  │   │ RAG / KB │   │  Gemini  │
          │ Database │   │ Retrieval│   │   AI     │
          └──────────┘   └──────────┘   └──────────┘