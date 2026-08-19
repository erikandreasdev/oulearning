# OULearning Platform

A Domain-Driven Design (DDD) & Clean Architecture backend service for managing **Organizational Units**, **Fiscal Year Budgets**, and **Training Requests**.

---

## 🚀 Prerequisites

- **Java 21**
- **Docker** (required for running integration tests via Testcontainers)
- No global Maven installation required (use the bundled Maven wrapper `./mvnw`)

---

## 🏃 Quick Start (Run Entire Application)

### 1. Start the Oracle Database
```bash
docker compose up -d
```
> **Note**: If you had a previous database volume initialized, run `docker compose down -v && docker compose up -d` to re-initialize with the dedicated PDB and user:
> - **PDB**: `OULEARNINGPDB`
> - **Username**: `oulearning`
> - **Password**: `oulearning_pass`

Wait a few seconds until the container is healthy (`docker compose ps`).

### 2. Start the Spring Boot Application
```bash
./mvnw spring-boot:run
```

The application connects to `OULEARNINGPDB` as `oulearning` and automatically applies Flyway migrations.

### Interactive API Documentation
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## 🛠️ Common Commands

### 1. Build and Run

| Goal | Command |
|---|---|
| **Run application locally** | `./mvnw spring-boot:run` |
| **Build executable JAR** | `./mvnw clean package` |
| **Run packaged JAR** | `java -jar target/oulearning-0.0.1-SNAPSHOT.jar` |
| **Clean build output** | `./mvnw clean` |

---

### 2. Testing & Verification

| Goal | Command |
|---|---|
| **Run all tests (Unit & ArchUnit)** | `./mvnw test` |
| **Run full verification & build JAR** | `./mvnw clean verify` |
| **Run specific test class** | `./mvnw test -Dtest=OrganizationTest` |
| **Run integration tests only** | `./mvnw verify -Dtest=*IT` |
| **Run architecture tests only** | `./mvnw test -Dtest=ArchitectureTest` |

---

### 3. API Testing (Bruno)

The repository includes a ready-to-use [Bruno](https://www.usebruno.com/) collection in the `bruno/` directory.

- **Collection Overview**: [`bruno/README.md`](bruno/README.md)
- **End-to-End Workflow Guide**: [`bruno/WORKFLOW_TESTING.md`](bruno/WORKFLOW_TESTING.md)

Run with Bruno CLI (`bru`):
```bash
bru run --env local
```
