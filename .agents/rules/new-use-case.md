---
description: Scaffold a new use case end-to-end following DDD + Clean Architecture (domain → application → adapters → tests).
---

# /new-use-case

Input: `<BoundedContext> <VerbNoun>` (e.g. `ordering CancelOrder`) and a short description of the business rule.

1. Read `<context>/README.md` and `doc/GLOSSARY.md`. List the aggregate(s), value objects, and existing use cases involved. Confirm the terms with the user if anything is new.
2. Produce an implementation plan (as an artifact) with sections: Domain changes, Application, Adapters, Migrations, Tests. Wait for approval if invariants, public API, or schema change.
3. Domain: add/modify the aggregate method(s), value objects, and domain event(s). Write pure JUnit tests first; run `./mvnw -q -pl <module> test -Dtest=<AggregateTest>` until green.
4. Application: create `<VerbNoun>Command`, `<VerbNoun>UseCase`, `<VerbNoun>Service` with `@Transactional`; add any missing output ports. Test with in-memory fakes.
5. Adapters: controller endpoint + request/response DTOs, exception mapping, persistence mapping changes, event publisher wiring. `@WebMvcTest` and `@DataJpaTest` (Testcontainers) tests.
6. Migration: add `V<yyyyMMddHHmm>__<description>.sql` if the schema changes.
7. Update ArchUnit rules if new packages were introduced; update `<context>/README.md` and glossary.
8. Run `./mvnw spotless:apply verify`. Report results and produce a walkthrough listing files touched per layer.
9. Propose a Conventional Commit message: `feat(<context>): <summary>`.
