---
trigger: always_on
description: Core project rules for a Java/Spring DDD + Clean Architecture codebase. Always applied.
---

# Core rules (always on)

## Stack
- Java 21, Spring Boot 3.x, Maven wrapper (`./mvnw`, never a global install), JPA/Hibernate + Flyway.
- Tests: JUnit 5, AssertJ, Mockito, Testcontainers, ArchUnit.
- Spring is an infrastructure detail. The architecture is DDD + Clean/Hexagonal.

## Architecture — dependencies point inward only
```
domain <- application <- infrastructure (adapters) <- bootstrap (Spring config)
```
- Package by bounded context first, then layer:
  `com.acme.ordering.domain.order`, `com.acme.ordering.application.placeorder`, `com.acme.ordering.infrastructure.persistence`.
- Bounded contexts never import each other's domain classes. Integrate through events, anti-corruption layers, or explicit APIs.
- ArchUnit tests enforce these boundaries and must stay green. Extend them when adding packages or contexts.
- Layer-specific rules: @100-domain.md, @200-application.md, @300-infrastructure.md, @400-testing.md.

## Coding conventions
- Immutability by default: `final` fields, records, `List.copyOf`.
- Constructor injection only. Never `@Autowired` on fields. No static mutable state, no singletons.
- No `null` in public domain/application APIs: `Optional` for returns, guard clauses for parameters. No `Optional` fields or parameters.
- No Lombok in `domain`. Elsewhere only `@Slf4j` / `@RequiredArgsConstructor`; prefer records.
- Exceptions are specific and domain-named (`InsufficientStockException extends DomainException`). Never catch-and-swallow generic `Exception`.
- Naming follows the ubiquitous language in `docs/glossary.md`. If a term is missing, ask or add it. Do not invent domain concepts silently.
- Google Java Style, 4-space indent, 120 cols. Run `./mvnw spotless:apply`.
- Comments explain why, not what. Javadoc only on public domain/application types.
- Inject `java.time.Clock` and id generators; never inline `Instant.now()` / `UUID.randomUUID()` in domain/application.

## Agent workflow
1. Read the bounded context README/glossary and the existing aggregate before adding behavior. Reuse existing value objects and use cases.
2. For non-trivial tasks produce an implementation plan listing changes per layer (domain → application → adapters → tests). Wait for approval when the change touches aggregate invariants, public APIs, or the schema.
3. Implement inside-out: domain + unit tests → use case → adapters → wiring.
4. Small, focused diffs. No unrelated refactors "while here".
5. Before reporting done, run `./mvnw spotless:apply verify` and include the real result. Never claim tests pass without running them.
6. No new dependencies without stating why. Never any framework in `domain`.
7. Never delete, weaken, or `@Disabled` tests to make a build green.
8. No hardcoded secrets; use `application-*.yml` + environment variables.
9. Commits use Conventional Commits: `feat(ordering): …`, `fix(billing): …`, `refactor`, `test`, `chore`, `docs`. Body explains intent and domain reasoning.

## Never do
- Framework annotations (`@Entity`, `@Component`, `@JsonProperty`, `@Valid`, …) on domain classes.
- Expose JPA entities or domain aggregates from controllers or messages.
- Public setters or public no-arg constructors on aggregates.
- Repository access or business logic in controllers.
- H2 in integration tests; `ddl-auto=update` in any profile.
- `Thread.sleep`, `System.out`, or `printStackTrace` in production code.
