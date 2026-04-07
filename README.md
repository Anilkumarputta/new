# Editorial Content Management Platform

This project is a realistic internal admin platform for editors who manage media and fitness content.

We are building it in phases so you can learn the full stack step by step without getting buried in too much code at once.

## Final Project Architecture

The full project will grow into these parts:

1. Frontend
   - React + Vite + TypeScript + Tailwind CSS
   - Used by editors/admins in the browser
   - Calls backend REST APIs with JSON

2. Backend
   - Java 21 + Spring Boot + Maven
   - Contains business logic, validation, APIs, background processing, and integrations

3. PostgreSQL
   - Main relational database
   - Stores trusted source-of-truth data like shows, workouts, trainers, and workflow status

4. Kafka
   - Event streaming system
   - Sends events like `SHOW_CREATED` and `SHOW_PUBLISHED` to other parts of the system

5. Cassandra
   - Fast read-model database
   - Stores data in a denormalized shape for quick lookup screens

6. Solr
   - Search engine
   - Lets admins search content by title, category, tag, trainer, and more

7. Docker Compose
   - Runs local dependencies in containers
   - Makes local setup easier and more consistent

## Why Each Technology Is Included

- React: Builds the admin user interface.
- Vite: Starts the frontend quickly and keeps development simple.
- TypeScript: Helps catch mistakes early with types.
- Tailwind CSS: Makes styling fast and organized.
- Spring Boot: Gives us a professional backend structure used in real companies.
- Maven: Standard Java build tool for dependencies, tests, and packaging.
- PostgreSQL: Best fit for structured business data and relations.
- Kafka: Shows how event-driven systems work in real backend teams.
- Cassandra: Teaches why some systems use a separate database for fast reads at scale.
- Solr: Teaches enterprise search and indexing concepts.
- Docker Compose: Makes local infrastructure reproducible.
- Git: Teaches real project workflow and commits.
- JUnit and Mockito: Teach backend testing basics used on real teams.

## High-Level Folder Structure

```text
editorial-content-platform/
├── backend/                # Spring Boot backend
├── frontend/               # React admin app
├── docker-compose.yml      # Local infrastructure
├── .gitignore
└── README.md
```

## Development Roadmap

1. Phase 1
   - Create full project structure
   - Add Spring Boot backend
   - Add React frontend
   - Add PostgreSQL in Docker Compose
   - Add health endpoint and dashboard shell

2. Phase 2
   - Design PostgreSQL schema
   - Build CRUD APIs for shows and workouts
   - Add validation, exceptions, and seed data

3. Phase 3
   - Connect frontend to backend APIs
   - Build forms, tables, and loading/error states

4. Phase 4
   - Add publishing workflow and audit logging

5. Phase 5
   - Add Kafka events and consumers

6. Phase 6
   - Add Cassandra read model

7. Phase 7
   - Add Solr indexing and search UI

8. Phase 8
   - Add async processing and multithreading example

9. Phase 9
   - Add unit tests and integration-style tests

10. Phase 10
   - Final cleanup, docs, architecture notes, and interview explanation

## Phase 1 Scope

In this phase we create:

- project structure
- backend app shell
- frontend app shell
- PostgreSQL local setup
- one simple health check API
- one simple admin dashboard page

## Phase 1 Run Commands

### 1. Start PostgreSQL

```powershell
docker compose up -d postgres
```

### 2. Run the backend

Requirements:
- Java 21
- Maven 3.9+

```powershell
cd backend
mvn spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

Health endpoint:

```text
http://localhost:8080/api/v1/health
```

### 3. Run the frontend

Requirements:
- Node.js 20+

```powershell
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

## Common Setup Notes

- If `java` is not found, install Java 21 and add it to your `PATH`.
- If `mvn` is not found, install Maven and add it to your `PATH`.
- If `docker` is not found, install Docker Desktop and restart the terminal.
- If PostgreSQL port `5432` is busy, stop the conflicting service or change the port in `docker-compose.yml`.

We will expand this README in later phases as the system grows.
