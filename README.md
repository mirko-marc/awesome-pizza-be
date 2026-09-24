# Awesome Pizza

REST API implemented as a Spring Modulith modular monolith for browsing the pizza menu, creating orders, and monitoring their status.

## Requirements

- Docker Desktop with Docker Compose
- The backend, frontend, and Liquibase repositories cloned as sibling directories

Java 21 and Maven are required only when running or testing the backend outside Docker.

The expected directory layout is:

```text
C:\
|-- awesome-pizza
|-- awesome-pizza-fe
`-- awesome-pizza-liquibase
```

The paths are relative to this repository, so the three directory names and their sibling relationship must be preserved.

## Run the complete system with Docker

Open PowerShell in the backend repository:

```powershell
cd C:\awesome-pizza
docker compose up --build -d
```

Docker Compose starts the components in dependency order:

```text
PostgreSQL -> Liquibase -> Spring Boot backend -> Angular frontend
```

Liquibase is a one-shot container. Its expected final state is `Exited (0)`, which means that the database migrations completed successfully. PostgreSQL, backend, and frontend must report `healthy`.

Check the complete stack with:

```powershell
docker compose ps --all
```

The application is available at:

- Frontend: `http://localhost:4200`
- Backend API: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

The frontend Nginx server forwards requests under `/api` to the backend container, so the browser accesses the UI and API through the same frontend origin.

### Development administrator

The backend bootstrap creates the configured pizza maker in `app_user` when the username does not already exist. The default development credentials are:

```text
Username: pizzaiolo
Password: PasswordSicura123!
```

Override these values before the first startup by copying the example environment file and editing `.env`:

```powershell
Copy-Item .env.example .env
```

### Stop and restart

Stop the system while preserving PostgreSQL data:

```powershell
docker compose down
```

Start it again without rebuilding unchanged images:

```powershell
docker compose up -d
```

Remove containers and all persisted database data:

```powershell
docker compose down --volumes
```

The last command is destructive and should only be used when a clean database is explicitly required.

## Configuration

The local database defaults are `pizza` / `pizza`. Use `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `DATABASE_SCHEMA` to override the database configuration. The separate Liquibase project creates and updates the schema; Hibernate validates it at application startup.

Authentication configuration is read from environment variables:

```text
JWT_SECRET_BASE64=<Base64 encoded secret containing at least 32 bytes>
JWT_ISSUER=awesome-pizza
JWT_ACCESS_TOKEN_TTL=PT1H
ADMIN_USERNAME=<optional bootstrap username>
ADMIN_PASSWORD=<optional bootstrap password>
CORS_ALLOWED_ORIGINS=http://localhost:4200
```

`ADMIN_USERNAME` and `ADMIN_PASSWORD` are optional for non-Docker execution, but they must be provided together. When configured, the application creates the pizza maker account only if it does not already exist and stores only its BCrypt hash.

The Docker Compose development configuration supplies default credentials and ports. See `.env.example` for every supported override, including `APP_PORT` and `FRONTEND_PORT`.

## Architecture

The backend is a Spring Modulith modular monolith. Each business module is organized internally by responsibility, including `controller`, `service`, `repository`, `entity`, `model`, `dto`, and `mapper` packages where applicable.

```text
authentication --> shared
ordering ---------> shared
```

Only three application modules are present: `ordering`, `authentication`, and `shared`. The `ordering` module owns public ordering, pizza menu queries, administration order operations, entities, repositories, models, and mappers. Its implementation is kept under `ordering.internal`; it does not expose an application API because no other module calls it. The `shared` module contains only cross-cutting configuration, auditing, common error DTOs, and generic exception support.

`OrderService` owns order creation, lookup, administration search, preparation start, and completion. `PizzaService` owns pizza menu queries. Customer and administration controllers use the same application models and expose separate HTTP DTOs where their contracts differ.

Module dependencies and exposed named interfaces are declared in each module's `package-info.java`. `ModularityTest` runs Spring Modulith verification and fails when code introduces a forbidden dependency, accesses a non-exposed package, creates a module cycle, or uses field injection.

Run tests with:

```powershell
mvn test
```

The PostgreSQL concurrency test is opt-in and requires the migrated database on `localhost:5432` (or the `DB_*` environment variables):

```powershell
$env:RUN_POSTGRES_INTEGRATION_TESTS='true'
mvn '-Dtest=OrderConcurrencyPostgresTest' test
```

## API

| Method | Path | Result |
| --- | --- | --- |
| `GET` | `/api/v1/pizzas` | Available pizza menu |
| `POST` | `/api/v1/orders` | Creates an order and returns `201 Created` with a `Location` header |
| `GET` | `/api/v1/orders/{orderCode}` | Returns the current order state or `404 Not Found` |
| `POST` | `/api/v1/auth/login` | Authenticates an administrative user and returns a JWT |
| `GET` | `/api/v1/admin` | Protected administration entry point requiring `ROLE_PIZZA_MAKER` |
| `GET` | `/api/v1/admin/orders` | Searches orders with pagination and optional AND filters |
| `GET` | `/api/v1/admin/orders/{orderCode}` | Returns the complete order detail |
| `PATCH` | `/api/v1/admin/orders/{orderCode}/start` | Starts preparation if no other order is active |
| `PATCH` | `/api/v1/admin/orders/{orderCode}/complete` | Completes an order currently in preparation |


Order states are `RECEIVED`, `IN_PREPARATION`, and `COMPLETED`. The public order code is an unpredictable UUID. API documentation is available at `/swagger-ui/index.html`, with the OpenAPI document at `/v3/api-docs`.
