# Rabam Car Service Manager

Small car service shop application for managing cars, assigning services, updating service status, filtering services, and keeping an audit trail through RabbitMQ.

## Tech Stack

- Backend: Java 17, Spring Boot, Spring Data JPA, Flyway, MySQL, RabbitMQ, Testcontainers
- Frontend: React, TypeScript, Axios, Material UI, Vite
- DevOps: Docker and Docker Compose

## Run With Docker Compose

```bash
docker compose up -d --build
```

Default URLs:

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- RabbitMQ Management: http://localhost:15683

Stop the stack:

```bash
docker compose down
```

Remove persisted MySQL data too:

```bash
docker compose down -v
```

## Configuration

Docker Compose supports environment overrides without editing source files:

```bash
MYSQL_DATABASE=rabam_db
MYSQL_USER=rabam_user
MYSQL_PASSWORD=rabam_pass
MYSQL_PORT=3333
BACKEND_PORT=8080
FRONTEND_PORT=5173
VITE_API_BASE_URL=http://localhost:8080/api
docker compose up -d --build
```

The backend also reads standard Spring environment variables such as `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_USERNAME`, and `SPRING_RABBITMQ_PASSWORD`.

## Local Development

Start MySQL and RabbitMQ:

```bash
docker compose up -d mysql rabbitmq
```

Run backend:

```bash
mvn spring-boot:run
```

Run frontend:

```bash
cd frontend
npm install
npm run dev
```

## Tests

Backend tests:

```bash
mvn test
```

Frontend build check:

```bash
cd frontend
npm run build
```

## Main API

- `GET /api/cars` - paginated car list
- `POST /api/cars` - create car
- `PUT /api/cars/{id}` - update car
- `GET /api/services?carId=&status=` - paginated service list with optional filters
- `POST /api/services` - create service for a car
- `PUT /api/services/{id}` - update service title, description, or status
- `GET /api/audit-logs` - paginated audit log list

Service titles are selected from this catalog: `Bakım`, `Muayene`, `Araç Yıkama`, `Lastik`, `Akaryakıt`, `Ekspertiz`, `Çekici`, `Sigorta`.

## Business Rules

License plates are normalized to uppercase and validated with this generic pattern: uppercase letters, numbers, spaces, and hyphens. Duplicate license plates return `409 Conflict`.

Service status is a forward-only state machine: `PENDING -> IN_PROGRESS -> DONE`. Skipping, going backward, or re-entering the same state returns `400 Bad Request`.

Service updates use optimistic locking through the `version` field. A stale update returns `409 Conflict` instead of silently overwriting another user's change.

## Max 2 Active Services Guarantee

When a service moves to `IN_PROGRESS`, the backend takes a pessimistic write lock on the parent car row and then counts active services for that car inside the same transaction. Because all transitions into `IN_PROGRESS` go through this locked path, concurrent requests for the same car are serialized before the count check. This prevents two simultaneous updates from both seeing the same active count and allowing the car to exceed two active services.

## Audit And Event Logging

Car and service create/update operations publish domain events after the database transaction commits. RabbitMQ delivers those events to a consumer that persists them into `audit_logs` with event type, entity type, entity ID, timestamp, and JSON payload, and also writes a standardized application log entry.

## Assumptions And Trade-offs

- MySQL is the primary runtime database; H2 is kept only for lightweight test support.
- The service title is intentionally constrained to a fixed catalog to keep the UI consistent for shop staff.
- Docker health checks focus on container readiness for local development and review environments.
- With more time, row-level frontend refresh could be made more granular for filtered service lists, and the audit event publisher could be extended with an outbox table for stronger delivery guarantees.
