# ATU Merlin API (Spring Boot)

Modern Java / Spring Boot re-implementation of the **ATU Merlin** IBM i (AS/400)
application. The original RPGLE / DDS data model and business logic are exposed as
a configurable REST API.

## Tech stack

- Java 21, Spring Boot 3.3
- Spring Web, Spring Data JPA, Bean Validation, Actuator
- springdoc-openapi (Swagger UI)
- H2 (default, in-memory) or PostgreSQL
- Maven

## Domain mapped from the AS/400 source

| REST resource        | Origin (IBM i)                    |
|----------------------|-----------------------------------|
| `/articles`          | `ARTICLE.PF` (FARTI) + `ARTIINF`  |
| `/families`          | `FAMILLY.PF` (FFAMI)              |
| `/countries`         | `COUNTRY.PF` (FCOUN)             |
| `/vat-codes`         | `VATDEF.PF` (FVAT) + `VAT300`     |
| `/customers`         | `CUSTOMER.PF` (FCUST)            |
| `/providers`         | `PROVIDER.PF` (FPROV)            |
| `/parameters`        | `PARAMETER.PF` (FPARAM)          |
| `/orders`            | `ORDER.PF` / `DETORD.PF` + ORD*   |

Business rules migrated:

- **VAT calculation** (`VAT300` `ClcVAT`): `vat = round(net * rate / 100, 2)`.
- **Order line totals**: `net = qty * unitPrice`, `gross = net + vat`; order header
  totals are the sum of the lines.
- **Order pricing defaults**: unit price and VAT code default from the article.
- **Last order date** (`ORD701` trigger): creating an order updates the customer's
  `lastOrderDate`.
- **Full delivery** (option 8): sets delivered quantity = ordered quantity.
- **Soft delete**: master files use a logical delete flag (original `DLCODE = 'X'`).

## Configuration (everything is externalised)

All settings live in `src/main/resources/application.yml` under `app.*`, `server.*`
and `spring.*`, and can be overridden with environment variables:

| Env var | Meaning | Default |
|---------|---------|---------|
| `SERVER_PORT` | HTTP port | `8080` |
| `API_BASE_PATH` | Prefix for every endpoint | `/api/v1` |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend origins (CSV) | `http://localhost:5173,...` |
| `PAGE_SIZE_DEFAULT` / `PAGE_SIZE_MAX` | Pagination limits | `20` / `200` |
| `SEED_ENABLED` | Seed demo data when empty | `true` |
| `APP_PROFILE` | `h2` or `postgres` | `h2` |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | Datasource | H2 in-memory |

## Run

```bash
# from generated_code/backend
mvn spring-boot:run
# or
mvn package -DskipTests && java -jar target/atu-merlin-api-1.0.0.jar

# run against PostgreSQL
APP_PROFILE=postgres DB_URL=jdbc:postgresql://localhost:5432/atumerlin \
  DB_USERNAME=atumerlin DB_PASSWORD=secret java -jar target/atu-merlin-api-1.0.0.jar

# change the API prefix and port
API_BASE_PATH=/api SERVER_PORT=9090 java -jar target/atu-merlin-api-1.0.0.jar
```

## Explore

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 console (h2 profile): `http://localhost:8080/h2-console`
- Health: `http://localhost:8080/actuator/health`

## Example

```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{"customerId":1,"lines":[{"articleId":"A00001","quantity":3},{"articleId":"A00003","quantity":1}]}'
```
