# Awesome Pizza

REST API implemented as a Spring Modulith modular monolith for browsing the pizza menu, creating orders, and monitoring their status.

## Requirements

- Java 21 or later
- Maven 3.6.3 or later
- Docker with Compose, or an existing PostgreSQL instance
- The sibling `awesome-pizza-liquibase` database project

## Start

```powershell
Copy-Item .env.example .env
docker compose up --build -d
```

The command starts PostgreSQL, applies the Liquibase migrations, starts the Spring Boot backend,
and serves the Angular frontend through Nginx.

- Frontend: `http://localhost:4200`
- Backend API: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

The Compose application starts PostgreSQL, runs Liquibase to completion, and only then starts the Spring backend. The backend bootstrap creates the configured pizza maker in `app_user` when the username does not already exist. With the development values from `.env.example`, login uses `pizzaiolo` / `PasswordSicura123!`.

Check startup and bootstrap logs with:

```powershell
docker compose logs -f backend
```

The first successful bootstrap prints `Bootstrap pizza maker account created for username 'pizzaiolo'`. Later restarts keep the existing BCrypt password and print that the account already exists.

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

The Docker Compose development configuration supplies default credentials. Override them in `.env` before the first startup. Changing `ADMIN_PASSWORD` later does not replace the password of an existing database user.

## Architecture

The backend is a Spring Modulith modular monolith. Each business module is organized internally by responsibility, including `controller`, `service`, `repository`, `entity`, `model`, `dto`, and `mapper` packages where applicable.

```text
authentication --> shared
customer ---------> shared
administration ---> shared
```

Only four application modules are present: `administration`, `authentication`, `customer`, and `shared`. The `customer` module contains both catalog browsing and public order operations. Entities, repositories, persistence mappers, shared models, and order enumerations used across modules live in the shared kernel. API/DTO mappers remain inside their owning business module.

Module dependencies and exposed named interfaces are declared in each module's `package-info.java`. `ModularityTest` runs Spring Modulith verification and fails when code introduces a forbidden dependency, accesses a non-exposed package, creates a module cycle, or uses field injection.

Run tests with:

```powershell
mvn test
```

## API

| Method | Path | Result |
| --- | --- | --- |
| `GET` | `/api/v1` | API information |
| `GET` | `/api/v1/pizzas` | Available pizza menu |
| `POST` | `/api/v1/orders` | Creates an order and returns `201 Created` with a `Location` header |
| `GET` | `/api/v1/orders/{orderCode}` | Returns the current order state or `404 Not Found` |
| `POST` | `/api/v1/auth/login` | Authenticates an administrative user and returns a JWT |
| `GET` | `/api/v1/admin` | Protected administration entry point requiring `ROLE_PIZZA_MAKER` |
| `GET` | `/api/v1/admin/orders` | Searches orders with pagination and optional AND filters |
| `GET` | `/api/v1/admin/orders/{orderCode}` | Returns the complete order detail |
| `PATCH` | `/api/v1/admin/orders/{orderCode}/start` | Starts preparation if no other order is active |
| `PATCH` | `/api/v1/admin/orders/{orderCode}/complete` | Completes an order currently in preparation |

The administrative search accepts `id`, `orderCode`, `day`, and `status` as optional query parameters, plus the standard `page` and `size` pagination parameters. Every supplied filter is combined with `AND`. Results use a stable `createdAt DESC, id DESC` order and page sizes are capped at 100.

The start transition uses a pessimistic database lock inside the transaction. This serializes concurrent start requests before checking for an existing `IN_PREPARATION` order, enforcing the global rule that only one order can be prepared at a time. An invalid transition or an already active order returns `409 Conflict` through the common API error payload.

Example order creation request:

```powershell
$body = @{ items = @(
    @{ pizzaId = 1; quantity = 2 },
    @{ pizzaId = 2; quantity = 1 }
) } | ConvertTo-Json -Depth 3

Invoke-RestMethod `
    -Method Post `
    -Uri http://localhost:8080/api/v1/orders `
    -ContentType 'application/json' `
    -Body $body
```

Order states are `RECEIVED`, `IN_PREPARATION`, and `COMPLETED`. The public order code is an unpredictable UUID. API documentation is available at `/swagger-ui.html`, with the OpenAPI document at `/v3/api-docs`.
