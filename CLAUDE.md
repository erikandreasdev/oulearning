
# CLAUDE.md

## Stack
- Java 21, Spring Boot 3.x, Maven wrapper (`./mvnw`, never a global install), JPA/Hibernate + Flyway.
- Tests: JUnit 5, AssertJ, Mockito, Testcontainers, ArchUnit, Instancio.
- Spring is an infrastructure detail. The architecture is DDD + Clean/Hexagonal.

## Architecture — dependencies point inward only
```
domain <- application <- infrastructure (adapters) <- bootstrap (Spring config)
```
- Package by bounded context first, then layer:
  `com.example.oulearning.organization.domain.employee`, `com.example.oulearning.budgeting.domain`, `com.example.oulearning.training.domain`.
- No `shared` package: every class belongs to its dedicated bounded context purpose package.
- Bounded contexts never import each other's domain classes directly. Integrate through events, anti-corruption layers, or explicit identifiers/APIs.
- ArchUnit tests enforce these boundaries and must stay green. Extend them when adding packages or contexts.
- Layer-specific rules: see the layer rules imported below.

## Coding conventions
- Immutability by default: `final` variables, `final` parameters, `final` fields, records, `List.copyOf`, `Set.copyOf`.
- Leverage Java type inference using `var` for local variables wherever possible (`final var x = ...;`).
- Constructor injection only. Never `@Autowired` on fields. No static mutable state, no singletons.
- Formatted strings only everywhere: use `String#formatted` / `"%s ...".formatted(...)`, avoid string concatenation with `+`.
- All identifiers are typed UUID value objects (`EmployeeId`, `OuId`, `BudgetId`, `TrainingId`, `TypeId`), never raw `UUID` or `String` primitives in domain/application models.
- Clean separation of constants:
  - **Domain constants** (`src/main/java/**/<Context>Constants.java`): only contain business rules and invariants enforced by domain objects (min/max lengths, regexes, scales, limits).
  - **Test generation constants** (`src/test/java/**`): live strictly inside test factories/test classes (ranges, generation templates, replacement characters).
  - No magical numbers or strings anywhere.
- No `null` in public domain/application APIs: `Optional` for returns, guard clauses for parameters. No `Optional` fields or parameters.
- No Lombok in `domain`. Elsewhere only `@Slf4j` / `@RequiredArgsConstructor`; prefer records.
- Exceptions are specific and domain-named (`InvalidTrainingOperationException extends DomainException`). Never catch-and-swallow generic `Exception`.
- Naming follows the ubiquitous language in `docs/GLOSSARY.md`. If a term is missing, ask or add it. Do not invent domain concepts silently.
- Google Java Style, 4-space indent, 120 cols. Run `./mvnw spotless:apply`.
- Pure code in production: No line, block, or Javadoc comments anywhere in production code. In tests, strictly only the mandatory `// given`, `// when`, `// then` line comments are permitted.
- Inject `java.time.Clock` and id generators; never inline `Instant.now()` / `UUID.randomUUID()` in production domain/application logic.

## Agent workflow
1. Read the bounded context README/glossary and the existing aggregate before adding behavior. Reuse existing value objects and use cases.
2. For non-trivial tasks produce an implementation plan listing changes per layer (domain → application → adapters → tests). Wait for approval when the change touches aggregate invariants, public APIs, or the schema.
3. Implement inside-out: domain + unit tests → use case → adapters → wiring.
4. Small, focused diffs. No unrelated refactors "while here".
5. Keep `docs/RUN.md` up to date: maintain category command tables (Docker infrastructure, quality/verification commands, application run commands) whenever infrastructure, build scripts, or execution commands are introduced or changed.
6. Before reporting done, run `./mvnw spotless:apply checkstyle:check pmd:check verify` and include the real result. Verify zero Checkstyle errors and no static analysis / PMD / SonarQube quality warnings. Never claim tests pass without running them.
7. No new dependencies without stating why. Never any framework in `domain`.
8. Never delete, weaken, or `@Disabled` tests to make a build green.
9. No hardcoded secrets; use `application-*.yml` + environment variables.
10. Commits use Conventional Commits: `feat(training): …`, `fix(employee): …`, `refactor`, `test`, `chore`, `docs`. Body explains intent and domain reasoning.

## Never do
- Framework annotations (`@Entity`, `@Component`, `@JsonProperty`, `@Valid`, …) on domain classes.
- Expose JPA entities or domain aggregates from controllers or messages.
- Public setters or public no-arg constructors on aggregates.
- Repository access or business logic in controllers.
- H2 in integration tests; `ddl-auto=update` in any profile.
- `Thread.sleep`, `System.out`, or `printStackTrace` in production code.

## Layer rules

Read the rule file matching the files you are touching:

- `**/domain/**/*.java` — @.claude/rules/100-domain.md
- `**/application/**/*.java` — @.claude/rules/200-application.md
- `**/infrastructure/**/*.java`, `**/config/**/*.java`, `**/*Controller.java`, `**/*JpaEntity.java`, `**/*Adapter.java`, `**/db/migration/*.sql` — @.claude/rules/300-infrastructure.md
- `**/src/test/**/*.java` — @.claude/rules/400-testing.md

## Commands and skills

- `/verify` — format, build, run the full test suite, report honestly.
- `/new-use-case` — scaffold a use case end-to-end across all layers.
- `ddd-review` skill — DDD / Clean Architecture review checklist; use it when reviewing code, discussing design, or when asked whether a change fits the architecture.

Rules are mirrored from `.agents/rules/` (Antigravity IDE). Keep both in sync when either changes.
