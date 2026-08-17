---
trigger: glob
globs: **/infrastructure/**/*.java, **/config/**/*.java, **/*Controller.java, **/*JpaEntity.java, **/*Adapter.java, **/db/migration/*.sql
description: Infrastructure/adapter rules — REST, persistence, messaging, config, migrations.
---

# Infrastructure / adapters

## Persistence
- JPA entities are separate classes (`OrderJpaEntity`) from domain aggregates. Never `@Entity` on domain classes.
- A persistence adapter implements the domain repository port and maps JPA ↔ domain with an explicit mapper (MapStruct or hand-written). Fully materialize aggregates; no lazy-loading leaks.
- Spring Data repositories are package-private and used only by the adapter.
- `@Version` optimistic locking on aggregate root tables.
- Flyway migrations `V<yyyyMMddHHmm>__<description>.sql`; never edit an applied migration. `ddl-auto=validate` outside local.

## Web (inbound)
- Controllers are thin: validate request DTO (Jakarta Validation) → build command → call use case → map result to response DTO. No business logic, no repository access.
- Request/response DTOs live in the adapter; never expose domain or JPA types.
- REST: plural kebab-case nouns, versioned path `/api/v1/...`. Errors as RFC 9457 Problem Details via `@RestControllerAdvice`; never leak stack traces or class names.
- Keep springdoc/OpenAPI annotations accurate.

## Messaging / clients (outbound)
- Publishers and clients implement application ports. Serialize versioned contracts explicitly, never domain classes.
- Resilience (timeouts, retries, circuit breakers) belongs here, not in application/domain.

## Bootstrap / config
- `@Configuration` wiring and `@ConfigurationProperties` records with validation. This is the only layer allowed to know all others.
- Log at boundaries with SLF4J parameterized messages, no PII.

## Tests
- Persistence: `@DataJpaTest` + Testcontainers Postgres (never H2).
- Web: `@WebMvcTest` with mocked use cases.
- `@SpringBootTest` only for a handful of smoke/e2e tests.
