# lovable-clone

An API-first, AI-assisted application builder backend inspired by Lovable. Built with Java 21, Spring Boot, and Spring AI, it provides the core services for creating projects, generating and managing project files, collaborating with team members, and preparing generated applications for preview deployment.

The project is designed as a technical foundation for an AI application-building product—not as a turnkey hosted service. It brings together streaming LLM responses, project-aware generation, persistence, authentication, billing integrations, and an isolated runner-pod deployment approach.

## What it does

- **AI-assisted project generation:** Uses Spring AI `ChatClient` with an OpenAI-compatible provider (configured for OpenRouter by default). Responses stream to clients as Server-Sent Events. A project-file context advisor supplies the file tree, and code-generation tools let the model work with project files.
- **Projects and files:** Create, update, list, and soft-delete projects; inspect project file trees and file contents; store project assets using MinIO.
- **Chat history and generation events:** Associates conversations with projects and persists messages, token usage, and parsed generation events, including file-edit events.
- **Accounts and collaboration:** Provides signup and login endpoints, JWT-based stateless authentication, BCrypt password hashing, and project-member invitation and role-management APIs.
- **Plans, subscriptions, and usage:** Includes plan and subscription services, Stripe checkout/customer-portal integration, webhook handling, and token-usage tracking components.
- **Preview deployment foundation:** Uses the Kubernetes client to assign runner pods, synchronize project files from MinIO, start a development server, and register preview routes in Redis. Kubernetes manifests and a lightweight proxy are included in `k8s/` and `proxy/`.

## Technology

| Area | Technologies |
| --- | --- |
| Runtime and API | Java 21, Spring Boot 4, Spring MVC, Spring Security |
| AI | Spring AI 2, `ChatClient`, OpenAI-compatible chat API, Reactor, SSE |
| Persistence | PostgreSQL, Spring Data JPA, Flyway integration |
| Storage and routing | MinIO, Redis |
| Payments and deployment | Stripe Java SDK, Fabric8 Kubernetes Client |
| Build and tests | Maven Wrapper, JUnit, Spring Boot Test |

## Architecture

The codebase follows a conventional layered Spring architecture:

```text
HTTP controllers
      |
services and service implementations
      |
repositories / integration clients
      |
PostgreSQL · MinIO · Redis · LLM provider · Stripe · Kubernetes
```

Controllers expose HTTP APIs and DTOs; services own application behavior; Spring Data repositories handle persistence. The AI generation path combines a system prompt, the user's request, project-tree context, and code-generation tools, then streams model output. Once generation completes, the service parses the response into chat events and persists file edits and usage data.

## Requirements

- Java 21
- Docker and Docker Compose (for the included PostgreSQL/MinIO services)
- Redis on `localhost:6379` (not included in the Compose file)
- An OpenAI-compatible API key; the default provider URL is OpenRouter
- Stripe test credentials for billing configuration
- Maven is not required when using the included wrapper

## Run locally

1. Start PostgreSQL and MinIO:

   ```bash
   docker compose -f services.docker-compose.yml up -d
   ```

   PostgreSQL is exposed on port `9010` with database `pgvector-test` and the local development credentials in the Compose file. MinIO is exposed on `9000` (API) and `9001` (console). Create a bucket named `projects` in the MinIO console before saving generated files.

2. Create a `.env` file in the repository root. Spring imports this file as properties:

   ```dotenv
   OPENAI_API_KEY=your_openrouter_api_key
   JWT_SECRET_KEY=replace_with_a_long_random_secret
   STRIPE_API_SECRET=sk_test_your_stripe_secret
   STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret
   MINIO_ACCESS_KEY=minioadmin
   MINIO_SECRET_KEY=minioadmin123
   ```

   The application configuration currently points to local PostgreSQL on port `9010`, MinIO on `9000`, and Redis on `6379`. The Compose file's MinIO credentials match the example above. Use test credentials for Stripe unless you have deliberately configured a production environment.

3. Ensure Redis is available at `localhost:6379`, then run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

   The API uses Spring Boot's default HTTP port, `8080`. To package the application:

   ```bash
   ./mvnw clean package
   ```

4. Sign up and log in using the examples in [`src/main/java/com/github/Clementol/lovable_clone/request/auth.http`](src/main/java/com/github/Clementol/lovable_clone/request/auth.http). Use the returned bearer token for protected endpoints. Additional project and member request examples are in [`project.http`](src/main/java/com/github/Clementol/lovable_clone/request/project.http).

> **Local-development configuration:** The checked-in application configuration contains development database credentials, SQL logging, Hibernate schema auto-update settings, and disables Flyway migrations. Review and externalize these settings before deploying outside a local environment. The included Compose file does not start Redis, the application, Stripe, or the Kubernetes runner infrastructure.

## API overview

| Area | Routes |
| --- | --- |
| Authentication | `POST /api/auth/signup`, `POST /api/auth/login`, `GET /api/auth/me` |
| Projects | `GET/POST /api/projects`, `GET/PATCH/DELETE /api/projects/{id}`, `POST /api/projects/{id}/deploy` |
| Project files | `GET /api/projects/{projectId}/files`, `GET /api/projects/{projectId}/files/content?path=...` |
| AI chat | `POST /api/chat/stream` (Server-Sent Events), `GET /api/chat/projects/{projectId}` |
| Project members | `GET/POST /api/projects/{projectId}/members`, `PATCH/DELETE /api/projects/{projectId}/members/{memberId}` |
| Plans and billing | `GET /api/plans`, `GET /api/me/subscription`, `POST /api/payments/checkout`, `POST /api/payments/portal`, `POST /webhooks/payment` |
| Usage | `GET /api/usage/today` |

Most application routes require a JWT bearer token. Signup, login, and payment webhooks are permitted without an authenticated session. See the controllers and request examples for request and response DTOs.

## Repository layout

```text
src/main/java/.../
  config/       Spring and integration configuration
  controller/   HTTP API endpoints
  dto/          Request and response models
  entity/       JPA entities
  llm/          Prompts, response parsing, advisor, and generation tools
  repository/   Spring Data repositories
  security/     JWT authentication and authorization helpers
  service/      Application services and implementations
src/main/resources/
  application.yaml
  db/migration/
k8s/             Runner-pool and proxy Kubernetes manifests
proxy/           Preview-routing proxy
services.docker-compose.yml
```

## Tests

Run the test suite with:

```bash
./mvnw test
```

The current test suite is small; add integration tests for external services and end-to-end generation/deployment workflows as those paths mature.

## Current scope and production readiness

This repository is an evolving backend implementation. Before using it with real users or production data:

- Replace local development credentials and hard-coded connection settings with environment-specific configuration and secret management.
- Review controller authorization end to end. In particular, some profile/project/file/usage paths still contain placeholder user IDs or incomplete responses; do not assume every endpoint consistently enforces ownership yet.
- Harden and test asynchronous chat finalization, persistence of generated assistant content, token-usage handling, and error reporting.
- Validate the Kubernetes runner pool, MinIO access policies, Redis route lifecycle, preview-domain configuration, and container isolation in the target environment.
- Complete and test billing webhook and subscription flows, enable and validate database migrations, and add operational monitoring.

The README describes the implementation currently present in this repository; deployment integrations and product workflows may require additional infrastructure and configuration.
